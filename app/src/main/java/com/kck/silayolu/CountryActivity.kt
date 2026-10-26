package com.kck.silayolu

class CountryActivity : Base() {

    override fun build() {
        val c = Data.countries[intent.getStringExtra("code") ?: ""] ?: run { finish(); return }
        header(c.flag + "  " + c.name.toString())

        info(t("💶 Para birimi", "💶 Währung"), c.currency.toString())
        info(t("🛣️ Otoyol ücreti / vinyet", "🛣️ Maut / Vignette"), c.toll.toString())
        info(
            t("🚦 Hız sınırları (km/s)", "🚦 Tempolimits (km/h)"),
            t("Şehir içi / Şehir dışı / Otoyol", "Innerorts / Außerorts / Autobahn") + "\n" + c.speed +
                (c.speedNote?.let { "\n" + it.toString() } ?: "")
        )
        info(t("📞 Acil numaralar", "📞 Notrufnummern"), c.emergency.toString())

        if (c.tips.isNotEmpty()) {
            section(t("İpuçları", "Tipps"))
            root.addView(card().apply {
                c.tips.forEachIndexed { i, tip ->
                    addView(tv("• " + tip.toString(), 15f), if (i == 0) lp() else gap(8))
                }
            }, gap(8))
        }

        root.addView(button("📞 " + t("Ara: ", "Anrufen: ") + c.emergencyNumber) {
            Maps.dial(this, c.emergencyNumber)
        }, gap(18))
        c.link?.let { url ->
            root.addView(ghostButton("🌐 " + t("Resmi site: ", "Offizielle Seite: ") + url.removePrefix("https://").removePrefix("www.")) {
                Maps.web(this, url)
            }, gap(10))
        }
        root.addView(tv(
            t("Kurallar ve ücretler değişebilir; yola çıkmadan resmi siteyi kontrol edin. Her zaman tabelalara uyun.",
              "Regeln und Gebühren können sich ändern; vor der Fahrt die offizielle Seite prüfen. Immer die Schilder beachten."),
            12f, C.MUTED
        ), gap(18))
    }

    private fun info(title: String, body: String) {
        section(title)
        root.addView(card().apply { addView(tv(body, 16f)) }, gap(8))
    }
}
