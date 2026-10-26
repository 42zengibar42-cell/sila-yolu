package com.kck.silayolu

class Country(
    val code: String,
    val flag: String,
    val name: T,
    val currency: T,
    val toll: T,
    val speed: String,          // şehir içi / şehir dışı / otoyol
    val speedNote: T?,
    val emergency: T,
    val emergencyNumber: String,
    val link: String?,
    val tips: List<T>
)

class Stop(val label: T, val query: String, val note: T? = null)

class Route(
    val id: String,
    val name: T,
    val desc: T,
    val countries: List<String>,
    val stops: List<Stop>,
    val mapWaypoints: List<String>,
    val notes: List<T>
)

class CheckItem(val id: String, val group: T, val text: T, val routes: Set<String>? = null)

object Data {

    val countries: Map<String, Country> = listOf(
        Country(
            "DE", "🇩🇪", T("Almanya", "Deutschland"), T("Euro (€)", "Euro (€)"),
            T("Otomobiller için otoyol ücreti veya vinyet yok.",
              "Für Pkw keine Maut und keine Vignette auf Autobahnen."),
            "50 / 100 / —",
            T("Otoyolda genel hız sınırı yok; tavsiye edilen 130 km/s.",
              "Auf der Autobahn kein generelles Tempolimit; Richtgeschwindigkeit 130 km/h."),
            T("Acil: 112 · Polis: 110", "Notruf: 112 · Polizei: 110"), "112", null,
            listOf(
                T("Yola çıkmadan önce aracın bakımını yaptırın, lastikleri kontrol edin.",
                  "Vor der Abfahrt Auto warten lassen und Reifen prüfen."),
                T("Uzun yolda yaklaşık her 2 saatte bir mola verin.",
                  "Auf langen Strecken etwa alle 2 Stunden eine Pause machen.")
            )
        ),
        Country(
            "AT", "🇦🇹", T("Avusturya", "Österreich"), T("Euro (€)", "Euro (€)"),
            T("Otoyol ve hızlı yollar için vinyet zorunlu (dijital vinyet, plakaya kayıtlı). Bazı tüneller ve yollar için ayrıca geçiş ücreti var (ör. Brenner otoyolu A13, Tauern ve Karawanken tünelleri).",
              "Vignettenpflicht auf Autobahnen und Schnellstraßen (digitale Vignette, an das Kennzeichen gebunden). Für einige Tunnel und Strecken gilt zusätzlich Streckenmaut (z. B. Brenner A13, Tauern- und Karawankentunnel)."),
            "50 / 100 / 130", null,
            T("Acil: 112 · Polis: 133 · Ambulans: 144", "Notruf: 112 · Polizei: 133 · Rettung: 144"), "112",
            "https://www.asfinag.at",
            listOf(
                T("ASFINAG sitesinden internetten alınan dijital vinyet ancak 18. günden itibaren geçerli olur. Ya en az 3 hafta önceden alın ya da benzinlik/satış noktalarından alın.",
                  "Online bei ASFINAG gekaufte digitale Vignetten gelten als Privatkunde erst ab dem 18. Tag. Entweder mindestens 3 Wochen vorher kaufen oder an Tankstellen/Verkaufsstellen."),
                T("Kışın (1 Kasım – 15 Nisan) kış koşullarında kış lastiği zorunlu.",
                  "Im Winter (1. Nov. – 15. Apr.) bei winterlichen Verhältnissen Winterreifenpflicht.")
            )
        ),
        Country(
            "HU", "🇭🇺", T("Macaristan", "Ungarn"), T("Forint (HUF)", "Forint (HUF)"),
            T("Otoyollar (M1, M5 vb.) için e-vinyet (e-matrica) zorunlu. Plakaya kayıtlıdır, etiket yapıştırılmaz.",
              "Auf Autobahnen (M1, M5 usw.) ist eine e-Vignette (e-matrica) Pflicht. Sie ist an das Kennzeichen gebunden, kein Aufkleber."),
            "50 / 90 / 130", null,
            T("Acil: 112", "Notruf: 112"), "112",
            "https://ematrica.nemzetiutdij.hu",
            listOf(
                T("Şehir dışında gündüz de kısa farlar açık olmalı.",
                  "Außerorts auch tagsüber Abblendlicht einschalten."),
                T("Röszke (Sırbistan'a geçiş) sınır kapısında yaz aylarında uzun kuyruk olabilir.",
                  "Am Grenzübergang Röszke (nach Serbien) kann es im Sommer lange Wartezeiten geben.")
            )
        ),
        Country(
            "SI", "🇸🇮", T("Slovenya", "Slowenien"), T("Euro (€)", "Euro (€)"),
            T("Otoyollarda dijital vinyet (e-vinjeta) zorunlu. Karawanken tüneli için ayrıca ücret ödenir.",
              "Auf Autobahnen ist eine digitale Vignette (e-vinjeta) Pflicht. Für den Karawankentunnel wird zusätzlich Maut fällig."),
            "50 / 90 / 130", null,
            T("Acil: 112 · Polis: 113", "Notruf: 112 · Polizei: 113"), "112",
            "https://evinjeta.dars.si",
            listOf(
                T("Gündüz de kısa farlar açık olmalı.", "Auch tagsüber mit Abblendlicht fahren.")
            )
        ),
        Country(
            "HR", "🇭🇷", T("Hırvatistan", "Kroatien"), T("Euro (€)", "Euro (€)"),
            T("Otoyollar ücretli. Gişeler kaldırılıp plaka tanıyan gişesiz sisteme (free flow) geçiliyor: plakanızı internetten kaydedin veya ENC cihazı kullanın. Yola çıkmadan HAC sitesinden güncel durumu kontrol edin.",
              "Autobahnen sind mautpflichtig. Die Mautstellen werden durch ein schrankenloses System mit Kennzeichenerkennung (Free Flow) ersetzt: Kennzeichen online registrieren oder ENC-Gerät nutzen. Vor der Fahrt den aktuellen Stand bei HAC prüfen."),
            "50 / 90 / 130", null,
            T("Acil: 112", "Notruf: 112"), "112",
            "https://hac.hr",
            listOf(
                T("Kışın gündüz de kısa farlar açık olmalı.", "Im Winter auch tagsüber Abblendlicht.")
            )
        ),
        Country(
            "RS", "🇷🇸", T("Sırbistan", "Serbien"), T("Dinar (RSD)", "Dinar (RSD)"),
            T("Otoyollarda gişede ücret ödenir (nakit veya kart).",
              "Auf Autobahnen wird an Mautstellen bezahlt (bar oder Karte)."),
            "50 / 80 / 130",
            T("Hızlı yollarda (ekspres yol) 100 km/s.", "Auf Schnellstraßen 100 km/h."),
            T("Polis: 192 · İtfaiye: 193 · Ambulans: 194", "Polizei: 192 · Feuerwehr: 193 · Rettung: 194"), "192",
            "https://www.putevi-srbije.rs",
            listOf(
                T("Yeşil Kart'ta Sırbistan (SRB) kutusunun üstü çizili olmamalı.",
                  "Auf der Grünen Versicherungskarte darf das Feld Serbien (SRB) nicht durchgestrichen sein."),
                T("Gündüz de kısa farlar açık olmalı.", "Auch tagsüber mit Abblendlicht fahren.")
            )
        ),
        Country(
            "RO", "🇷🇴", T("Romanya", "Rumänien"), T("Leu (RON)", "Leu (RON)"),
            T("Tüm ulusal yollar için e-vinyet (rovinieta) zorunlu. Giurgiu–Ruse Tuna köprüsü ve Fetești köprüsü için ayrıca ücret ödenir.",
              "Für alle Nationalstraßen ist eine e-Vignette (Rovinieta) Pflicht. Für die Donaubrücke Giurgiu–Ruse und die Brücke bei Fetești wird zusätzlich Maut fällig."),
            "50 / 90 / 130",
            T("Ekspres yollarda 100 km/s.", "Auf Schnellstraßen 100 km/h."),
            T("Acil: 112", "Notruf: 112"), "112",
            "https://www.erovinieta.ro",
            listOf(
                T("Rovinietayı sınırdan önce internetten alın.", "Die Rovinieta vor der Grenze online kaufen.")
            )
        ),
        Country(
            "BG", "🇧🇬", T("Bulgaristan", "Bulgarien"), T("Euro (€) — 2026'dan beri", "Euro (€) — seit 2026"),
            T("Otoyol ve ana yollarda e-vinyet zorunlu (plakaya kayıtlı).",
              "Auf Autobahnen und Hauptstraßen ist eine e-Vignette Pflicht (an das Kennzeichen gebunden)."),
            "50 / 90 / 140", null,
            T("Acil: 112", "Notruf: 112"), "112",
            "https://www.bgtoll.bg",
            listOf(
                T("Vinyeti sınırdan önce resmi siteden alın.", "Die Vignette vor der Grenze auf der offiziellen Seite kaufen."),
                T("Kışın gündüz de kısa farlar açık olmalı.", "Im Winter auch tagsüber Abblendlicht.")
            )
        ),
        Country(
            "IT", "🇮🇹", T("İtalya", "Italien"), T("Euro (€)", "Euro (€)"),
            T("Otoyollar gişeli: girişte bilet alınır, çıkışta ödenir (nakit veya kart).",
              "Autobahnen mit Mautstellen: Ticket bei der Einfahrt ziehen, bei der Ausfahrt bezahlen (bar oder Karte)."),
            "50 / 90 / 130",
            T("Yağmurda otoyolda 110 km/s.", "Bei Regen auf der Autobahn 110 km/h."),
            T("Acil: 112", "Notruf: 112"), "112",
            "https://www.autostrade.it",
            listOf(
                T("Feribot biletini önceden alın; limana kalkıştan genellikle en az 2 saat önce varın.",
                  "Fährticket vorher buchen; meist mindestens 2 Stunden vor Abfahrt am Hafen sein."),
                T("Şehir merkezlerindeki ZTL (trafiğe kapalı bölge) alanlarına girmeyin, ceza kesilir.",
                  "Nicht in ZTL-Zonen (Verkehrsbeschränkte Zonen) in Innenstädten fahren – sonst Bußgeld.")
            )
        ),
        Country(
            "GR", "🇬🇷", T("Yunanistan", "Griechenland"), T("Euro (€)", "Euro (€)"),
            T("Otoyollar gişeli; nakit veya kartla ödenir.",
              "Autobahnen mit Mautstellen; Zahlung bar oder mit Karte."),
            "50 / 90 / 130", null,
            T("Acil: 112", "Notruf: 112"), "112",
            null,
            listOf(
                T("Türkiye'ye geçiş genellikle Kipoi – İpsala sınır kapısından yapılır.",
                  "Der Übergang in die Türkei erfolgt meist über Kipoi – İpsala.")
            )
        ),
        Country(
            "TR", "🇹🇷", T("Türkiye", "Türkei"), T("Türk Lirası (₺)", "Türkische Lira (₺)"),
            T("Otoyol ve köprülerde HGS zorunlu. HGS etiketi PTT'den ve bazı bankalardan alınabilir.",
              "Auf Autobahnen und Brücken ist HGS Pflicht. Den HGS-Aufkleber gibt es bei der PTT und einigen Banken."),
            "50 / 90 / 120",
            T("Bölünmüş yolda 110, otoyolda 120 km/s (tabelalara uyun).",
              "Auf getrennten Fahrbahnen 110, auf der Autobahn 120 km/h (Schilder beachten)."),
            T("Acil: 112", "Notruf: 112"), "112",
            "https://www.kgm.gov.tr",
            listOf(
                T("Araçta yangın tüpü, ilk yardım çantası ve iki uyarı üçgeni bulunmalı.",
                  "Im Auto müssen Feuerlöscher, Verbandskasten und zwei Warndreiecke sein."),
                T("Yabancı plakalı aracın Türkiye'de kalış süresi sınırlıdır; gümrük kurallarını kontrol edin.",
                  "Die Aufenthaltsdauer für Fahrzeuge mit ausländischem Kennzeichen ist begrenzt; Zollregeln prüfen.")
            )
        )
    ).associateBy { it.code }

