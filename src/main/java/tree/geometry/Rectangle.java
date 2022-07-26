package tree.geometry;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Класс прямоугольника.
 */
@Getter
@Setter
@ToString
public class Rectangle implements Cloneable {
  private Point leftTop;
  private Point rightBottom;

  private Point dataPoint;

  public Rectangle(Point leftTop, Point rightBottom) {
    double lefTopX = leftTop.getX();
    double rightBottomX = rightBottom.getX();
    double leftTopY = leftTop.getY();
    double rightBottomY = rightBottom.getY();
    double xMin = Math.min(lefTopX, rightBottomX);
    double xMax = Math.max(lefTopX, rightBottomX);
    double yMin = Math.min(leftTopY, rightBottomY);
    double yMax = Math.max(leftTopY, rightBottomY);
    this.leftTop = new Point(xMin, yMax);
    this.rightBottom = new Point(xMax, yMin);
  }

  public Rectangle(Point leftTop, Point rightBottom, Point dataPoint) {
    this(leftTop, rightBottom);
    this.dataPoint = dataPoint;
  }

  /**
   * Расчет длины прямоугольника.
   *
   * @return длину прямоугольника.
   */
  public double length() {
    return Math.abs(rightBottom.getX() - leftTop.getX());
  }

  /**
   * Расчет ширины прямоугольника.
   *
   * @return ширину прямоугольника.
   */
  public double width() {
    return Math.abs(leftTop.getY() - rightBottom.getY());
  }

  /**
   * Расчет площади прямоугольника.
   *
   * @return площадь прямоугольника.
   */
  public double area() {
    return length() * width();
  }

  /**
   * Расчет площади объединенных прямоугольников.
   * @param rectangle - прямоугольник с которым нужно объединить текущий прямоугольник.
   * @return объединенную площадь.
   */
  public double unionArea(Rectangle rectangle) {
    return Math.abs(getUnionRectangle(rectangle).area() - area());
  }

  /**
   * Объединяет текущий прямоугольник с другим.
   *
   * @param rectangle - прямоугольник, с которым необходимо объединить текущий прямоугольник.
   */
  public void unionRectangle(Rectangle rectangle) {
    var union = getUnionRectangle(rectangle);
    leftTop = union.leftTop;
    rightBottom = union.rightBottom;
  }

  /**
   * Рассчитывает объединенный прямоугольник.
   * @param rectangle - прямоугольник с которым нужно объединить текущий прямоугольник.
   * @return MBR - минимальный ограничивающий прямоугольник двух прямоугольников.
   */
  public Rectangle getUnionRectangle(Rectangle rectangle) {
    double leftTopX = Math.min(this.leftTop.getX(), rectangle.leftTop.getX());
    double leftTopY = Math.max(this.leftTop.getY(), rectangle.leftTop.getY());

    double rightBottomX = Math.max(this.rightBottom.getX(), rectangle.rightBottom.getX());
    double rightBottomY = Math.min(rightBottom.getY(), rectangle.rightBottom.getY());
    return new Rectangle(new Point(leftTopX, leftTopY), new Point(rightBottomX, rightBottomY));
  }

  /**
   * Проверяет на нахождение текущего прямоугольника внутри другого.
   *
   * @param rectangle - прямоугольник, в котором нужно проверить нахождение.
   * @return истина, если текущий прямоугольник находится внутри другого, иначе - ложь.
   */
  public boolean overlaps(Rectangle rectangle) {
    if (rightBottom.getX() < rectangle.leftTop.getX()) {
      return false;
    }
    if (leftTop.getX() > rectangle.rightBottom.getX()) {
      return false;
    }
    if (leftTop.getY() < rectangle.rightBottom.getY()) {
      return false;
    }
    if (rightBottom.getY() > rectangle.leftTop.getY()) {
      return false;
    }

    return true;
  }

  @Override
  public Rectangle clone() {
    try {
      return (Rectangle) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
