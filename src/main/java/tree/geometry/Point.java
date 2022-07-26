package tree.geometry;

import lombok.Getter;
import lombok.ToString;

/**
 * Класс точки.
 */
@Getter
@ToString
public class Point implements Cloneable {
  private double x;
  private double y;
  private long seek;   //Для индексирования

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
    this.seek = -1;
  }

  public Point(double x, double y, long seek) {
    this(x, y);
    this.seek = seek;
  }

  // расчет дистанции между двумя точками по ортодромии
  public double distance(Point target) {
    // 6371 - средний радиус земли
    double xRad = Math.toRadians(x);
    double yRad = Math.toRadians(y);
    double targetXRad = Math.toRadians(target.getX());
    double targetYRad = Math.toRadians(target.getY());
    double a = Math.sin(xRad) * Math.sin(targetXRad);
    double b = Math.cos(xRad) * Math.cos(targetXRad) * Math.cos(Math.max(targetYRad, yRad) - Math.min(targetYRad, yRad));
    double sigma = Math.acos(a + b);
    return 6371 * sigma;
  }

  @Override
  public Point clone() {
    try {
      return (Point) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

}
