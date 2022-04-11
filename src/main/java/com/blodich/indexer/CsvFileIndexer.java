package com.blodich.indexer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Реализация интерфейса FileIndexer
 */
public class CsvFileIndexer implements FileIndexer {
    private final int column;
    private final String path;

    /**
     * Контструктор индексатора
     * @param path путь к файлу
     * @param column колонка, для которой нужна индексация
     */
    public CsvFileIndexer(String path, int column) {
        this.path = path;
        this.column = column;
    }

    /**
     * Метод для индексирования. Для индексирования одинаковых значений,
     * к каждой строке добавляется "соль" в виде char
     * @return Возвращает SortedMap, который содержить значение колонки в качестве ключа
     * и смещение в байтах для строки
     * @throws IOException
     */
    @Override
    public SortedMap<String, Long> index() throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(this.path), StandardCharsets.UTF_8))) {
            String row;
            long seek = 0;
            TreeMap<String, Long> indexes = new TreeMap<>();
            char i = 1;
            while ((row = br.readLine()) != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(row.split(",")[this.column].replaceAll("\"", ""));
                sb.append(i);
                indexes.put(sb.toString(), seek);
                seek += (row.getBytes(StandardCharsets.UTF_8).length + 1);
                i++;
            }
            return indexes;
        }
    }
}