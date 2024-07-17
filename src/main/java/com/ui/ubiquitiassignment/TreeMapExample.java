package com.ui.ubiquitiassignment;

import java.util.TreeMap;

public class TreeMapExample {
    public static void main(String[] args) {
        TreeMap<String, String> treeMap = new TreeMap<>();

        treeMap.put("Charlie", "Employee");
        treeMap.put("Alice", "Manager");
        treeMap.put("Bob", "Team Lead");
        treeMap.put("Dave", "Intern");

        System.out.println("TreeMap entries:");
        System.out.println(treeMap);
//        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
//            System.out.println(entry.getKey() + " : " + entry.getValue());
//        }
    }
}
