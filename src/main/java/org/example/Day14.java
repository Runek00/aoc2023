package org.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day14 {
    public static long aoc14(Stream<String> input) {
        char[][] tab = input
                .map(String::toCharArray)
                .toArray(char[][]::new);
        for (int i = 0; i < 3; i++) {
            tab = rotateRight(tab);
        }
        tab = tiltWest(tab);
        tab = rotateRight(tab);
        return getLoad(tab);
    }

    public static long aoc14a(Stream<String> input) {
        char[][] tab = input
                .map(String::toCharArray)
                .toArray(char[][]::new);
        tab = cycles(tab, 1_000_000_000L);
        return getLoad(tab);
    }

    private static char[][] cycles(char[][] tab, long cycleCount) {
        Set<String> cache = new HashSet<>();
        long repeatStart = 0;
        for (long i = 0; i < cycleCount; i++) {
            String strVersion = Arrays.deepToString(tab);
            if (cache.contains(strVersion)) {
                cache = new HashSet<>();
                cache.add(strVersion);
                if (repeatStart == 0) {
                    repeatStart = i;
                } else {
                    return cycles(tab, (cycleCount - i) % (i - repeatStart));
                }
            } else {
                cache.add(strVersion);
            }
            tab = cycle(tab);
        }
        return tab;
    }

    private static char[][] cycle(char[][] tab) {
        for (int i = 0; i < 2; i++) {
            tab = rotateRight(tab);
        }
        for (int i = 0; i < 4; i++) {
            tab = rotateRight(tab);
            tab = tiltWest(tab);
        }
        for (int i = 0; i < 2; i++) {
            tab = rotateRight(tab);
        }
        return tab;
    }

    private static long getLoad(char[][] tab) {
        long counter = 0;
        for (int i = 0; i < tab.length; i++)
            for (int j = 0; j < tab[0].length; j++) {
                if (tab[i][j] == 'O') {
                    counter += tab.length - i;
                }
            }
        return counter;
    }

    record Row(int idx, char[] vals) {
    }

    static char[][] rotateRight(char[][] tab) {
        char[][] out = new char[tab[0].length][tab.length];
        IntStream.range(0, tab.length * tab[0].length)
                .parallel()
                .forEach(n -> out[n % tab[0].length][tab.length - 1 - (n / tab[0].length)] = tab[n / tab[0].length][n % tab[0].length]);
        return out;
    }

    private static char[][] tiltWest(char[][] tab) {
        return IntStream.range(0, tab.length)
                .boxed()
                .parallel()
                .map(i -> new Row(i, tilt(tab[i])))
                .sorted(Comparator.comparing(Row::idx))
                .map(Row::vals)
                .toArray(char[][]::new);
    }

    private static char[] tilt(char[] vals) {
        int rollDist = 0;
        for (int i = 0; i < vals.length; i++) {
            switch (vals[i]) {
                case '.' -> rollDist++;
                case '#' -> rollDist = 0;
                case 'O' -> {
                    vals[i] = '.';
                    vals[i - rollDist] = 'O';
                }
            }
        }
        return vals;
    }
}
