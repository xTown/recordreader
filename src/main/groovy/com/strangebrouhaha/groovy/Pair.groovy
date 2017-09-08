package com.strangebrouhaha.groovy

import groovy.transform.Canonical

@Canonical
class Pair {
    String key
    String value

    @Override
    String toString() {
        "PAIR: $key, $value"
    }
}
