package com.nicjames2378.bqforestry.utils;

public class StringUtils {
    public static int indexOfFirstCapital(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isUpperCase(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    public static String capitalizeFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
