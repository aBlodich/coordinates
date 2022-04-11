package com.blodich.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedMap;

/**
 * Базовый интерфейс для чтения файла с помощью индексов
 */
public interface CsvRandomAccessReader {
    /**
     * Метод для чтения
     * @param indexes SortedMap индексов
     * @param prefix String префикс для поиска по индексу
     * @return ArrayList, содержащий найденные строки
     * @throws IOException
     */
    ArrayList<String> read(SortedMap<String, Long> indexes, String prefix) throws IOException;
}
