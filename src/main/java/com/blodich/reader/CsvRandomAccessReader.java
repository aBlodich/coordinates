package com.blodich.reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import tree.RTree;
import tree.geometry.Point;

/**
 * Базовый интерфейс для чтения файла с помощью индексов
 */
public interface CsvRandomAccessReader {
    /**
     * Метод для чтения
     * @param tree Rtree с индексами
     * @param target Целевая точка для поиска
     * @param delta Величина изменения координаты
     * @return List, содержащий найденные строки
     * @throws IOException
     */
    List<String> read(RTree tree, Point target, double delta) throws IOException;
}
