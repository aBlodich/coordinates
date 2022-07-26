package com.blodich.search;

import java.io.IOException;
import java.util.List;
import java.util.SortedMap;
import tree.RTree;
import tree.geometry.Point;

/**
 * Базовый интерфейс поиска по индексам
 */
public interface IndexSearchEngine {
    /**
     * Осуществляет поиск в индексах по префиксу
     * @param indexes индексы
     * @param target целевая точка
     * @param delta величина изменения координат
     * @return SortedMap, содержащий найденные индексы
     */
    List<Point> search(RTree indexes, Point target, double delta) throws IOException;
}
