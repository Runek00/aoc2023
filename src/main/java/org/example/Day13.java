package org.example;

import java.util.Arrays;

public class Day13 {

    record CompResult(boolean smudge, boolean madeit) {
    }

    public static long aoc13(String input) {
        return Arrays.stream(input.split("\r\n\r\n"))
                .map(part -> Arrays.stream(part.split("\r\n"))
                        .map(String::toCharArray)
                        .toArray(char[][]::new))
                .mapToLong(part -> getResult(part, false))
                .sum();
    }

    public static long aoc13a(String input) {
        return Arrays.stream(input.split("\r\n\r\n"))
                .map(part -> Arrays.stream(part.split("\r\n"))
                        .map(String::toCharArray)
                        .toArray(char[][]::new))
                .mapToLong(part -> getResult(part, true))
                .sum();
    }

    private static long getResult(char[][] part, boolean smudge) {
        long count = 100 * countMirroredRows(part, smudge);
        if (count == 0L) {
            count = countMirroredColumns(part, smudge);
        }
        return count;
    }

    private static long countMirroredRows(char[][] part, boolean initialSmudge) {
        FindStart:
        for (int i = 0; i < part.length - 1; i++) {
            CompResult compResult = sameRows(part, i, i + 1, initialSmudge);
            boolean smudge = compResult.smudge();
            if (compResult.madeit()) {
                int a = i - 1;
                int b = i + 2;
                while (a >= 0 && b < part.length) {
                    compResult = sameRows(part, a, b, smudge);
                    smudge = compResult.smudge();
                    if (!compResult.madeit()) {
                        continue FindStart;
                    }
                    a--;
                    b++;
                }
                if (!smudge) {
                    return (long) i + 1;
                }
            }
        }
        return 0L;
    }

    private static long countMirroredColumns(char[][] part, boolean initialSmudge) {
        FindStart:
        for (int i = 0; i < part[0].length - 1; i++) {
            CompResult compResult = sameColumns(part, i, i + 1, initialSmudge);
            boolean smudge = compResult.smudge();
            if (compResult.madeit()) {
                int a = i - 1;
                int b = i + 2;
                while (a >= 0 && b < part[0].length) {
                    compResult = sameColumns(part, a, b, smudge);
                    smudge = compResult.smudge();
                    if (!compResult.madeit()) {
                        continue FindStart;
                    }
                    a--;
                    b++;
                }
                if (!smudge) {
                    return (long) i + 1;
                }
            }
        }
        return 0L;
    }

    private static CompResult sameRows(char[][] part, int a, int b, boolean smudge) {
        for (int i = 0; i < part[0].length; i++) {
            if (part[a][i] != part[b][i]) {
                if (!smudge) {
                    return new CompResult(false, false);
                } else {
                    smudge = false;
                }
            }
        }
        return new CompResult(smudge, true);
    }

    private static CompResult sameColumns(char[][] part, int a, int b, boolean smudge) {
        for (char[] chars : part) {
            if (chars[a] != chars[b]) {
                if (!smudge) {
                    return new CompResult(false, false);
                } else {
                    smudge = false;
                }
            }
        }
        return new CompResult(smudge, true);
    }
}
