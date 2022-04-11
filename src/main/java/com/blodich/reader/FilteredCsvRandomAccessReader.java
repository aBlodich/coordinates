package com.blodich.reader;

import com.blodich.search.IndexSearchEngine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.SortedMap;

/**
 * Реализация интерфейса CsvRandomAccessReader
 */
public class FilteredCsvRandomAccessReader implements CsvRandomAccessReader {
    private String path;
    private IndexSearchEngine searchEngine;

    /**
     * Конструктор класса
     * @param path путь к файлу
     * @param searchEngine реализация интерфейса IndexSearchEngine для поиска по индексам
     */
    public FilteredCsvRandomAccessReader(String path, IndexSearchEngine searchEngine) {
        this.path = path;
        this.searchEngine = searchEngine;
    }

    /**
     * Реализация метода read для поиска по индексам с помощью IndexSearchEngine
     * @param indexes SortedMap индексов
     * @param prefix String префикс для поиска по индексу
     * @return
     * @throws IOException
     */
    @Override
    public ArrayList<String> read(SortedMap<String, Long> indexes, String prefix) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(new File(path), "r")) {
            ArrayList<String> result = new ArrayList<>();
            var values = searchEngine.search(indexes, prefix).values();
            for (var value: values) {
                raf.seek(value);
                result.add(raf.readLine());
            }
            return result;
        }
    }
}
