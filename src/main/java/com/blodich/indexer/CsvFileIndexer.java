package com.blodich.indexer;

import java.io.*;
import java.util.SortedMap;
import java.util.TreeMap;

public class CsvFileIndexer implements FileIndexer {
    private final int column;
    private final String path;

    public CsvFileIndexer(String path, int column) {
        this.path = path;
        this.column = column;
    }

    @Override
    public SortedMap<String, Long> index() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.path), "UTF-8"))) {
            String row;
            long seek = 0;
            TreeMap<String, Long> indexes = new TreeMap<>();
            char i = 1;
            while ((row = br.readLine()) != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(row.split(",")[this.column].replaceAll("\"", ""));
                sb.append(i);
                indexes.put(sb.toString(), seek);
                seek += (row.getBytes("UTF-8").length + 1);
                i++;
            }
            return indexes;
        }
    }
}