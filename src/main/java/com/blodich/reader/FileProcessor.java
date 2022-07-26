package com.blodich.reader;

import com.blodich.indexer.FileIndexer;
import com.blodich.indexer.RTreeFileIndexer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import tree.RTree;
import tree.geometry.Point;

/**
 * Обработчик файла
 */
public class FileProcessor {
    private RTreeFileIndexer indexer;
    private RTree indexes;
    private CsvRandomAccessReader reader;

    /**
     * Конструктор класса
     * @param indexer реализация FileIndexer
     * @param reader реализация ScvRandomAccessReader
     */
    public FileProcessor(RTreeFileIndexer indexer, CsvRandomAccessReader reader) {
        this.indexer = indexer;
        this.reader = reader;
    }

    /**
     * Препроцессинг файла, проводит индексацию файла
     * @throws IOException
     */
    public void preprocess() throws IOException {
        this.indexes =  indexer.index();
    }

    /**
     * Осуществляет поиск по файлу с помощью CsvRandomAccessReader
     * @param target префикс поиска
     * @param delta величина изменения координат
     * @return возвращает List, содержащий все найденные строки
     * @throws IOException
     */
    public List<String> process(Point target, double delta) throws IOException {
        return reader.read(indexes, target, delta);
    }
}
