# Learning Tracker
Dieses Repository beinhaltet das Resultat meiner Bachelorarbeit mit dem Titel "App zur Sammlung von Lerndaten".
Diese Android-App macht von der [Experience API](http://experienceapi.com/)(xAPI) Gebrauch, um den Browserverlauf zu erfassen und automatisiert xAPI-Statements zu erstellen.

![Startseite](https://goo.gl/iarbkF)

Das obere Bild zeigt die Hauptansicht der App.

Neben der Adressleiste, die zudem als Suchfeld dient, befindet sich der Lerntoggle.
Solange der Nutzer am Lernen ist, kann er diesen Toggle aktiv lassen. Dabei werden alle Webseiten, die besucht werden, als lernrelevante Inhalte erfasst. Ist der Toggle deaktiviert, werden die besuchten Webseiten als irrelevant aufgenommen.
Der Toggle kann nach belieben aktiviert und deaktiviert werden. Die Lernrelevanz der aktuellen Seite wird während des Umschaltens dementsprechend angepasst.

Über das Menü ist der Verlauf der besuchten Webseiten einsehbar. Diese können per Klick direkt an den Browser übergeben und geladen werden.

Neben dem Toggle befindet sich der Button zur Anzeige der Datenvisualisierung.

## Datenvisualisierung
Die Datenvisualisierung besteht aus zwei Ansichten.

![Teil1](https://goo.gl/t24Zuq)

Die erste Ansicht präsentiert in einem Kreisdiagramm und einer zugehörigen Tabelle die Zeit, die der Nutzer auf verschiedenen Webseiten verbracht hat.
Das Kreisdiagramm wird mit der [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) dargestellt und bezieht die Daten aus dem Verlauf des Nutzers.
Wird eine Seite im Nachhinein durch den Toggle des Browsers als irrelevant gekennzeichnet, wird die Zeit der jeweiligen Seite aus dem Diagramm entfernt.


![Teil2](https://goo.gl/5y9tGW)

Die zweite Ansicht ist eine Präsentation der erstellen xAPI-Statements.
Jedes Statement ist mit der Eigenschaft "Lernrelevant" gekennzeichnet.
Entdeckt der Nutzer ein Statement, das seiner Meinung nach relevant bzw irrelevant ist, kann er dieses per Klick umschalten. Dabei werden alle Statements des jeweiligen Objects aktualisiert, um eine Konsistenz zu gewährleisten.
