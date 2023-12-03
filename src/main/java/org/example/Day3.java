package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day3 {

    record CheckResult(int newJ, Long partNum, String gearId){}
    static String aoc3(Stream<String> input) {
        char [][] tab = input
                .map(String::toCharArray)
                .toArray(char[][]::new);
        Long sum = 0L;
        Predicate<Character> isSymbol = (c) -> !Character.isDigit(c) && c != '.';
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                if (Character.isDigit(tab[i][j])) {
                    CheckResult checkResult = checkNumber(tab, i, j, isSymbol);
                    j = checkResult.newJ();
                    sum += checkResult.partNum();
                }
            }
        }
        return sum.toString();
    }

    static String aoc3a(Stream<String> input) {
        char [][] tab = input
                .map(String::toCharArray)
                .toArray(char[][]::new);
        Predicate<Character> isGear = (c) -> c == '*';
        Map<String, List<Long>> gearMap = new HashMap<>();
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                if (Character.isDigit(tab[i][j])) {
                    CheckResult checkResult = checkNumber(tab, i, j, isGear);
                    if (checkResult.partNum() > 0) {
                        List<Long> partList = gearMap.getOrDefault(checkResult.gearId(), new ArrayList<>());
                        partList.add(checkResult.partNum());
                        gearMap.put(checkResult.gearId(), partList);
                    }
                    j = checkResult.newJ();
                }
            }
        }
        return gearMap.values().stream()
                .filter(l -> l.size() == 2)
                .map(l -> l.get(0) * l.get(1))
                .reduce(0L, Long::sum)
                .toString();
    }



    private static CheckResult checkNumber(char[][] tab, int i, int j, Predicate<Character> characterPredicate) {
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
                CheckResult result = getCheckResult(tab, i - 1, x, j, numLen, numVal, characterPredicate);
                if (result != null) return result;
            }
        }
        if (i < tab.length-1) {
            for (int x = Math.max(0, j-1); x < Math.min(tab[0].length, j+numLen+1); x++) {
                CheckResult result = getCheckResult(tab, i + 1, x, j, numLen, numVal, characterPredicate);
                if (result != null) return result;
            }
        }

        if (j > 0) {
            CheckResult result = getCheckResult(tab, i, j - 1, j, numLen, numVal, characterPredicate);
            if (result != null) return result;
        }
        if (j+numLen < tab[0].length) {
            CheckResult result = getCheckResult(tab, i, j + numLen, j, numLen, numVal, characterPredicate);
            if (result != null) return result;
        }
        return new CheckResult(j + numLen -1, 0L, null);
    }

    private static CheckResult getCheckResult(char[][] tab, int i, int x, int j, int numLen, long numVal, Predicate<Character> pred) {
        char ch = tab[i][x];
        if (pred.test(ch)) {
            return new CheckResult(j + numLen - 1, numVal, i + "|" + x);
        }
        return null;
    }
}
