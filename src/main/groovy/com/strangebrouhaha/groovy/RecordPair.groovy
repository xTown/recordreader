package com.strangebrouhaha.groovy

import groovy.transform.Canonical

@Canonical
class RecordPair {
    String key
    String value

    RecordPair(String line) {
        this.key = line.substring(0, 12).trim()
        this.value = line.substring(13).trim()
    }

    @Override
    String toString() {
        "RECORD PAIR: $key, $value"
    }

}
