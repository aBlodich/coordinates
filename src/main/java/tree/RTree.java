package tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import tree.geometry.Point;
import tree.geometry.Rectangle;

/**
 * Реализация R-дерева для индексирования пространственных данных.
 */
public class RTree {
  /**
   * Узел, представляющий собой MBR в случае, если он не лист.
   * Если же это лист, то в нем хранятся исходные данные - точки.
   */
  @Getter
  @Setter
  private static class Node {
    // Индекс в массиве родительского элемента
    private int index;

    // Ограничивающий прямоугольник
    private Rectangle mbr;

    // Ссылка на родительский элемент
    private Node parent;

    // Массив потомков
    private Node[] children;

    // Количество потомков
    private int keyCount;

    // Уровень, на котором находится узел
    private int height;

    public Node(int maxKeysPerNodes, Node parent) {
      this.parent = parent;
      children = new Node[maxKeysPerNodes];
    }

    /**
     * Добавляет дочерний узел к родительскому элементу.
     *
     * @param child - вставляемый дочерний узел.
     */
    public void addChild(Node child) {
      setChild(keyCount, child);
      keyCount++;
    }

    /**
     * Устанавливает дочерний узел в нужный индекс. И изменяет MBR с учетом нового ребенка.
     *
     * @param index - индекс.
     *
     * @param child - дочерний узел.
     */
    private void setChild(int index, Node child) {
      child.parent = this;
      child.index = index;
      children[index] = child;
      if (mbr == null) {
        mbr = new Rectangle(child.mbr.getLeftTop(), child.mbr.getRightBottom());
      } else {
        mbr.unionRectangle(child.mbr);
      }

      height = child.height + 1;
    }


    /**
     * Выбирать подходящий лист для всавки прямоугольника.
     *
     * @param rectangle - вставляемый прямоугольник.
     *
     * @return подходящий лист.
     */
    public Node chooseLeaf(Rectangle rectangle) {
      return Arrays.stream(children)
          .limit(keyCount)
          .min(Comparator.comparingDouble((Node r) -> r.mbr.unionArea(rectangle))
              .thenComparing((Node r) -> r.mbr.area()))
          .orElse(null);
    }

    /**
     * Проверяет, яляется ли узел листом с данными.
     *
     * @return true, если узел - лист, иначе - false.
     */
    public boolean isLeaf() {
      return mbr.getDataPoint() != null || children[0].mbr.getDataPoint() != null;
      //return mbr.getDataPoint() != null;
    }
  }

  // Максимальное количество дочерних элементов у родителя
  private int maxKeysPerNode;

  // Минимальное количество дочерних элементов у родителя
  private int minKeysPerNode;

  // Корень дерева
  private Node root;

  // Количество элементов в дереве
  private int count;

  public RTree(int maxKeysPerNode) {
    this.maxKeysPerNode = maxKeysPerNode;
    this.minKeysPerNode = maxKeysPerNode / 2;
  }

  /**
   * Добавляет новую точку в дерево.
   *
   * @param newPoint - новая точка.
   */
  public void add(Point newPoint) {
    var newNode = new Node(maxKeysPerNode, null);
    newNode.setMbr(new Rectangle(newPoint, newPoint));
    newNode.mbr.setDataPoint(newPoint);

    insertToLeaf(newNode);
    count++;
  }

  /**
   * Промежуточный метод добавления. Проверяет корень дерева, если он пуст,
   * то элемент добавляется в него. Иначе выбирается нужный лист,
   * куда можно ставить новый элемент.
   * После того, как лист был выбран, происходит разделение дерева для его балансировки.
   *
   * @param newNode
   */
  private void insertToLeaf(Node newNode) {
    if (root == null) {
      root = new Node(maxKeysPerNode, null);
      root.addChild(newNode);
      return;
    }

    var leafToAdd = chooseLeaf(root, newNode);
    addAndSplit(leafToAdd, newNode);
  }

