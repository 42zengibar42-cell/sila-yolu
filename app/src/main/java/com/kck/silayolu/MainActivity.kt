package com.kck.silayolu

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout

class MainActivity : Base() {

    override fun build() {
        // Başlık + dil seçimi
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val titles = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        titles.addView(tv("🚗 " + t("Sıla Yolu", "Heimreise"), 26f, C.TEXT, true))
        titles.addView(tv(t("Avrupa'dan Türkiye'ye arabayla", "Mit dem Auto von Europa in die Türkei"), 14f, C.MUTED))
        top.addView(titles, lp(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        top.addView(tv(if (Lang.tr) "DE" else "TR", 15f, C.ACCENT, true).apply {
            gravity = Gravity.CENTER
            background = roundBg(C.CARD, dp(10).toFloat(), C.ACCENT)
            setPadding(dp(14), dp(8), dp(14), dp(8))
            setOnClickListener {
                prefs.lang = if (Lang.tr) "de" else "tr"
                recreate()
            }
        })
        root.addView(top)

        // Hedef şehir
        section(t("Hedef (Türkiye'de)", "Ziel (in der Türkei)"))
        val dest = EditText(this).apply {
            setText(prefs.dest)
            setTextColor(C.TEXT); setHintTextColor(C.MUTED); textSize = 18f * Screen.scale
            hint = t("Şehir veya adres", "Stadt oder Adresse")
            isSingleLine = true
            imeOptions = EditorInfo.IME_ACTION_DONE
            background = roundBg(C.CARD, dp(12).toFloat())
            setPadding(dp(14), dp(12), dp(14), dp(12))
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: Editable?) { prefs.dest = s?.toString()?.trim() ?: "" }
            })
        }
        root.addView(dest, gap(8))

        // Güzergah seçimi
        section(t("Güzergah", "Route"))
        val selected = prefs.route
        val routeCards = Data.routes.map { r ->
            val on = r.id == selected
            val c = card().apply {
                background = roundBg(if (on) C.HI else C.CARD, dp(14).toFloat(), if (on) C.ACCENT else 0)
                setOnClickListener { prefs.route = r.id; recreate() }
            }
            c.addView(tv((if (on) "● " else "○ ") + r.name.toString(), 17f, if (on) C.ACCENT else C.TEXT, true))
            c.addView(tv(r.countries.joinToString(" ") { Data.countries[it]?.flag ?: "" }, 20f), gap(4))
            c.addView(tv(r.desc.toString(), 14f, C.MUTED), gap(2))
            c
        }
        root.addView(columns(routeCards, Screen.cols), gap(8))

        val route = Data.route(selected)
        root.addView(button("🧭 " + t("Google Maps ile yola çık", "Mit Google Maps losfahren")) {
            Maps.route(this, route.mapWaypoints, destination())
        }, gap(16))
        val items = Data.checklistFor(selected)
        val done = items.count { prefs.checked(it.id) }
        root.addView(columns(listOf(
            ghostButton("🗺️ " + t("Etaplar ve ülke bilgileri", "Etappen und Länderinfos")) {
                startActivity(Intent(this, RouteActivity::class.java))
            },
            ghostButton("✅ " + t("Kontrol listesi", "Checkliste") + "  ($done/${items.size})") {
                startActivity(Intent(this, ChecklistActivity::class.java))
            }
        ), Screen.cols, 10), gap(10))

        // Yakında ara
        section(t("Yakınımda ara", "In meiner Nähe"))
        root.addView(grid(listOf(
            "⛽ " + t("Benzinlik", "Tankstelle") to { Maps.nearby(this, "gas station") },
            "🛏️ " + t("Motel / Otel", "Motel / Hotel") to { Maps.nearby(this, "motel hotel") },
            "🍽️ " + t("Restoran", "Restaurant") to { Maps.nearby(this, "restaurant") },
            "🅿️ " + t("Dinlenme yeri", "Rastplatz") to { Maps.nearby(this, "rest area") },
            "🏧 " + t("ATM", "Geldautomat") to { Maps.nearby(this, "ATM") },
            "💊 " + t("Eczane", "Apotheke") to { Maps.nearby(this, "pharmacy") },
            "🏥 " + t("Hastane", "Krankenhaus") to { Maps.nearby(this, "hospital") },
            "🔧 " + t("Oto tamir", "Autowerkstatt") to { Maps.nearby(this, "car repair") }
        )), gap(8))

        // Acil
        section(t("Acil durum", "Notfall"))
        root.addView(tv("📞 112 — " + t("Acil (AB ülkeleri ve Türkiye)", "Notruf (EU-Länder und Türkei)"), 17f, C.TEXT, true).apply {
            gravity = Gravity.CENTER
            background = roundBg(C.RED, dp(12).toFloat())
            setPadding(dp(10), dp(14), dp(10), dp(14))
            setOnClickListener { Maps.dial(this@MainActivity, "112") }
        }, gap(8))
        root.addView(tv(t("Sırbistan: Polis 192 · Ambulans 194", "Serbien: Polizei 192 · Rettung 194"), 13f, C.MUTED).apply {
            gravity = Gravity.CENTER
        }, gap(6))

        root.addView(tv(
            t("Bilgiler genel rehberdir; kurallar ve ücretler değişebilir. Yola çıkmadan resmi siteleri kontrol edin.",
              "Die Angaben sind ein allgemeiner Leitfaden; Regeln und Gebühren können sich ändern. Vor der Fahrt die offiziellen Seiten prüfen."),
            12f, C.MUTED
        ), gap(22))
    }

    private fun destination(): String {
        val d = prefs.dest.ifBlank { "İstanbul" }
        return if (d.contains("Türkiye", true) || d.contains("Turkey", true) || d.contains("Türkei", true)) d else "$d, Türkiye"
    }
}
