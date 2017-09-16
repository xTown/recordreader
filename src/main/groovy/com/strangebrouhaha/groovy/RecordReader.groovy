package com.strangebrouhaha.groovy

import com.opencsv.bean.ColumnPositionMappingStrategy
import com.opencsv.bean.StatefulBeanToCsvBuilder
import groovy.swing.SwingBuilder

import javax.swing.*
import java.awt.*
import java.nio.file.Files
import java.util.List

class RecordReader {

    void convert(Map<String, String> args, Boolean briefMode) {

        String outputFilename = args.containsKey("output") ? args.get("output") : "record.csv"

        File inputFile = new File(args.get("file"))
        String[] fileContents = inputFile.getText('ISO-8859-1').split(/\r\n\r\n/)

        List<ReaderRecord> readerRecords = []
        fileContents.each {
            readerRecords.add(ReaderRecordUtils.convertEntryToReaderRecord(it))
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
        RecordReader recordReader = new RecordReader()
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
                                if (file.showOpenDialog(file.parent) == JFileChooser.APPROVE_OPTION) {
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
                                recordReader.convert(data, briefMode.selected)
                                dispose()
                            }
                    )
                }
            }
        }
    }
}
