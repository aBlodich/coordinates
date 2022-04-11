package com.blodich.reader;

import com.blodich.indexer.FileIndexer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedMap;

public class FileProcessor {
    private FileIndexer indexer;
    private SortedMap<String, Long> indexes;
    private int size;
    private CsvRandomAccessReader reader;

    public FileProcessor(FileIndexer indexer, CsvRandomAccessReader reader) {
        this.indexer = indexer;
        this.reader = reader;
    }

    public int preprocess() throws IOException {
        this.indexes =  indexer.index();
        this.size = indexes.size();
        return this.size;
    }

    public ArrayList<String> process(String prefix) throws IOException {
        return reader.read(indexes, prefix);
    }
}