    private val kapikule = Stop(
        T("Kapıkule Sınır Kapısı", "Grenzübergang Kapıkule"), "Kapıkule Border Gate, Edirne",
        T("Bulgaristan – Türkiye sınırı", "Grenze Bulgarien – Türkei")
    )

    val routes: List<Route> = listOf(
        Route(
            "A", T("Macaristan – Sırbistan", "Ungarn – Serbien"),
            T("En çok kullanılan kara yolu: Viyana, Budapeşte, Belgrad, Sofya.",
              "Die meistgenutzte Landroute: Wien, Budapest, Belgrad, Sofia."),
            listOf("DE", "AT", "HU", "RS", "BG", "TR"),
            listOf(
                Stop(T("Viyana", "Wien"), "Vienna, Austria"),
                Stop(T("Budapeşte", "Budapest"), "Budapest, Hungary"),
                Stop(T("Röszke Sınır Kapısı", "Grenzübergang Röszke"), "Röszke border crossing, Hungary",
                    T("Macaristan – Sırbistan sınırı", "Grenze Ungarn – Serbien")),
                Stop(T("Belgrad", "Belgrad"), "Belgrade, Serbia"),
                Stop(T("Niş", "Niš"), "Niš, Serbia"),
                Stop(T("Sofya", "Sofia"), "Sofia, Bulgaria"),
                kapikule
            ),
            listOf("Budapest, Hungary", "Belgrade, Serbia", "Sofia, Bulgaria"),
            listOf(
                T("Gerekli: Avusturya vinyeti, Macaristan e-vinyeti, Bulgaristan e-vinyeti. Sırbistan'da gişede ödenir.",
                  "Nötig: Vignette Österreich, e-Vignette Ungarn, e-Vignette Bulgarien. In Serbien zahlt man an der Mautstelle.")
            )
        ),
        Route(
            "B", T("Slovenya – Hırvatistan – Sırbistan", "Slowenien – Kroatien – Serbien"),
            T("Salzburg, Karawanken tüneli, Ljubljana, Zagreb, Belgrad, Sofya.",
              "Salzburg, Karawankentunnel, Ljubljana, Zagreb, Belgrad, Sofia."),
            listOf("DE", "AT", "SI", "HR", "RS", "BG", "TR"),
            listOf(
                Stop(T("Salzburg", "Salzburg"), "Salzburg, Austria"),
                Stop(T("Karawanken Tüneli", "Karawankentunnel"), "Karawanken Tunnel",
                    T("Ayrıca tünel ücreti", "Zusätzliche Tunnelmaut")),
                Stop(T("Ljubljana", "Ljubljana"), "Ljubljana, Slovenia"),
                Stop(T("Zagreb", "Zagreb"), "Zagreb, Croatia"),
                Stop(T("Belgrad", "Belgrad"), "Belgrade, Serbia"),
                Stop(T("Niş", "Niš"), "Niš, Serbia"),
                Stop(T("Sofya", "Sofia"), "Sofia, Bulgaria"),
                kapikule
            ),
            listOf("Ljubljana, Slovenia", "Zagreb, Croatia", "Belgrade, Serbia"),
            listOf(
                T("Gerekli: Avusturya vinyeti, Slovenya vinyeti, Bulgaristan e-vinyeti. Hırvatistan ve Sırbistan otoyolları ayrıca ücretli.",
                  "Nötig: Vignette Österreich, Vignette Slowenien, e-Vignette Bulgarien. Autobahnen in Kroatien und Serbien sind zusätzlich mautpflichtig.")
            )
        ),
        Route(
            "C", T("Romanya – Bulgaristan", "Rumänien – Bulgarien"),
            T("Sırbistan'a girmeden: Budapeşte, Arad, Sibiu, Bükreş, Ruse.",
              "Ohne Serbien: Budapest, Arad, Sibiu, Bukarest, Ruse."),
            listOf("DE", "AT", "HU", "RO", "BG", "TR"),
            listOf(
                Stop(T("Viyana", "Wien"), "Vienna, Austria"),
                Stop(T("Budapeşte", "Budapest"), "Budapest, Hungary"),
                Stop(T("Arad", "Arad"), "Arad, Romania", T("Nădlac sınır kapısından sonra", "Nach dem Grenzübergang Nădlac")),
                Stop(T("Sibiu", "Sibiu"), "Sibiu, Romania"),
                Stop(T("Bükreş", "Bukarest"), "Bucharest, Romania"),
                Stop(T("Giurgiu – Ruse Köprüsü", "Brücke Giurgiu – Ruse"), "Giurgiu-Ruse Friendship Bridge",
                    T("Romanya – Bulgaristan sınırı, köprü ücretli", "Grenze Rumänien – Bulgarien, Brückenmaut")),
                kapikule
            ),
            listOf("Budapest, Hungary", "Sibiu, Romania", "Ruse, Bulgaria"),
            listOf(
                T("Gerekli: Avusturya vinyeti, Macaristan e-vinyeti, Romanya rovinietası, Bulgaristan e-vinyeti, köprü ücreti.",
                  "Nötig: Vignette Österreich, e-Vignette Ungarn, Rovinieta Rumänien, e-Vignette Bulgarien, Brückenmaut.")
            )
        ),
        Route(
            "D", T("İtalya feribotu – Yunanistan", "Fähre Italien – Griechenland"),
            T("Brenner, Ancona'dan feribotla İgumenitsa, Selanik, İpsala.",
              "Brenner, Fähre ab Ancona nach Igoumenitsa, Thessaloniki, İpsala."),
            listOf("DE", "AT", "IT", "GR", "TR"),
            listOf(
                Stop(T("Innsbruck / Brenner", "Innsbruck / Brenner"), "Brenner Pass",
                    T("Brenner otoyolu ayrıca ücretli", "Brennerautobahn mit Streckenmaut")),
                Stop(T("Verona", "Verona"), "Verona, Italy"),
                Stop(T("Ancona Limanı", "Hafen Ancona"), "Port of Ancona, Italy",
                    T("Feribot bileti önceden alınmalı", "Fährticket vorher buchen")),
                Stop(T("İgumenitsa Limanı", "Hafen Igoumenitsa"), "Port of Igoumenitsa, Greece"),
                Stop(T("Selanik", "Thessaloniki"), "Thessaloniki, Greece"),
                Stop(T("İpsala Sınır Kapısı", "Grenzübergang İpsala"), "İpsala Border Gate, Edirne",
                    T("Yunanistan – Türkiye sınırı", "Grenze Griechenland – Türkei"))
            ),
            listOf("Ancona, Italy", "Igoumenitsa, Greece", "Thessaloniki, Greece"),
            listOf(
                T("Gerekli: Avusturya vinyeti ve Brenner ücreti, İtalya ve Yunanistan'da gişe, feribot bileti.",
                  "Nötig: Vignette Österreich und Brennermaut, Mautstellen in Italien und Griechenland, Fährticket."),
                T("Feribot bileti önceden alınmalı; Google Maps feribotlu güzergahı kendisi bulur.",
                  "Das Fährticket muss vorher gebucht werden; Google Maps findet die Route mit Fähre selbst.")
            )
        )
    )

