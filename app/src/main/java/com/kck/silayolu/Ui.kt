package com.kck.silayolu

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

object C {
    const val BG = 0xFF0F1B2D.toInt()
    const val CARD = 0xFF182A44.toInt()
    const val HI = 0xFF24406A.toInt()
    const val ACCENT = 0xFFF2A541.toInt()
    const val RED = 0xFFE4572E.toInt()
    const val TEXT = 0xFFF1F4F8.toInt()
    const val MUTED = 0xFF9FB0C8.toInt()
}

object Lang { var tr = true }

/** Ekran: telefon mu tablet mi */
object Screen {
    var wide = false      // en kısa kenar >= 600dp (7–10 inç tablet)
    var scale = 1f        // tablette yazılar biraz büyük
    val cols get() = if (wide) 2 else 1
}

fun t(tr: String, de: String): String = if (Lang.tr) tr else de

class T(val tr: String, val de: String) {
    override fun toString(): String = if (Lang.tr) tr else de
}

class Prefs(ctx: Context) {
    private val sp = ctx.getSharedPreferences("sila", Context.MODE_PRIVATE)
    var lang: String
        get() = sp.getString("lang", "tr") ?: "tr"
        set(v) { sp.edit().putString("lang", v).apply() }
    var dest: String
        get() = sp.getString("dest", "İstanbul") ?: "İstanbul"
        set(v) { sp.edit().putString("dest", v).apply() }
    var route: String
        get() = sp.getString("route", "A") ?: "A"
        set(v) { sp.edit().putString("route", v).apply() }
    fun checked(id: String) = sp.getBoolean("c_$id", false)
    fun setChecked(id: String, v: Boolean) { sp.edit().putBoolean("c_$id", v).apply() }
    var nick: String
        get() = sp.getString("nick", "") ?: ""
        set(v) { sp.edit().putString("nick", v).apply() }
    var lastSent: Long
        get() = sp.getLong("lastSent", 0L)
        set(v) { sp.edit().putLong("lastSent", v).apply() }
    var addPlace: Boolean
        get() = sp.getBoolean("addPlace", true)
        set(v) { sp.edit().putBoolean("addPlace", v).apply() }
    val blocked: Set<String> get() = sp.getStringSet("blocked", emptySet()) ?: emptySet()
    fun block(uid: String) { sp.edit().putStringSet("blocked", blocked + uid).apply() }
    fun resetChecks() {
        val e = sp.edit()
        sp.all.keys.filter { it.startsWith("c_") }.forEach { e.remove(it) }
        e.apply()
    }
}

fun Context.dp(v: Int): Int = (v * resources.displayMetrics.density + 0.5f).toInt()

fun Context.tv(s: String, sp: Float = 16f, color: Int = C.TEXT, bold: Boolean = false): TextView =
    TextView(this).apply {
        text = s; textSize = sp * Screen.scale; setTextColor(color)
        if (bold) setTypeface(typeface, Typeface.BOLD)
    }

fun roundBg(color: Int, radius: Float, stroke: Int = 0): GradientDrawable =
    GradientDrawable().apply {
        setColor(color); cornerRadius = radius
        if (stroke != 0) setStroke(3, stroke)
    }

fun Context.card(): LinearLayout = LinearLayout(this).apply {
    orientation = LinearLayout.VERTICAL
    background = roundBg(C.CARD, dp(16).toFloat())
    setPadding(dp(16), dp(14), dp(16), dp(14))
}

fun Context.button(s: String, onClick: () -> Unit): TextView = tv(s, 16f, C.BG, true).apply {
    gravity = Gravity.CENTER
    background = roundBg(C.ACCENT, dp(12).toFloat())
    setPadding(dp(10), dp(14), dp(10), dp(14))
    setOnClickListener { onClick() }
}

fun Context.ghostButton(s: String, onClick: () -> Unit): TextView = tv(s, 15f, C.ACCENT, true).apply {
    gravity = Gravity.CENTER
    background = roundBg(C.CARD, dp(12).toFloat(), C.ACCENT)
    setPadding(dp(10), dp(12), dp(10), dp(12))
    setOnClickListener { onClick() }
}

fun lp(w: Int = LinearLayout.LayoutParams.MATCH_PARENT, h: Int = LinearLayout.LayoutParams.WRAP_CONTENT, weight: Float = 0f) =
    LinearLayout.LayoutParams(w, h, weight)

fun Context.gap(v: Int): LinearLayout.LayoutParams = lp().apply { topMargin = dp(v) }

