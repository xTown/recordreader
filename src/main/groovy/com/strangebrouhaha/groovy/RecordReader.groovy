package com.strangebrouhaha.groovy

import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.StatefulBeanToCsvBuilder
import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*
import java.nio.file.Files
import java.util.List

class RecordReader {

    ReaderRecord convertEntryToReaderRecord(String entry) {
        // id from first line
        // first 13 characters are field unless first character is a digit
        ReaderRecord readerRecord = new ReaderRecord()
        List<String> lines = (entry.split(/\n/)).toList()
        readerRecord.setId(ReaderRecord.getIdFromRecordLine(lines.get(0)))
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
            readerRecord.append("YEAR", readerRecord.getYearFromImprint(readerRecord.IMPRINT))
        }
        // create FORMAT from TITLE (anything in [])
        if (null != readerRecord.TITLE) {
            readerRecord.append("FORMAT", readerRecord.getFormatStringFromTitle(readerRecord.TITLE))
        }
        readerRecord
    }

    // TODO Yes, I know. This was a q-and-d conversion from a main() method. I'll fix it.
    static void convert(Map<String, String> args, Boolean briefMode) {

        String outputFilename = args.containsKey("output") ? args.get("output") : "record.csv"

        RecordReader recordReader = new RecordReader()

        File inputFile = new File(args.get("file"))
        String[] fileContents = inputFile.getText('ISO-8859-1').split(/\r\n\r\n/)

        List<ReaderRecord> readerRecords = []
        fileContents.each {
            readerRecords.add(recordReader.convertEntryToReaderRecord(it))
        }

        File outputFile = new File(outputFilename)
        Files.deleteIfExists(outputFile.toPath())
        outputFile.createNewFile()
        Writer writer = new FileWriter(outputFilename)

        ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy()
        mappingStrategy.setType(ReaderRecord)
        // we get the headers if we don't use a MappingStrategy, but they're in an unfriendly order
        // since we want them in a friendly order, we have to write them out ourselves
        if (briefMode) {
            mappingStrategy.setColumnMapping(ReaderRecord.briefColumns())
            writer.write ReaderRecord.briefHeader()
        } else {
            mappingStrategy.setColumnMapping(ReaderRecord.columns())
            writer.write ReaderRecord.header()
        }

        StatefulBeanToCsvBuilder<ReaderRecord> beanToCsvBuilder =
                new StatefulBeanToCsvBuilder<>(writer).withMappingStrategy(mappingStrategy)
        beanToCsvBuilder.build().write(readerRecords)
        writer.close()
    }

    static void main (String[] args) {
        Map<String, String> data = [:]
        SwingBuilder sb = new SwingBuilder()
        sb.edt {
            dialog(
                    title: 'Convert Records to CSV',
                    defaultCloseOperation: JFrame.DISPOSE_ON_CLOSE,
                    size: [500, 200],
                    show: true
            ) {
                lookAndFeel("system")
                panel {
                    gridBagLayout()
                    source = textField(
                            text: '(No source file selected)',
                            constraints: gbc(
                                    gridx:0,
                                    gridy:0,
                                    gridwidth:GridBagConstraints.REMAINDER,
                                    fill:GridBagConstraints.HORIZONTAL
                            ),
                    )

                    button(
                            constraints: gbc(
                                    gridx:0,
                                    gridy:1
                            ),
                            text: 'Choose File...',
                            actionPerformed: {
                                JFileChooser file = sb.fileChooser(
                                    dialogTitle: 'Select a record file',
                                    fileSelectionMode: JFileChooser.FILES_AND_DIRECTORIES
                                )
                                if (file.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
                                    data.put("file", file.selectedFile.toString())
                                    source.text = file.selectedFile.toString()
                                }
                            }
                    )

                    briefMode = checkBox(
                            constraints: gbc(
                                    gridx:2,
                                    gridy:1
                            ),
                            text: 'Brief Mode'
                    )

                    button(
                            constraints: gbc(
                                    gridx:2,
                                    gridy:3,
                                    insets:[10,0,0,0],
                                    anchor:GridBagConstraints.CENTER
                            ),
                            text: 'Cancel',
                            actionPerformed: {
                                dispose()
                            }
                    )

                    button(
                            constraints: gbc(
                                    gridx:3,
                                    gridy:3,
                                    insets:[10,0,0,0],
                                    anchor:GridBagConstraints.CENTER
                            ),
                            text: 'Convert',
                            actionPerformed: {
                                convert(data, briefMode.selected)
                                dispose()
                            }
                    )
                }
            }
        }
    }
}
