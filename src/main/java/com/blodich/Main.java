package com.blodich;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.blodich.indexer.CsvFileIndexer;
import com.blodich.indexer.FileIndexer;
import com.blodich.reader.*;
import com.blodich.search.CsvIndexSearchEngine;
import com.blodich.reader.CsvRandomAccessReader;
import com.blodich.search.IndexSearchEngine;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        String path = "C:\\airports.csv";
        int column = -1;
        try {
            if (args.length > 0)
                column = Integer.parseInt(args[0]);
            else
                column = getColumnFromSettings();
            column--;
            if (column < 0 || column > 13) {
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
        if (column == -1) return;
        FileIndexer fileIndexer = new CsvFileIndexer(path, column);
        IndexSearchEngine searchEngine = new CsvIndexSearchEngine();
        CsvRandomAccessReader csvRandomAccessReader = new FilteredCsvRandomAccessReader(path, searchEngine);
        FileProcessor filePreprocessor = new FileProcessor(fileIndexer, csvRandomAccessReader);
        try {
            filePreprocessor.preprocess();
            System.out.print("Введите строку: ");
            Scanner sc = new Scanner(System.in);
            String prefix = sc.nextLine();
            long start = System.currentTimeMillis();
            ArrayList<String> result = filePreprocessor.process(prefix);
            long end = System.currentTimeMillis();
            result.forEach(System.out::println);
            System.out.printf("Количество найденных записей: %d\n", result.size());
            System.out.printf("Затраченное время на поиск: %d мс\n", end-start);
        } catch (FileNotFoundException e) {
            System.out.println("Ошибка при поиске таблицы: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Ошибка при обработке раблицы: " + e.getMessage());
        }
    }

    public static int getColumnFromSettings() throws IOException {
        try(var res = Main.class.getClassLoader().getResourceAsStream("application.yml")) {
            YamlMapping yamlMapping = Yaml
                    .createYamlInput(res)
                    .readYamlMapping();
            return yamlMapping.integer("column");
        }
    }
}