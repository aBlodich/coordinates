package com.blodich.reader;

import com.blodich.indexer.RTreeFileIndexer;
import com.blodich.search.IndexSearchEngine;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import tree.RTree;
import tree.geometry.Point;

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
     * @param tree Rtree с индексами
     * @param target Целевая точка для поиска
     * @param delta Величина изменения координат
     * @return List, содержащий найденные записи в файле
     * @throws IOException
     */
    @Override
    public List<String> read(RTree tree, Point target, double delta) throws IOException {
            ;
        try (RandomAccessFile raf = new RandomAccessFile(new File(FilteredCsvRandomAccessReader.class.getClassLoader().getResource("airports.csv").getPath()), "r")) {
            ArrayList<String> result = new ArrayList<>();
            var points = searchEngine.search(tree, target, delta);
            for (var point : points) {
                raf.seek(point.getSeek());
                result.add(raf.readLine());
            }
            return result;
        }
    }
}
