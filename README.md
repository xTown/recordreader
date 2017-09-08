# Record Reader

Record Reader converts records to CSV using [opencsv](http://opencsv.sourceforge.net/).

A record looks like this, albeit significantly less silly.

```
Record 1 of 2
LOCATIONS    Foo
AUTHOR       Baz, Bar.
TITLE        Bat Qux Quux / Bar Baz.
IMPRINT      Foo : Bar, 1987.
DESCRIPT     xvii, 300 pages, [16] pages of plates : illustrations ; 24 cm.
NOTE         Includes bibliographical references (pages 265-285) and index.
NOTE         Foo -- Bar -- Baz -- Bat -- Qux 
               Quux -- Quuux.
SUBJECT      Foo, Bar, 1792-1933.
SUBJECT      Foo, Bar -- 20th century -- Biography.
ISBN/ISSN    0123456789.
ISBN/ISSN    987654321.
1 > PubLib BIOGRAPHIES (2nd B WEST, R.                     AVAILABLE
```

Record Reader converts each record into a `ReaderRecord` bean, stores those beans in a list,
then writes them out to a plain CSV file.

Usage:

`$ java -jar reader-record-<version>-all.jar <input> [output]`

Notes:
1. Download `build/libs/reader-record-<version>-all.jar` to somewhere easy to remember on your 
computer. Open a command prompt and run the Usage command from that location.
2. `<input>` is the name of the input file containing the records
3. The optional `[output]` lets you choose the name of the file to write. If you don't provide a
name, RR will create and use `record.csv` in your current directory.

## Changing output

To modify the output, edit `ReaderRecord.groovy`. The `columns()` method contains the list of
columns in the order they will display and the `header()` method contains the list of column 
names for the CSV header. 

For example, to remove `ALT AUTHOR`, you would remove `ALTAUTHOR` from `columns()` and `ALT AUTHOR`
from `header()`, and rebuild.
