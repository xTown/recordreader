package com.strangebrouhaha.groovy

import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.StatefulBeanToCsvBuilder

import java.nio.file.Files

class RecordReader {

    Long getIdFromRecordLine(String recordLine) {
        return Long.parseLong(recordLine.split()[1])
    }

    ReaderRecord convertEntryToReaderRecord(String entry) {
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
                Pair pair = readerRecord.toPair(line)
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
                if ((Integer.parseInt(line.charAt(0).toString()) == 1 && !Character.isDigit(line.charAt(1))) || line.substring(0, 2) == "01") {
                    readerRecord.append("SHELFINFO", line.substring(line.indexOf('>') + 1, line.length() - 20).trim())
                }
            }
        }
        readerRecord
    }

    static void main (String[] args) {
        if (args.size() == 0) {
            System.err << "ERROR: Please provide a filename."
            return
        }

        String outputFilename = args.size() == 2 ? args[1] : "record.csv"

        RecordReader recordReader = new RecordReader()

        File file = new File(args[0])
        String[] fileContents = file.getText('ISO-8859-1').split(/\r\n\r\n/)
        File outputFile = new File(outputFilename)
        Files.deleteIfExists(outputFile.toPath())
        outputFile.createNewFile()

        List<ReaderRecord> readerRecords = []
        fileContents.each {
            readerRecords.add(recordReader.convertEntryToReaderRecord(it))
        }

        Writer writer = new FileWriter(outputFilename)
        ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy()
        mappingStrategy.setType(ReaderRecord)
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
        // we get the headers if we don't use a MappingStrategy, but they're in an unfriendly order
        // since we want them in a friendly order, we have to write them out ourselves
        writer.write"id, TITLE, UNIF TITLE, AUTHOR, ISBN/ISSN, LOCATIONS, SHELF INFO, IMPRINT, EDITION, SUBJECT, NOTE, SERIES, DESCRIPT, ALT TITLE, ALT AUTHOR\r\n"
        mappingStrategy.setColumnMapping(columns)
        mappingStrategy.generateHeader()
        StatefulBeanToCsvBuilder<ReaderRecord> beanToCsvBuilder =
                new StatefulBeanToCsvBuilder<>(writer).withMappingStrategy(mappingStrategy)
        beanToCsvBuilder.build().write(readerRecords)
        writer.close()

    }
}
