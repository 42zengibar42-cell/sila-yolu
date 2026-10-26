package com.kck.silayolu

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.widget.CheckBox

class ChecklistActivity : Base() {

    override fun build() {
        val route = Data.route(prefs.route)
        val items = Data.checklistFor(route.id)
        val done = items.count { prefs.checked(it.id) }
        header(t("Kontrol listesi", "Checkliste"))
        root.addView(tv(route.name.toString() + " · $done/${items.size} " + t("hazır", "erledigt"), 14f, C.MUTED), gap(6))

        items.groupBy { it.group.toString() }.forEach { (group, list) ->
            section(group)
            val c = card()
            list.forEach { item ->
                c.addView(CheckBox(this).apply {
                    text = item.text.toString()
                    textSize = 16f
                    setTextColor(C.TEXT)
                    buttonTintList = ColorStateList.valueOf(C.ACCENT)
                    isChecked = prefs.checked(item.id)
                    setPadding(dp(6), dp(8), 0, dp(8))
                    setOnCheckedChangeListener { _, v -> prefs.setChecked(item.id, v) }
                })
            }
            root.addView(c, gap(8))
        }

        root.addView(ghostButton("↺ " + t("Listeyi sıfırla", "Liste zurücksetzen")) {
            AlertDialog.Builder(this)
                .setMessage(t("Tüm işaretler kaldırılsın mı?", "Alle Häkchen entfernen?"))
                .setPositiveButton(t("Evet", "Ja")) { _, _ -> prefs.resetChecks(); recreate() }
                .setNegativeButton(t("Hayır", "Nein"), null)
                .show()
        }, gap(20))
    }
}
