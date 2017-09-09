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

## Usage
[Click here](https://github.com/xTown/recordreader/raw/master/build/libs/reader-record-0.2-all.jar) to download the 
application's `jar` file to somewhere easy to remember on your computer. Once it's downloaded, double-click it to run.

Click the `Choose File...` button to choose the source file, then click `Convert`
to convert the file. It will automatically create a file called `record.csv` in the same 
directory that contains the `jar` file.

## Changing output

To modify the output, edit `ReaderRecord.groovy`. The `columns()` method contains the list of
columns in the order they will display and the `header()` method contains the list of column 
names for the CSV header. 

For example, to remove `ALT AUTHOR`, you would remove `ALTAUTHOR` from `columns()` and `ALT AUTHOR`
from `header()`, and rebuild.
