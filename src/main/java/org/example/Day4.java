package org.example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class Day4 {

    static long aoc4(Stream<String> input) {
        return input
                .map(line -> line.split(": ")[1])
                .mapToInt(Day4::getWinCount)
                .mapToLong(Day4::countToPoints)
                .sum();
    }

    static long aoc4a(Stream<String> input) {
        HashMap<Integer, Long> copyMap = new HashMap<>();
        return input
                .mapToLong(line -> processLine(line, copyMap))
                .sum();
    }

    private static Long processLine(String line, HashMap<Integer, Long> copyMap) {
        int cardId = getCardId(line);
        int wins = getWinCount(line.split(": ")[1]);
        for (int i = cardId + 1; i <= cardId + wins; i++) {
            copyMap.put(i, copyMap.getOrDefault(i, 1L) + copyMap.getOrDefault(cardId, 1L));
        }
        return copyMap.getOrDefault(cardId, 1L);
    }

    private static int getCardId(String line) {
        return Integer.parseInt(line.split(": ")[0].split(" +")[1]);
    }

    private static int getWinCount(String line) {
        String[] linetab = line.split(" \\| ");
        boolean[] arr = new boolean[100];
        Arrays.stream(linetab[0].split(" +"))
                .filter(s -> !s.isEmpty())
                .forEach(n -> arr[Integer.parseInt(n)] = true);
        return (int) Arrays.stream(linetab[1].split(" +"))
                .filter(s -> !s.isEmpty())
                .filter(n -> arr[Integer.parseInt(n)])
                .count();
    }

    private static long countToPoints(Integer count) {
        int exp = count - 1;
        return exp == -1 ? 0L : (long) Math.pow(2, exp);
    }
}