    fun route(id: String): Route = routes.firstOrNull { it.id == id } ?: routes[0]

    private val gDocs = T("Belgeler", "Dokumente")
    private val gCar = T("Araç", "Auto")
    private val gToll = T("Vinyet ve ücretler", "Vignetten und Maut")
    private val gTrip = T("Yolculuk", "Reise")

    val checklist: List<CheckItem> = listOf(
        CheckItem("pass", gDocs, T("Pasaport veya kimlik (tüm yolcular)", "Reisepass oder Ausweis (alle Mitreisenden)")),
        CheckItem("lic", gDocs, T("Ehliyet", "Führerschein")),
        CheckItem("reg", gDocs, T("Araç ruhsatı", "Fahrzeugschein")),
        CheckItem("green", gDocs, T("Yeşil Kart sigortası (geçilen ülkeler çizili değil mi?)", "Grüne Versicherungskarte (sind alle Transitländer gültig?)")),
        CheckItem("power", gDocs, T("Vekaletname (araç başkasına aitse)", "Vollmacht (wenn das Auto jemand anderem gehört)")),
        CheckItem("health", gDocs, T("Sağlık sigortası kartı / seyahat sağlık sigortası", "Krankenversichertenkarte / Auslandskrankenversicherung")),

        CheckItem("service", gCar, T("Bakım: yağ, fren, lastikler", "Wartung: Öl, Bremsen, Reifen")),
        CheckItem("spare", gCar, T("Yedek lastik ve kriko", "Reserverad und Wagenheber")),
        CheckItem("vest", gCar, T("Reflektif yelek (her yolcu için)", "Warnweste (für jeden Mitreisenden)")),
        CheckItem("triangle", gCar, T("Uyarı üçgeni (Türkiye için iki tane)", "Warndreieck (für die Türkei zwei)")),
        CheckItem("firstaid", gCar, T("İlk yardım çantası", "Verbandskasten")),
        CheckItem("fire", gCar, T("Yangın tüpü (Türkiye'de zorunlu)", "Feuerlöscher (in der Türkei Pflicht)")),
        CheckItem("bulbs", gCar, T("Yedek ampul seti", "Ersatzlampen-Set")),

        CheckItem("v_at", gToll, T("Avusturya vinyeti (erken alın!)", "Vignette Österreich (früh kaufen!)")),
        CheckItem("v_hu", gToll, T("Macaristan e-vinyeti", "e-Vignette Ungarn"), setOf("A", "C")),
        CheckItem("v_si", gToll, T("Slovenya vinyeti", "Vignette Slowenien"), setOf("B")),
        CheckItem("v_hr", gToll, T("Hırvatistan otoyol kaydı / ENC", "Kroatien Maut-Registrierung / ENC"), setOf("B")),
        CheckItem("v_ro", gToll, T("Romanya rovinietası", "Rovinieta Rumänien"), setOf("C")),
        CheckItem("v_bg", gToll, T("Bulgaristan e-vinyeti", "e-Vignette Bulgarien"), setOf("A", "B", "C")),
        CheckItem("ferry", gToll, T("Feribot bileti", "Fährticket"), setOf("D")),
        CheckItem("hgs", gToll, T("HGS (Türkiye)", "HGS (Türkei)")),

        CheckItem("offline", gTrip, T("Google Maps'te çevrimdışı harita indir", "Offline-Karten in Google Maps laden")),
        CheckItem("charger", gTrip, T("Telefon şarj kablosu ve araç şarjı", "Ladekabel und Autoladegerät")),
        CheckItem("cash", gTrip, T("Nakit (euro ve yerel para)", "Bargeld (Euro und Landeswährung)")),
        CheckItem("food", gTrip, T("Su ve atıştırmalık", "Wasser und Snacks")),
        CheckItem("roaming", gTrip, T("Telefon: AB dışı ülkelerde (Sırbistan, Türkiye) roaming ücretleri", "Handy: Roaming-Kosten außerhalb der EU (Serbien, Türkei)"))
    )

    fun checklistFor(route: String) = checklist.filter { it.routes == null || route in it.routes }
}
