package com.blodich.indexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import tree.geometry.Point;
import tree.RTree;

public class RTreeFileIndexer{
  private final int[] columns;

  public RTreeFileIndexer(int[] columns) {
    this.columns = columns;
  }

  public RTree index() throws IOException {
    try (BufferedReader br =
             new BufferedReader(
                 new InputStreamReader(
                     RTreeFileIndexer.class.getClassLoader().getResourceAsStream("airports.csv")))) {
      RTree tree = new RTree(4);
      String row;
      long seek = 0;
      while ((row = br.readLine()) != null) {
        String[] splited = row.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        double latitude = Double.parseDouble(splited[columns[0]]);
        double longitude = Double.parseDouble(splited[columns[1]]);
        double elevation = Double.parseDouble(splited[columns[2]]);
        Point point = new Point(latitude, longitude, seek);
        seek += (row.getBytes(StandardCharsets.UTF_8).length + 2);
        tree.add(point);
      }
      return tree;
    }
  }
}
