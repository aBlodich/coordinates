package com.blodich.search;


import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.stream.Collectors;
import tree.RTree;
import tree.geometry.Point;
import tree.geometry.Rectangle;

/**
 * Реализация IndexSearchEngine.
 */
public class CsvIndexSearchEngine implements IndexSearchEngine {

    /**
     * Реалзиация метода search с помощью префикса
     * @param indexes индексы
     * @param target целевая точка
     * @param delta величина изменения координат
     * @return SortedMap, содержащий найденные индексы
     */
    public List<Point> search(RTree indexes, Point target, double delta) {
        double[] mercator = mercator(target.getX(), target.getY(), delta);
        double dLatitude = mercator[0];
        double dLongitude = mercator[1];
        Point leftTop = new Point(target.getX() - dLatitude / 2, target.getY() + dLongitude / 2);
        Point rightBottom = new Point(target.getX() + dLatitude / 2, target.getY() - dLongitude / 2);
        Rectangle targetRectangle = new Rectangle(leftTop, rightBottom);
        var result =  indexes.range(targetRectangle);
        return result
            .stream()
            .filter(r -> target.distance(r) <= delta)
            .sorted(Comparator.comparingDouble(target::distance))
            .limit(5)
            .collect(Collectors.toList());
    }

    /**
     * Проекция Меркатора сферы на цилиндр.
     *
     * @param latitude - широта в градусах.
     * @param longitude - долгота в градусах.
     * @param delta - величина изменения координат
     * @return массив с широтой и долготой в прямоугольных координатах.
     */
    public static double[] mercator(double latitude, double longitude, double delta) {
        // 6371 - средний радиус земли
        double latitudeInKm = 6371 * Math.log(Math.tan(Math.toRadians(latitude)/2 + Math.PI/4)) + delta;
        double dLatitude = Math.toDegrees(Math.atan(Math.sinh(latitudeInKm / 6371)));
        double longitudeInKm = Math.toRadians(longitude) * 6371 + delta;
        double dLongitude = Math.toDegrees(longitudeInKm / 6371);
        return new double[] {latitude, longitude};
    }
}
