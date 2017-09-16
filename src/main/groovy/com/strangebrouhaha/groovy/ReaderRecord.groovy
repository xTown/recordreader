package com.strangebrouhaha.groovy

import groovy.transform.Canonical

@Canonical
class ReaderRecord {
    Long id
    String LOCATIONS
    String AUTHOR
    String UNIFTITLE
    String TITLE
    String FORMAT
    String ALTTITLE
    String EDITION
    String IMPRINT
    String YEAR
    String DESCRIPT
    String SERIES
    String NOTE
    String SUBJECT
    String ALTAUTHOR
    String ISBNISSN
    String SHELFINFO

    static String[] columns() {
        String[] columns = ["id",
                            "TITLE",
                            "FORMAT",
                            "UNIFTITLE",
                            "AUTHOR",
                            "ISBNISSN",
                            "LOCATIONS",
                            "SHELFINFO",
                            "IMPRINT",
                            "YEAR",
                            "EDITION",
                            "SUBJECT",
                            "NOTE",
                            "SERIES",
                            "DESCRIPT",
                            "ALTTITLE",
                            "ALTAUTHOR"
        ] as String[]
        return columns
    }

    static String[] briefColumns() {
        String[] columns = [
                "id",
                "AUTHOR",
                "TITLE",
                "FORMAT",
                "IMPRINT",
                "YEAR",
                "UNIFTITLE"
        ] as String[]
        return columns
    }

    static String header() {
        "id, TITLE, FORMAT, UNIF TITLE, AUTHOR, ISBN/ISSN, LOCATIONS, SHELF INFO, IMPRINT, YEAR, EDITION, SUBJECT, NOTE, SERIES, DESCRIPT, ALT TITLE, ALT AUTHOR\r\n"
    }

    static String briefHeader() {
        "id, AUTHOR, TITLE, FORMAT, IMPRINT, YEAR, UNIF TITLE\r\n"
    }

    @Override
    String toString() {
        "$id, $LOCATIONS, $AUTHOR, $UNIFTITLE, $TITLE, $ALTTITLE, $EDITION, $IMPRINT, $DESCRIPT, $SERIES, $NOTE, $SUBJECT, $ALTAUTHOR, $ISBNISSN, $SHELFINFO"
    }

    void append(String key, String value) {
        if (null == this[key]) {
            this[key] = value
        } else {
            this[key] += "; ${value}"
        }
    }
}
