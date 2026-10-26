package com.kck.silayolu

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.text.InputFilter
import android.view.Gravity
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.Locale

/* ---------------- Odalar ---------------- */

class Room(val id: String, val icon: String, val name: T, val sub: T)

object Rooms {
    const val KEEP_HOURS = 6L   // bu kadar saatten eski bildirimler gösterilmez

    val routeRooms: List<Room> = Data.routes.map { r ->
        Room("route_${r.id}", "🛣️", r.name, T("Güzergah odası", "Routen-Raum"))
    }

    val borderRooms: List<Room> = listOf(
        Room("kapikule", "🇧🇬🇹🇷", T("Kapıkule", "Kapıkule"), T("Bulgaristan – Türkiye", "Bulgarien – Türkei")),
        Room("ipsala", "🇬🇷🇹🇷", T("İpsala", "İpsala"), T("Yunanistan – Türkiye", "Griechenland – Türkei")),
        Room("gradina", "🇷🇸🇧🇬", T("Gradina – Kalotina", "Gradina – Kalotina"), T("Sırbistan – Bulgaristan", "Serbien – Bulgarien")),
        Room("roszke", "🇭🇺🇷🇸", T("Röszke – Horgoš", "Röszke – Horgoš"), T("Macaristan – Sırbistan", "Ungarn – Serbien")),
        Room("batrovci", "🇭🇷🇷🇸", T("Bajakovo – Batrovci", "Bajakovo – Batrovci"), T("Hırvatistan – Sırbistan", "Kroatien – Serbien")),
        Room("nadlac", "🇭🇺🇷🇴", T("Nădlac", "Nădlac"), T("Macaristan – Romanya", "Ungarn – Rumänien")),
        Room("ruse", "🇷🇴🇧🇬", T("Giurgiu – Ruse", "Giurgiu – Ruse"), T("Romanya – Bulgaristan", "Rumänien – Bulgarien"))
    )

    val all: List<Room> get() = routeRooms + borderRooms
    fun byId(id: String): Room? = all.firstOrNull { it.id == id }

    /** Bildirim türleri */
    class Kind(val id: String, val emoji: String, val label: T)

    val kinds = listOf(
        Kind("traffic", "🚗", T("Yoğunluk", "Stau")),
        Kind("wait", "⏳", T("Sınırda bekleme", "Wartezeit Grenze")),
        Kind("accident", "⚠️", T("Kaza", "Unfall")),
        Kind("works", "🚧", T("Yol çalışması", "Baustelle")),
        Kind("snow", "🌨️", T("Kar / buz", "Schnee / Glätte")),
        Kind("clear", "✅", T("Yol açık", "Freie Fahrt"))
    )
    private val msgKind = Kind("msg", "💬", T("Mesaj", "Nachricht"))
    private val waits = listOf(30, 60, 120, 180)

    fun kindOf(type: String): Kind =
        kinds.firstOrNull { type == it.id || type.startsWith(it.id + "_") } ?: msgKind

    fun title(type: String): String {
        val k = kindOf(type)
        val mins = type.substringAfter("wait_", "").toIntOrNull()
        val extra = when {
            mins == null -> ""
            mins >= 180 -> t(" · 3+ saat", " · 3+ Std.")
            mins >= 60 -> t(" · ~${mins / 60} saat", " · ~${mins / 60} Std.")
            else -> t(" · ~$mins dk", " · ~$mins Min.")
        }
        return k.emoji + " " + k.label.toString() + extra
    }

    fun waitOptions(): List<Pair<String, String>> = waits.map { m ->
        val label = when {
            m >= 180 -> t("3 saatten fazla", "mehr als 3 Std.")
            m >= 60 -> t("yaklaşık ${m / 60} saat", "etwa ${m / 60} Std.")
            else -> t("yaklaşık $m dakika", "etwa $m Minuten")
        }
        "wait_$m" to label
    }

