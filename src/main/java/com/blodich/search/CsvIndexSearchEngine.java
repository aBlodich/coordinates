package com.blodich.search;


import java.util.SortedMap;

public class CsvIndexSearchEngine implements IndexSearchEngine {
    private String path;

    public CsvIndexSearchEngine(String path) {
        this.path = path;
    }

    public SortedMap<String, Long> search(SortedMap<String, Long> indexes, String prefix) {
        if (prefix.length() > 0) {
            int length = prefix.length();
            char nextLetter = (char) (prefix.charAt(length -1) + 3);
            String end = prefix.substring(0, length-1) + nextLetter;
            return indexes.subMap(prefix, end);
        }
        return indexes;
    }
}
