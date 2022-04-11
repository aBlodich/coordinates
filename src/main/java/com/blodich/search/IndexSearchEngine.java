package com.blodich.search;

import java.io.IOException;
import java.util.SortedMap;

public interface IndexSearchEngine {
    SortedMap<String, Long> search(SortedMap<String, Long> indexes, String prefix) throws IOException;
}
