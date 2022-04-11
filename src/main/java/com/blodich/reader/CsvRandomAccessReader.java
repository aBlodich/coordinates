package com.blodich.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedMap;

public interface CsvRandomAccessReader {
    ArrayList<String> read(SortedMap<String, Long> indexes, String prefix) throws IOException;
}
