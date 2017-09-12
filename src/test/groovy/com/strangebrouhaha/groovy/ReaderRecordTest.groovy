package com.strangebrouhaha.groovy

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull;

class ReaderRecordTest {

    ReaderRecord readerRecord
    RecordReader recordReader
    String filename = "/Users/rjahrling/Documents/src/reader-record/src/test/resources/export.txt"

    @Before
    void setUp() {
        readerRecord = new ReaderRecord(
                1L,
                "locations",
                "AUTHOR",
                "UNIFTITLE",
                "TITLE",
                "ALTTITLE",
                "EDITION",
                "IMPRINT",
                "DESCRIPT",
                "SERIES",
                "NOTE",
                "SUBJECT",
                "ALTAUTHOR",
                "ISBNISSN issn"
        )

        recordReader = new RecordReader()
    }

    @Test
    void testReaderRecord() throws Exception {
        assertEquals(1L, readerRecord.id)
        assertEquals("locations", readerRecord.LOCATIONS)
        assertNull(readerRecord.SHELFINFO)
    }

    @Test
    void testNumRecordsParser() throws Exception {
        assertEquals(10L, recordReader.getIdFromRecordLine("Record 10 of 900"))
    }

    @Test
    void testOnlyOneShelfInfo() throws Exception {
        String input = "Record 1 of 1\n1 > Keep this but chop off              this part\n2 > this should not display at all"
        assertEquals("Keep this but chop off", recordReader.convertEntryToReaderRecord(input).SHELFINFO)
    }

    @Test
    void testOnlyOneIsbn() throws Exception {
        String input = "Record 1 of 1\nISBN/ISSN    1234567890\nISBN/ISSN    0987654321\nISBN/ISSN    9999999999"
        assertEquals("1234567890", recordReader.convertEntryToReaderRecord(input).ISBNISSN)
    }

    @Test
    void testAppending() throws Exception {
        String input = "Record 1 of 1\nAUTHOR       Author One\nAUTHOR       Author Two\nAUTHOR       Author Three"
        assertEquals("Author One; Author Two; Author Three", recordReader.convertEntryToReaderRecord(input).AUTHOR)
    }

    @Test
    void testFormatExtractor() throws Exception {
        String input = "Record 1 of 1\nTITLE        This is a title and [format string] is what we want to extract"
        assertEquals("format string", recordReader.convertEntryToReaderRecord(input).FORMAT)
    }

    @Test
    void testFormatExtractorWithNoFormat() throws Exception {
        String input = "Record 1 of 1\nTITLE        This is a title with no format string"
        assertEquals("print book", recordReader.convertEntryToReaderRecord(input).FORMAT)
    }

    @Test
    void testYearExtractor() throws Exception {
        String input = "Record 1 of 1\nIMPRINT      This input string ends with a year 2013."
        assertEquals("2013", recordReader.convertEntryToReaderRecord(input).YEAR)
    }

}