    fun ago(ts: Timestamp?): String {
        if (ts == null) return t("şimdi", "jetzt")
        val min = ((System.currentTimeMillis() - ts.toDate().time) / 60000).coerceAtLeast(0)
        return when {
            min < 1 -> t("şimdi", "jetzt")
            min < 60 -> t("$min dk önce", "vor $min Min.")
            else -> t("${min / 60} saat ${min % 60} dk önce", "vor ${min / 60} Std. ${min % 60} Min.")
        }
    }
}

/** Anonim giriş: kullanıcıdan bir şey istemez */
object Auth {
    fun ensure(ctx: Context, ok: (String) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.let { ok(it.uid); return }
        auth.signInAnonymously()
            .addOnSuccessListener { r -> r.user?.uid?.let(ok) }
            .addOnFailureListener {
                Toast.makeText(ctx, t("Bağlanılamadı. İnternet bağlantısını kontrol edin.",
                    "Keine Verbindung. Bitte Internet prüfen."), Toast.LENGTH_LONG).show()
            }
    }
}

/* ---------------- Oda listesi ---------------- */

class RoomsActivity : Base() {
    override fun build() {
        header("💬 " + t("Yol bildirimleri", "Straßenmeldungen"))
        root.addView(tv(
            t("Yoğunluk, sınır bekleme süresi, kaza ve yol durumunu birbirinize bildirin. Bildirimler ${Rooms.KEEP_HOURS} saat sonra kaybolur.",
              "Meldet euch gegenseitig Stau, Wartezeiten an der Grenze, Unfälle und Straßenzustand. Meldungen verschwinden nach ${Rooms.KEEP_HOURS} Stunden."),
            14f, C.MUTED
        ), gap(6))

        section(t("Güzergah odaları", "Routen-Räume"))
        val mine = "route_" + prefs.route
        val sorted = Rooms.routeRooms.sortedBy { if (it.id == mine) 0 else 1 }
        root.addView(columns(sorted.map { roomCard(it, it.id == mine) }, Screen.cols), gap(8))

        section(t("Sınır kapıları", "Grenzübergänge"))
        root.addView(columns(Rooms.borderRooms.map { roomCard(it, false) }, Screen.cols), gap(8))
    }

    private fun roomCard(r: Room, highlight: Boolean): LinearLayout {
        val c = card().apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            if (highlight) background = roundBg(C.HI, dp(16).toFloat(), C.ACCENT)
            setOnClickListener {
                startActivity(Intent(this@RoomsActivity, RoomActivity::class.java).putExtra("room", r.id))
            }
        }
        c.addView(tv(r.icon, 20f))
        val txt = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, 0, 0) }
        txt.addView(tv(r.name.toString(), 16f, if (highlight) C.ACCENT else C.TEXT, true))
        txt.addView(tv(r.sub.toString() + if (highlight) t(" · sizin güzergahınız", " · Ihre Route") else "", 13f, C.MUTED))
        c.addView(txt, lp(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        c.addView(tv("›", 26f, C.ACCENT, true))
        return c
    }
}

/* ---------------- Oda ---------------- */

class RoomActivity : Base() {
    private val db by lazy { FirebaseFirestore.getInstance() }
    private var listener: ListenerRegistration? = null
    private lateinit var room: Room
    private lateinit var list: LinearLayout
    private lateinit var status: TextView
    private lateinit var noteBox: EditText
    private var myUid: String? = null
    private var docs: List<DocumentSnapshot> = emptyList()
    private val handler = Handler(Looper.getMainLooper())
    private val refresh = object : Runnable {
        override fun run() { render(); handler.postDelayed(this, 60_000) }
    }

    private val msgs get() = db.collection("rooms").document(room.id).collection("msgs")

