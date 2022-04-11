package com.blodich.reader;

import com.blodich.indexer.FileIndexer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.SortedMap;

/**
 * Обработчик файла
 */
public class FileProcessor {
    private FileIndexer indexer;
    private SortedMap<String, Long> indexes;
    private int size;
    private CsvRandomAccessReader reader;

    /**
     * Конструктор класса
     * @param indexer реализация FileIndexer
     * @param reader реализация ScvRandomAccessReader
     */
    public FileProcessor(FileIndexer indexer, CsvRandomAccessReader reader) {
        this.indexer = indexer;
        this.reader = reader;
    }

    /**
     * Препроцессинг файла, проводит индексацию файла
     * @return Возвращает количество проидексированных строк
     * @throws IOException
     */
    public int preprocess() throws IOException {
        this.indexes =  indexer.index();
        this.size = indexes.size();
        return this.size;
    }

    /**
     * Осуществляет поиск по файлу с помощью CsvRandomAccessReader
     * @param prefix префикс поиска
     * @return возвращает ArrayList, содержащий все найденные строки
     * @throws IOException
     */
    public ArrayList<String> process(String prefix) throws IOException {
        return reader.read(indexes, prefix);
    }
}
