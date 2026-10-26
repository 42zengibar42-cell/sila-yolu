package com.kck.silayolu

import android.content.Intent
import android.view.Gravity
import android.widget.LinearLayout

class RouteActivity : Base() {

    override fun build() {
        val r = Data.route(prefs.route)
        header(r.name.toString())
        root.addView(tv(r.desc.toString(), 14f, C.MUTED), gap(6))

        // Ülkeler
        section(t("Geçilen ülkeler", "Durchfahrene Länder"))
        r.countries.forEach { code ->
            val c = Data.countries[code] ?: return@forEach
            val row = card().apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setOnClickListener {
                    startActivity(Intent(this@RouteActivity, CountryActivity::class.java).putExtra("code", code))
                }
            }
            row.addView(tv(c.flag, 26f))
            val txt = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, 0, 0) }
            txt.addView(tv(c.name.toString(), 17f, C.TEXT, true))
            txt.addView(tv(c.currency.toString() + " · " + c.speed + t(" km/s", " km/h"), 13f, C.MUTED))
            row.addView(txt, lp(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            row.addView(tv("›", 26f, C.ACCENT, true))
            root.addView(row, gap(8))
        }

        // Etaplar
        section(t("Etaplar — tek tek navigasyon", "Etappen — einzeln navigieren"))
        root.addView(tv(
            t("Her etabın yanındaki ▶ düğmesi Google Maps navigasyonunu doğrudan o noktaya başlatır.",
              "Die ▶-Taste neben jeder Etappe startet die Google-Maps-Navigation direkt dorthin."),
            13f, C.MUTED
        ), gap(6))

        val stops = r.stops + Stop(T("🏁 " + prefs.dest.ifBlank { "İstanbul" }, "🏁 " + prefs.dest.ifBlank { "İstanbul" }),
            prefs.dest.ifBlank { "İstanbul" } + ", Türkiye")
        stops.forEachIndexed { i, s -> root.addView(stopRow(i + 1, s), gap(8)) }

        // Notlar
        section(t("Bu güzergah için", "Für diese Route"))
        r.notes.forEach { n ->
            root.addView(card().apply { addView(tv("• " + n.toString(), 15f)) }, gap(8))
        }

        root.addView(button("🧭 " + t("Tüm güzergahı Google Maps'te aç", "Ganze Route in Google Maps öffnen")) {
            Maps.route(this, r.mapWaypoints, stops.last().query)
        }, gap(18))
    }

    private fun stopRow(n: Int, s: Stop): LinearLayout {
        val row = card().apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        row.addView(tv("$n", 15f, C.BG, true).apply {
            gravity = Gravity.CENTER
            background = roundBg(C.ACCENT, dp(14).toFloat())
        }, LinearLayout.LayoutParams(dp(28), dp(28)))
        val txt = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(12), 0, dp(8), 0) }
        txt.addView(tv(s.label.toString(), 16f, C.TEXT, true))
        s.note?.let { txt.addView(tv(it.toString(), 13f, C.MUTED)) }
        row.addView(txt, lp(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(tv("▶", 18f, C.BG, true).apply {
            gravity = Gravity.CENTER
            background = roundBg(C.ACCENT, dp(10).toFloat())
            setPadding(dp(14), dp(8), dp(14), dp(8))
            setOnClickListener { Maps.navigate(this@RouteActivity, s.query) }
        })
        return row
    }
}