    override fun build() {
        room = Rooms.byId(intent.getStringExtra("room") ?: "") ?: run { finish(); return }
        header(room.icon + "  " + room.name.toString())
        root.addView(tv(room.sub.toString(), 14f, C.MUTED), gap(2))

        // Sürüş uyarısı
        root.addView(tv(
            "🚘 " + t("Araç kullanırken yazmayın. Bildirimi yolcu göndersin veya mola verince gönderin.",
                "Nicht während der Fahrt tippen. Meldungen vom Beifahrer oder in der Pause senden."),
            13f, C.TEXT
        ).apply {
            background = roundBg(0xFF5A3A1A.toInt(), dp(12).toFloat())
            setPadding(dp(12), dp(10), dp(12), dp(10))
        }, gap(10))

        // Hızlı bildirimler
        section(t("Hızlı bildirim", "Schnellmeldung"))
        root.addView(grid(Rooms.kinds.map { k ->
            (k.emoji + " " + k.label.toString()) to { quick(k.id) }
        }), gap(8))

        // Not
        noteBox = EditText(this).apply {
            hint = t("Kısa not (isteğe bağlı)", "Kurze Notiz (optional)")
            setTextColor(C.TEXT); setHintTextColor(C.MUTED); textSize = 16f * Screen.scale
            background = roundBg(C.CARD, dp(12).toFloat())
            setPadding(dp(14), dp(12), dp(14), dp(12))
            filters = arrayOf(InputFilter.LengthFilter(160))
            maxLines = 3
        }
        root.addView(noteBox, gap(12))
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        row.addView(CheckBox(this).apply {
            text = t("Yer adı ekle (ör. \"Niš yakını\")", "Ortsnamen hinzufügen (z. B. \"bei Niš\")")
            setTextColor(C.MUTED); textSize = 14f * Screen.scale
            buttonTintList = ColorStateList.valueOf(C.ACCENT)
            isChecked = prefs.addPlace
            setOnCheckedChangeListener { _, v ->
                prefs.addPlace = v
                if (v) askLocation()
            }
        }, lp(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(tv(t("Gönder", "Senden"), 15f, C.BG, true).apply {
            gravity = Gravity.CENTER
            background = roundBg(C.ACCENT, dp(12).toFloat())
            setPadding(dp(18), dp(10), dp(18), dp(10))
            setOnClickListener {
                val n = noteBox.text.toString().trim()
                if (n.isEmpty()) toast(t("Önce bir not yazın veya hızlı bildirim seçin.", "Erst Notiz schreiben oder Schnellmeldung wählen."))
                else send("msg", n)
            }
        })
        root.addView(row, gap(6))

        // Liste
        section(t("Son bildirimler", "Letzte Meldungen"))
        status = tv(t("Yükleniyor…", "Wird geladen…"), 14f, C.MUTED)
        root.addView(status, gap(6))
        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(list, gap(4))
        root.addView(tv(
            t("Bir bildirime uzun basarak şikâyet edebilir, gönderen kişiyi engelleyebilir veya kendi bildiriminizi silebilirsiniz. Bilgiler kullanıcılardan gelir; doğruluğu garanti edilmez.",
              "Lange auf eine Meldung drücken, um sie zu melden, den Absender zu blockieren oder die eigene Meldung zu löschen. Die Angaben stammen von Nutzern und sind ohne Gewähr."),
            12f, C.MUTED
        ), gap(18))
    }

    override fun onResume() {
        super.onResume()
        if (!::room.isInitialized || isFinishing) return
        Auth.ensure(this) { uid ->
            myUid = uid
            listen()
            cleanup()
        }
        handler.removeCallbacks(refresh); handler.postDelayed(refresh, 60_000)
    }

    override fun onPause() {
        super.onPause()
        listener?.remove(); listener = null
        handler.removeCallbacks(refresh)
    }

    private fun cutoff() = Timestamp(java.util.Date(System.currentTimeMillis() - Rooms.KEEP_HOURS * 3600_000L))

    private fun listen() {
        listener?.remove()
        listener = msgs.whereGreaterThan("ts", cutoff())
            .orderBy("ts", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    status.text = t("Bildirimler alınamadı.", "Meldungen konnten nicht geladen werden.")
                    return@addSnapshotListener
                }
                docs = snap?.documents ?: emptyList()
                render()
            }
    }

    /** Eski bildirimleri sil (sunucu kodu olmadan temizlik) */
    private fun cleanup() {
        val old = Timestamp(java.util.Date(System.currentTimeMillis() - 12 * 3600_000L))
        msgs.whereLessThan("ts", old).limit(20).get().addOnSuccessListener { s ->
            s.documents.forEach { it.reference.delete() }
        }
    }

    private fun render() {
        if (!::list.isInitialized) return
        list.removeAllViews()
        val blocked = prefs.blocked
        val shown = docs.filter { (it.getString("uid") ?: "") !in blocked }
        status.text = if (shown.isEmpty())
            t("Son ${Rooms.KEEP_HOURS} saatte bildirim yok. İlk bildirimi siz gönderin!",
              "Keine Meldungen in den letzten ${Rooms.KEEP_HOURS} Stunden. Senden Sie die erste!")
        else t("${shown.size} bildirim", "${shown.size} Meldungen")
        shown.forEach { d -> list.addView(item(d), gap(8)) }
    }

    private fun item(d: DocumentSnapshot): LinearLayout {
        val type = d.getString("type") ?: "msg"
        val note = d.getString("note") ?: ""
        val place = d.getString("place") ?: ""
        val nick = d.getString("nick") ?: "?"
        val uid = d.getString("uid") ?: ""
        val mine = uid == myUid
        val c = card()
        if (mine) c.background = roundBg(C.HI, dp(16).toFloat())
        val top = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        top.addView(tv(Rooms.title(type), 16f, C.TEXT, true), lp(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        top.addView(tv(Rooms.ago(d.getTimestamp("ts")), 12f, C.MUTED))
        c.addView(top)
        if (note.isNotEmpty()) c.addView(tv(note, 15f), gap(4))
        val meta = (if (place.isNotEmpty()) "📍 " + t("$place yakını", "bei $place") + "  ·  " else "") + "👤 " + nick +
            if (mine) t(" (siz)", " (Sie)") else ""
        c.addView(tv(meta, 13f, C.MUTED), gap(4))
        c.setOnLongClickListener { menu(d, mine, uid, nick); true }
        return c
    }

    private fun menu(d: DocumentSnapshot, mine: Boolean, uid: String, nick: String) {
        if (mine) {
            AlertDialog.Builder(this)
                .setMessage(t("Bu bildirim silinsin mi?", "Diese Meldung löschen?"))
                .setPositiveButton(t("Sil", "Löschen")) { _, _ -> d.reference.delete() }
                .setNegativeButton(t("Vazgeç", "Abbrechen"), null).show()
            return
        }
        val opts = arrayOf(t("Uygunsuz diye şikâyet et", "Als unangemessen melden"), t("$nick kişisini engelle", "$nick blockieren"))
        AlertDialog.Builder(this).setItems(opts) { _, i ->
            if (i == 0) {
                Auth.ensure(this) { me ->
                    db.collection("flags").add(mapOf(
                        "room" to room.id, "msg" to d.id, "uid" to me, "ts" to FieldValue.serverTimestamp()
                    ))
                    toast(t("Teşekkürler, şikâyetiniz iletildi.", "Danke, die Meldung wurde übermittelt."))
                }
            } else {
                prefs.block(uid); render()
                toast(t("$nick engellendi.", "$nick wurde blockiert."))
            }
        }.show()
    }

    /* ---------- Gönderme ---------- */

    private fun quick(kind: String) {
        if (kind == "wait") {
            val opts = Rooms.waitOptions()
            AlertDialog.Builder(this)
                .setTitle(t("Sınırda bekleme süresi", "Wartezeit an der Grenze"))
                .setItems(opts.map { it.second }.toTypedArray()) { _, i -> confirm(opts[i].first) }
                .show()
        } else confirm(kind)
    }

    private fun confirm(type: String) {
        val n = noteBox.text.toString().trim()
        AlertDialog.Builder(this)
            .setTitle(t("Gönderilsin mi?", "Senden?"))
            .setMessage(Rooms.title(type) + if (n.isNotEmpty()) "\n$n" else "")
            .setPositiveButton(t("Gönder", "Senden")) { _, _ -> send(type, n) }
            .setNegativeButton(t("Vazgeç", "Abbrechen"), null).show()
    }

    private fun send(type: String, note: String) {
        if (prefs.nick.isBlank()) { askNick { send(type, note) }; return }
        val wait = 60_000L - (System.currentTimeMillis() - prefs.lastSent)
        if (wait > 0) {
            toast(t("Lütfen ${wait / 1000 + 1} saniye bekleyin.", "Bitte ${wait / 1000 + 1} Sekunden warten."))
            return
        }
        Auth.ensure(this) { uid ->
            placeName { place ->
                val data = hashMapOf<String, Any>(
                    "type" to type, "note" to note, "place" to place,
                    "nick" to prefs.nick, "uid" to uid, "ts" to FieldValue.serverTimestamp()
                )
                msgs.add(data)
                    .addOnSuccessListener { toast(t("Gönderildi, teşekkürler!", "Gesendet, danke!")) }
                    .addOnFailureListener { toast(t("Gönderilemedi.", "Senden fehlgeschlagen.")) }
                prefs.lastSent = System.currentTimeMillis()
                noteBox.setText("")
            }
        }
    }

    private fun askNick(then: () -> Unit) {
        val box = EditText(this).apply {
            hint = t("ör. Ahmet_Frankfurt", "z. B. Ahmet_Frankfurt")
            filters = arrayOf(InputFilter.LengthFilter(24))
            isSingleLine = true
        }
        val wrap = LinearLayout(this).apply { setPadding(dp(20), dp(8), dp(20), 0); addView(box, lp()) }
        AlertDialog.Builder(this)
            .setTitle(t("Takma adınız", "Ihr Spitzname"))
            .setMessage(t("Bildirimlerde bu ad görünür. Gerçek adınızı yazmanız gerekmez.",
                "Dieser Name steht bei Ihren Meldungen. Sie müssen nicht Ihren echten Namen angeben."))
            .setView(wrap)
            .setPositiveButton(t("Kaydet", "Speichern")) { _, _ ->
                val v = box.text.toString().trim()
                if (v.length >= 2) { prefs.nick = v; then() }
                else toast(t("En az 2 harf yazın.", "Mindestens 2 Zeichen."))
            }
            .setNegativeButton(t("Vazgeç", "Abbrechen"), null).show()
    }

    /* ---------- Yer adı ---------- */

    private fun hasLoc() = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun askLocation() {
        if (!hasLoc()) requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 7)
    }

    @SuppressLint("MissingPermission")
    private fun placeName(done: (String) -> Unit) {
        if (!prefs.addPlace || !hasLoc()) { done(""); return }
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var best: Location? = null
        for (p in listOf(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER, LocationManager.PASSIVE_PROVIDER)) {
            val l = try { lm.getLastKnownLocation(p) } catch (e: Exception) { null } ?: continue
            if (best == null || l.time > best.time) best = l
        }
        val loc = best
        // 2 saatten eski konum yanıltıcı olur
        if (loc == null || System.currentTimeMillis() - loc.time > 2 * 3600_000L) { done(""); return }
        Thread {
            val name = try {
                @Suppress("DEPRECATION")
                val a = Geocoder(this, Locale.getDefault()).getFromLocation(loc.latitude, loc.longitude, 1)?.firstOrNull()
                val city = a?.locality ?: a?.subAdminArea ?: a?.adminArea ?: ""
                if (city.isEmpty()) "" else (city + (a?.countryCode?.let { ", $it" } ?: "")).take(60)
            } catch (e: Exception) { "" }
            runOnUiThread { done(name) }
        }.start()
    }

    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_LONG).show()
}
