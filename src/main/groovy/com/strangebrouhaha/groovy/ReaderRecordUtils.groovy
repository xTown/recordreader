package com.strangebrouhaha.groovy

import java.util.regex.Matcher
import java.util.regex.Pattern


class ReaderRecordUtils {

    static List<String> formats = [
            "audiocassette",
            "Blu",
            "book",
            "CD",
            "compact disc",
            "computer",
            "Downloadable Audiobook",
            "Downloadable eBook",
            "DVD",
            "electronic resource",
            "graphic novel",
            "kit",
            "large",
            "magazine",
            "map",
            "microfilm",
            "microform",
            "motion picture",
            "MP3",
            "newspaper",
            "sound recording",
            "Streaming media",
            "VHS",
            "Videocassette",
            "video game",
            "videorecording",
            "paperback"
    ]

    static String getFormatStringFromTitle(String title) {
        Pattern bracketPattern = Pattern.compile(/\[(.*?)]/)
        Matcher matchers = bracketPattern.matcher(title)
        String format = matchers.size() > 0 ? matchers[0][1] : "book"
        for (int i = 0; i < formats.size(); i++) {
            if (format.startsWith(formats.get(i))) {
                return format
            }
        }

        "unknown"
    }

    static String getYearFromImprint(String imprint) {
        if (null == imprint) {
            return null
        }
        // some imprints are just the year followed by a period
        if (imprint.size() < 6) {
            imprint.replaceAll(/[^0-9]/,"")
        } else {
            // otherwise, the year could be enclosed in [] or preceded with p or c or other text
            imprint.substring(imprint.size() - 6).replaceAll(/[^0-9]/, "")
        }
    }

    static Long getIdFromRecordLine(String recordLine) {
        Long.parseLong(recordLine.split()[1])
    }

    static ReaderRecord convertEntryToReaderRecord(String entry) {
        // id from first line
        // first 13 characters are field unless first character is a digit
        ReaderRecord readerRecord = new ReaderRecord()
        List<String> lines = (entry.split(/\n/)).toList()
        readerRecord.setId(getIdFromRecordLine(lines.get(0)))
        // some lines are continuations and don't have a key
        // we need to remember the previous key
        String tKey
        lines.eachWithIndex { line, index ->
            if (index > 0 && !Character.isDigit(line.charAt(0))) {
                RecordPair pair = new RecordPair(line)
                if (tKey == null) {
                    tKey = pair.key
                } else if (tKey == "ISBN/ISSN") {
                    return  // we only want the first ISBN/ISSN, regardless of how many there are
                } else if (pair.key != "" && pair.key != tKey) {
                    tKey = pair.key
                } else if (pair.key == "") {
                    pair.setKey(tKey)
                }
                // normalize keys
                pair.setKey(pair.key.replaceAll(/[^A-Z]/, ""))
                // some of the records include headers for the shelf location:
                //     LOCATION                CALL #                         STATUS
                // suppress that, it's irrelevant
                if (pair.key != "LOCATION" && !pair.value.contains("CALL")) {
                    readerRecord.append(pair.key, pair.value)
                }
            } else if (Character.isDigit(line.charAt(0))) {
                // we only want the first shelf info line, regardless of how many there are
                // if a record has more than 10 checkouts, lines are zero-padded ("01" vs "1")
                if ((Integer.parseInt(line.charAt(0).toString()) == 1 && !Character.isDigit(line.charAt(1))) || line.substring(0, 2) == "01") {
                    readerRecord.append("SHELFINFO", line.substring(line.indexOf('>') + 1, line.length() - 20).trim())
                }
            }
        }
        // create YEAR from IMPRINT (last 6 characters, strip non-digits)
        if (null != readerRecord.IMPRINT) {
            readerRecord.append("YEAR", getYearFromImprint(readerRecord.IMPRINT))
        }
        // create FORMAT from TITLE (anything in [])
        if (null != readerRecord.TITLE) {
            readerRecord.append("FORMAT", getFormatStringFromTitle(readerRecord.TITLE))
        }
        readerRecord
    }
}