  /**
   * Добавляет элемент и распределеяет дерево рекурсивно.
   *
   * Шаг 1:
   * Для начала идет проверка, есть ли место среди потомков листа для нового элемента.
   * Если это так, то элемент добавляется и происходит перераспределение MBR. Выход из метода.
   *
   * Шаг 2:
   * Если предыдущее условие не выполнилось, то создается список 'e' из всех потомков + новый потомок листа.
   * И выбираются две точки 'e1' и 'e2', растояние между которыми максимальное среди всех потомков.
   *
   * Шаг 3:
   * Проверяем оставшиеся элементы в списке потомков один за другим и кладем их в e1 или e2, в зависимости от того,
   * какой из элементов этих узлов потребует минимального увелечения области.
   *
   * @param node - узел, куда нужно внести улемент.
   * @param newNode - вносимый элемент.
   */
  private void addAndSplit(Node node, Node newNode) {
    // Шаг 1
    if (node.keyCount < maxKeysPerNode) {
      node.addChild(newNode);
      expandAncestorMBRs(node);
      return;
    }

    // Шаг 2
    List<Node> e = new LinkedList<>();
    e.add(newNode);
    e.addAll(Arrays.asList(node.children));

    Node[] distantPairs = getDistantPairs(e);

    Node e1 = new Node(maxKeysPerNode, null);
    Node e2 = new Node(maxKeysPerNode, null);

    e1.addChild(distantPairs[0]);
    e2.addChild(distantPairs[1]);

    e = e.stream().filter(el -> el != distantPairs[0] && el != distantPairs[1]).collect(Collectors.toList());
    int remaining = e.size();
    var iterator = e.listIterator(e.size());
    while (iterator.hasPrevious() && e.size() > 0) {
      Node current = iterator.previous();

      double leftEnlargementArea = e1.mbr.unionArea(current.mbr);
      double rightEnlargementArea = e2.mbr.unionArea(current.mbr);

      if (leftEnlargementArea == rightEnlargementArea) {
        double leftArea = e1.mbr.area();
        double rightArea = e2.mbr.area();

        if (leftArea == rightArea) {
          if (e1.keyCount < e2.keyCount) {
            e1.addChild(current);
          } else {
            e2.addChild(current);
          }
        } else if (leftArea < rightArea) {
          e1.addChild(current);
        } else {
          e2.addChild(current);
        }
      } else if (leftEnlargementArea < rightEnlargementArea) {
        e1.addChild(current);
      } else {
        e2.addChild(current);
      }

      iterator.remove();

      remaining--;

      //Впихиваем неназначенные никуда записи в e1 или e2, чтобы заполнить там оставшееся место из минимума элементов
      if (e1.keyCount == minKeysPerNode - remaining) {
        for (var entry :
            e) {
          e1.addChild(entry);
        }
        e.clear();
      } else if (e2.keyCount == minKeysPerNode - remaining) {
        for (var entry :
            e) {
          e2.addChild(entry);
        }
        e.clear();
      }
    }

    Node parent = node.parent;
    if (parent != null) {
      //меняем текущий узел на e1
      parent.setChild(node.index, e1);
      //продолжаем вставку и перераспределение дерева
      addAndSplit(parent, e2);
    } else {
      //элемент является корнем, просто вставляем e1 и e2
      root = new Node(maxKeysPerNode, null);
      root.addChild(e1);
      root.addChild(e2);
    }
  }

  /**
   * Растягиваем MBR у предков узла
   * @param node - узел, у предков, которого нужно изменить MBR.
   */
  private void expandAncestorMBRs(Node node) {
    while (node.parent != null) {
      node.parent.mbr.unionRectangle(node.mbr);
      node.parent.height = node.height + 1;
      node = node.parent;
    }
  }

  /**
   * Находит самые удаленные друг от друга узлы.
   *
   * @param nodes - список узлов
   * @return 2 самых удаленных друг от друга узла.
   */
  private Node[] getDistantPairs(List<Node> nodes) {
    Node[] result = new Node[2];

    double maxArea = -1;
    var iterator = nodes.iterator();
    for (int i = 0; i < nodes.size(); i++) {
      Node first = iterator.next();
      var iterator2 = nodes.listIterator(i + 1);
      for (int j = i + 1; j < nodes.size(); j++) {
        Node second = iterator2.next();
        double currentArea = first.mbr.unionArea(second.mbr);
        if (currentArea > maxArea) {
          result[0] = first;
          result[1] = second;
          maxArea = currentArea;
        }
      }
    }
    return result;
  }

  /**
   * Выбирает лист для вставки нового элемента.
   *
   * @param node - предполагаемый узел, в который можно вставить новый элемент.
   * @param newNode - нвоый элемент.
   * @return - найденный узел, в который можно вставить элемент.
   */
  private Node chooseLeaf(Node node, Node newNode) {
    if (node.isLeaf()) {
      return node;
    }

    return chooseLeaf(node.chooseLeaf(newNode.mbr), newNode);
  }

  /**
   * Ищет все точки в пределах заданного прямоугольника.
   *
   * @param target - прямоугольник, в пределах которого нужно осуществлять поиск.
   * @return найденные точки
   */
  public List<Point> range(Rectangle target) {
    return range(root, target, new ArrayList<Point>());
  }

  /**
   * Рекурсивный поиск всех точек в пределах заданного прямоугольника.
   * @param current - текущий узел для поиска.
   * @param target - прямоугольник, в пределах которого нужно осуществлять поиск.
   * @param result - список со всем точками.
   * @return найденные точки.
   */
  private List<Point> range(Node current, Rectangle target, List<Point> result) {
    if (current.isLeaf()) {
      for (int i = 0; i < current.keyCount; i++) {
        Node node = current.children[i];
        if (target.overlaps(node.mbr)) {
          result.add(node.mbr.getDataPoint());
        }
      }
    }

    for (int i = 0; i < current.keyCount; i++) {
      Node node = current.children[i];
      if (node.mbr.overlaps(target)) {
        range(node, target, result);
      }
    }

    return result;
  }

  /**
   * Печатает дерево (для дебага)
   */
  public void printTree() {
    printTree(root);
  }

  /**
   * Обходит дерево рекурсивно для его распечатки.
   * @param node - текущий узел.
   */
  private void printTree(Node node) {
    if (node.isLeaf()) {
      for (int i = 0; i < node.keyCount; i++) {
        System.out.println(node.children[i].mbr.getDataPoint());
      }
    }
    for (int i = 0; i < node.keyCount; i++) {
      printTree(node.children[i]);
    }
  }
}
