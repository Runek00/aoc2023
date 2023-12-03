package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Day3 {

    record CheckResult(int newJ, Long partNum, String gearId) {
    }

    static long aoc3(Stream<String> input) {
        char[][] tab = input
                .map(String::toCharArray)
                .toArray(char[][]::new);
        long sum = 0L;
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
        return sum;
    }

    static long aoc3a(Stream<String> input) {
        char[][] tab = input
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
                .mapToLong(l -> l.get(0) * l.get(1))
                .sum();
    }

    private static CheckResult checkNumber(char[][] tab, int i, int j, Predicate<Character> characterPredicate) {
        long numVal = 0L;
        int numLen = 0;
        for (int x = j; x < tab[0].length; x++) {
            if (Character.isDigit(tab[i][x])) {
                numVal *= 10;
                numVal += Long.parseLong("" + tab[i][x]);
                numLen++;
            } else {
                break;
            }
        }
        int numEnd = j + numLen - 1;

        if (i > 0) {
            for (int x = Math.max(0, j - 1); x < Math.min(tab[0].length, numEnd + 2); x++) {
                CheckResult result = getCheckResult(tab, i - 1, x, numEnd, numVal, characterPredicate);
                if (result != null) return result;
            }
        }
        if (i < tab.length - 1) {
            for (int x = Math.max(0, j - 1); x < Math.min(tab[0].length, numEnd + 2); x++) {
                CheckResult result = getCheckResult(tab, i + 1, x, numEnd, numVal, characterPredicate);
                if (result != null) return result;
            }
        }
        if (j > 0) {
            CheckResult result = getCheckResult(tab, i, j - 1, numEnd, numVal, characterPredicate);
            if (result != null) return result;
        }
        if (j + numLen < tab[0].length) {
            CheckResult result = getCheckResult(tab, i, j + numLen, numEnd, numVal, characterPredicate);
            if (result != null) return result;
        }
        return new CheckResult(numEnd, 0L, null);
    }

    private static CheckResult getCheckResult(char[][] tab, int i, int x, int numEnd, long numVal, Predicate<Character> pred) {
        char ch = tab[i][x];
        if (pred.test(ch)) {
            return new CheckResult(numEnd, numVal, i + "|" + x);
        }
        return null;
    }
}
