package com.blodich.search;

import java.io.IOException;
import java.util.SortedMap;

/**
 * Базовый интерфейс поиска по индексам
 */
public interface IndexSearchEngine {
    /**
     * Осуществляет поиск в индексах по префиксу
     * @param indexes индексы
     * @param prefix префикс для поиска
     * @return SortedMap, содержащий найденные индексы
     * @throws IOException
     */
    SortedMap<String, Long> search(SortedMap<String, Long> indexes, String prefix) throws IOException;
}
