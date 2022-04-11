package com.blodich.search;


import java.util.SortedMap;

/**
 * Реализация IndexSearchEngine
 */
public class CsvIndexSearchEngine implements IndexSearchEngine {

    /**
     * Реалзиация метода search с помощью префикса
     * @param indexes индексы
     * @param prefix префикс для поиска
     * @return SortedMap, содержащий найденные индексы
     */
    public SortedMap<String, Long> search(SortedMap<String, Long> indexes, String prefix) {
        if (prefix.length() > 0) {
            int length = prefix.length();
            char nextLetter = (char) (prefix.charAt(length -1) + 1);
            String end = prefix.substring(0, length-1) + nextLetter;
            return indexes.subMap(prefix, end);
        }
        return indexes;
    }
}
