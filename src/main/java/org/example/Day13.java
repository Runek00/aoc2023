package org.example;

import java.util.Arrays;

public class Day13 {

    public static long aoc13(String input) {
        return Arrays.stream(input.split("\r\n\r\n"))
                .map(part -> {
                    return Arrays.stream(part.split("\r\n"))
                            .map(String::toCharArray)
                            .toArray(char[][]::new);
                })
                .mapToLong(part -> getResult(part))
                .sum();
    }

    private static long getResult(char[][] part) {
        long count = 100*countMirroredRows(part);
        if (count == 0L){
            count = countMirroredColumns(part);
        }
            return count;
    }

    private static long countMirroredRows(char[][] part) {
        FindStart:
        for (int i = 0; i < part.length-1; i++) {
            if (Arrays.equals(part[i], part[i+1])) {
                int a = i;
                int b = i+1;
                while (a >= 0 && b < part.length) {
                    if (!Arrays.equals(part[a], part[b])) {
                        continue FindStart;
                    }
                    a--;
                    b++;
                }
                return (long) i+1;
            }
        }
        return 0L;
    }

    private static long countMirroredColumns(char[][] part) {
        FindStart:
        for (int i = 0; i < part[0].length-1; i++) {
            if (sameColumns(part, i, i+1)) {
                int a = i;
                int b = i+1;
                while (a >= 0 && b < part[0].length) {
                    if (!sameColumns(part, a, b)) {
                        continue FindStart;
                    }
                    a--;
                    b++;
                }
                return (long) i+1;
            }
        }
        return 0L;
    }

    private static boolean sameColumns(char[][] part, int a, int b) {
        for (char[] part1 : part) {
            if (part1[a] != part1[b]) {
                return false;
            }
        }
        return true;
    }
}
