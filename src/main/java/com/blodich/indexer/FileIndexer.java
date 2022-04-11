package com.blodich.indexer;

import java.io.IOException;
import java.util.SortedMap;

/**
 * Базовый интерфейс для индексатора файлов
 */
public interface FileIndexer {
    /**
     * Метод для индексирования
     * @return Возвращает SortedMap, который содержить значение колонки в качестве ключа
     * и смещение в байтах для строки
     * @throws IOException
     */
    SortedMap<String, Long> index() throws IOException;
}