/** Görünümleri n sütunlu satırlara dizer */
fun Context.columns(views: List<View>, cols: Int, space: Int = 8): LinearLayout {
    val box = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
    views.chunked(cols).forEachIndexed { i, chunk ->
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        for (j in 0 until cols) {
            val v = chunk.getOrNull(j) ?: View(this)
            row.addView(v, lp(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f).apply {
                if (j > 0) leftMargin = dp(space)
            })
        }
        box.addView(row, lp().apply { if (i > 0) topMargin = dp(space) })
    }
    return box
}

/** Düğme ızgarası: telefonda 2, tablette 4 sütun */
fun Context.grid(items: List<Pair<String, () -> Unit>>): LinearLayout =
    columns(items.map { (label, action) -> ghostButton(label, action) }, if (Screen.wide) 4 else 2)

abstract class Base : AppCompatActivity() {
    lateinit var prefs: Prefs
    lateinit var root: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs(this)
    }

    override fun onResume() {
        super.onResume()
        Lang.tr = prefs.lang == "tr"
        val cfg = resources.configuration
        Screen.wide = cfg.smallestScreenWidthDp >= 600
        Screen.scale = when {
            cfg.smallestScreenWidthDp >= 720 -> 1.25f   // 10 inç tablet
            cfg.smallestScreenWidthDp >= 600 -> 1.15f   // 7–8 inç tablet
            else -> 1f
        }
        val side = if (Screen.wide) 28 else 16
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(side), dp(18), dp(side), dp(28))
        }
        // Çok geniş ekranda (yatay tablet) içerik ortada, okunur genişlikte durur
        val maxW = dp(if (Screen.wide) 1000 else 10000)
        val frame = FrameLayout(this).apply {
            addView(root, FrameLayout.LayoutParams(
                minOf(maxW, resources.displayMetrics.widthPixels),
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL
            ))
        }
        val scroll = ScrollView(this).apply {
            setBackgroundColor(C.BG)
            isFillViewport = true
            addView(frame)
        }
        build()
        setContentView(scroll)
    }

    abstract fun build()

    fun header(title: String) {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(tv("‹", 34f, C.ACCENT, true).apply {
            setPadding(0, 0, dp(14), dp(4))
            setOnClickListener { finish() }
        })
        row.addView(tv(title, 22f, C.TEXT, true), lp(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        root.addView(row)
    }

    fun section(title: String) {
        root.addView(tv(title, 13f, C.MUTED, true).apply { isAllCaps = true }, gap(20))
    }
}

object Maps {
    private const val PKG = "com.google.android.apps.maps"

    private fun open(ctx: Context, uri: Uri): Boolean {
        val i = Intent(Intent.ACTION_VIEW, uri).setPackage(PKG)
        return try { ctx.startActivity(i); true } catch (e: ActivityNotFoundException) {
            try { ctx.startActivity(Intent(Intent.ACTION_VIEW, uri)); true } catch (e2: ActivityNotFoundException) { false }
        }
    }

    /** Tüm güzergah: bulunduğun yerden ara duraklar üzerinden hedefe */
    fun route(ctx: Context, waypoints: List<String>, dest: String) {
        val b = Uri.parse("https://www.google.com/maps/dir/").buildUpon()
            .appendQueryParameter("api", "1")
            .appendQueryParameter("destination", dest)
            .appendQueryParameter("travelmode", "driving")
        if (waypoints.isNotEmpty()) b.appendQueryParameter("waypoints", waypoints.joinToString("|"))
        if (!open(ctx, b.build())) fail(ctx)
    }

    /** Tek bir yere doğrudan navigasyon */
    fun navigate(ctx: Context, q: String) {
        val i = Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + Uri.encode(q))).setPackage(PKG)
        try { ctx.startActivity(i) } catch (e: ActivityNotFoundException) { route(ctx, emptyList(), q) }
    }

    /** Yakındaki yerleri ara */
    fun nearby(ctx: Context, q: String) {
        if (!open(ctx, Uri.parse("geo:0,0?q=" + Uri.encode(q)))) fail(ctx)
    }

    fun web(ctx: Context, url: String) {
        try { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) } catch (e: ActivityNotFoundException) { fail(ctx) }
    }

    fun dial(ctx: Context, num: String) {
        try { ctx.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$num"))) } catch (e: ActivityNotFoundException) { fail(ctx) }
    }

    private fun fail(ctx: Context) {
        Toast.makeText(ctx, t("Uygun uygulama bulunamadı", "Keine passende App gefunden"), Toast.LENGTH_LONG).show()
    }
}
