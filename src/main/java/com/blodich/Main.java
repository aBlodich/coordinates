package com.blodich;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.blodich.indexer.RTreeFileIndexer;
import com.blodich.reader.FileProcessor;
import com.blodich.reader.FilteredCsvRandomAccessReader;
import com.blodich.search.CsvIndexSearchEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Collectors;
import tree.geometry.Point;
import tree.RTree;

public class Main {
  public static void main(String[] args) {
    int[] columns = null;
    try {
      if (args.length > 2) {
        columns = new int[3];
        columns[0] = Integer.parseInt(args[0]) - 1;
        columns[1] = Integer.parseInt(args[1]) - 1;
        columns[2] = Integer.parseInt(args[2]) - 1;
      }
      else {
        columns = getColumnsFromSettings();
        columns[0]--;
        columns[1]--;
        columns[2]--;
      }
      if ((columns[0] < 0 || columns[0] > 13) && (columns[1] < 0 || columns[1] > 13) && (columns[2] < 0 || columns[2] > 13)) {
        System.out.println("Значение аргумента для индексация колонки должно лежать в пределах [1:14]");
        return;
      }
    } catch (FileNotFoundException e) {
      System.out.println("Ошибка при поиске файла настроек: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("Ошибка при обработке файла настроек: " + e.getMessage());
    } catch (NumberFormatException e) {
      System.out.println("Ошибка значения аргумета для индексации колонки: " + e.getMessage());
    }
    if (columns == null) return;
    RTreeFileIndexer fileIndexer = new RTreeFileIndexer(columns);
    try {
      RTree tree = fileIndexer.index();
      Scanner sc = new Scanner(System.in);
      System.out.print("Введите широту: ");
      double latitude = Double.parseDouble(sc.nextLine());
      System.out.print("Введите долготу: ");
      double longitude = Double.parseDouble(sc.nextLine());
      double radius = getRadiusFromSettings();
      long start = System.currentTimeMillis();
      Point targetPoint = new Point(latitude, longitude);
      FileProcessor fileProcessor = new FileProcessor(new RTreeFileIndexer(columns),
          new FilteredCsvRandomAccessReader("", new CsvIndexSearchEngine()));
      fileProcessor.preprocess();
      var result = fileProcessor.process(targetPoint, radius);
      long end = System.currentTimeMillis();
      System.out.println("Результат:");
      result.forEach(System.out::println);
      System.out.printf("Количество найденных записей: %d\n", result.size());
      System.out.printf("Затраченное время на поиск: %d мс\n", end-start);
    } catch (FileNotFoundException e) {
      System.out.println("Ошибка при поиске таблицы: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("Ошибка при обработке раблицы: " + e.getMessage());
    }
  }

  /**
   * Читает колонки для индексации из файла настроек.
   * @return - колонки.
   * @throws IOException - в случае, если произошла проблема при работе с файлом настроек
   */
  public static int[] getColumnsFromSettings() throws IOException {
    try(var res = Main.class.getClassLoader().getResourceAsStream("application.yml")) {
      YamlMapping yamlMapping = Yaml
          .createYamlInput(res)
          .readYamlMapping();
      int[] columns = new int[3];
      columns[0] = yamlMapping.integer("latitude");
      columns[1] = yamlMapping.integer("longitude");
      columns[2] = yamlMapping.integer("elevation");
      return columns;
    }
  }

  /**
   * Читает радиус из файла настроек.
   * @return - радиус.
   * @throws IOException - в случае, если произошла проблема при работе с файлом настроек
   */
  public static double getRadiusFromSettings() throws IOException {
    try(var res = Main.class.getClassLoader().getResourceAsStream("application.yml")) {
      YamlMapping yamlMapping = Yaml
          .createYamlInput(res)
          .readYamlMapping();
      return yamlMapping.doubleNumber("radius");
    }
  }


}