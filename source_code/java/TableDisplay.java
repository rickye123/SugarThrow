package com.example.richa.sugarthrow;

import java.util.*;

class TableDisplay {

    void printTable(String tableName, List<List<String>> table) {

        int j = 0;

        System.out.println(tableName);
        while(j < table.size()) {
            int i = 0;
            while(i < table.get(j).size()) {
                System.out.printf("%-15s", table.get(j).get(i));
                i++;
            }
            j++;
            System.out.println();
        }
        System.out.println("\n");

    }
}
