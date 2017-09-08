package com.strangebrouhaha.groovy

import groovy.transform.Canonical

@Canonical
class ReaderRecord {
    Long id
    String LOCATIONS
    String AUTHOR
    String UNIFTITLE
    String TITLE
    String ALTTITLE
    String EDITION
    String IMPRINT
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
                            "UNIFTITLE",
                            "AUTHOR",
                            "ISBNISSN",
                            "LOCATIONS",
                            "SHELFINFO",
                            "IMPRINT",
                            "EDITION",
                            "SUBJECT",
                            "NOTE",
                            "SERIES",
                            "DESCRIPT",
                            "ALTTITLE",
                            "ALTAUTHOR"
        ] as String[]
    }

    static String header() {
        "id, TITLE, UNIF TITLE, AUTHOR, ISBN/ISSN, LOCATIONS, SHELF INFO, IMPRINT, EDITION, SUBJECT, NOTE, SERIES, DESCRIPT, ALT TITLE, ALT AUTHOR\r\n"
    }

    @Override
    String toString() {
        "$id, $LOCATIONS, $AUTHOR, $UNIFTITLE, $TITLE, $ALTTITLE, $EDITION, $IMPRINT, $DESCRIPT, $SERIES, $NOTE, $SUBJECT, $ALTAUTHOR, $ISBNISSN, $SHELFINFO"
    }

    Pair toPair(String line) {
        String key = line.substring(0, 12).trim()
        String value = line.substring(13).trim()
        return new Pair(key, value)
    }

    void append(String key, String value) {
        if (null == this[key]) {
            this[key] = value
        } else {
            this[key] += "; ${value}"
        }
    }
}
