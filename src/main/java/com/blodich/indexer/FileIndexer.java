package com.blodich.indexer;

import java.io.IOException;
import java.util.SortedMap;

public interface FileIndexer {
    SortedMap<String, Long> index() throws IOException;
}
