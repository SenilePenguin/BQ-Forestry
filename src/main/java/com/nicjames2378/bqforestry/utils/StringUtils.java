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

    public static String padInMiddle(String a, String b, int totalLength) {
        // If the combined strings are already longer than the total length, add 3 spaces for readability and return it
        if (a.length() + b.length() > totalLength) return a.concat(getSpaces(3)).concat(b);

        int remaining = totalLength - a.length() - b.length();

        // JVM optimizes this to use a StringBuilder, so we'll keep it short here for readability
        return a + getSpaces(remaining) + b;
    }

    private static String getSpaces(int amount) {
        return getCharacterRepeated(' ', amount);
    }

    private static String getCharacterRepeated(char character, int amount) {
        // '\u0000' is the "null character". It is not a null value, but instead the default value for a char when not declared.
        return new String(new char[amount]).replace('\u0000', character);
    }
}
