package org.example;

import java.util.stream.Stream;

public class Day3 {

    record CheckResult(int newJ, Long sumPart){}
    static String aoc3(Stream<String> input) {
        char [][] tab = input
                .map(String::toCharArray)
                .toArray(char[][]::new);
        Long sum = 0L;
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                if (Character.isDigit(tab[i][j])) {
                    CheckResult checkResult = checkNumber(tab, i, j);
                    j = checkResult.newJ();
                    sum += checkResult.sumPart();
                }
            }
        }
        return sum.toString();
    }

    private static CheckResult checkNumber(char[][] tab, int i, int j) {
        long numVal = 0L;
        int numLen = 0;
        for (int x = j; x < tab[0].length; x++) {
            if (Character.isDigit(tab[i][x])) {
                numVal *= 10;
                numVal += Long.parseLong(""+tab[i][x]);
                numLen++;
            } else {
                break;
            }
        }
        if (i > 0) {
            for (int x = Math.max(0, j-1); x < Math.min(tab[0].length, j+numLen+1); x++) {
                CheckResult result = getCheckResult(tab, i - 1, x, j, numLen, numVal);
                if (result != null) return result;
            }
        }
        if (i < tab.length-1) {
            for (int x = Math.max(0, j-1); x < Math.min(tab[0].length, j+numLen+1); x++) {
                CheckResult result = getCheckResult(tab, i + 1, x, j, numLen, numVal);
                if (result != null) return result;
            }
        }

        if (j > 0) {
            CheckResult result = getCheckResult(tab, i, j - 1, j, numLen, numVal);
            if (result != null) return result;
        }
        if (j+numLen < tab[0].length) {
            CheckResult result = getCheckResult(tab, i, j + numLen, j, numLen, numVal);
            if (result != null) return result;
        }
        return new CheckResult(j + numLen -1, 0L);
    }

    private static CheckResult getCheckResult(char[][] tab, int i, int x, int j, int numLen, long numVal) {
        char ch = tab[i][x];
        if (!Character.isDigit(ch) && ch != '.') {
            return new CheckResult(j + numLen - 1, numVal);
        }
        return null;
    }
}
