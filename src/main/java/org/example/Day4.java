package org.example;

import java.util.Arrays;
import java.util.stream.Stream;

public class Day4 {

    static long aoc4(Stream<String> input) {
        return input
                .map(line -> line.split(": ")[1])
                .mapToLong(Day4::lineToPoints)
                .sum();
    }

    private static long lineToPoints(String line) {
        String[] linetab = line.split(" \\| ");
        boolean[] arr = new boolean[100];
        Arrays.stream(linetab[0].split(" +"))
                .filter(s -> !s.isEmpty())
                .forEach(n -> arr[Integer.parseInt(n)] = true);
        int exp = -1;
        exp += (int) Arrays.stream(linetab[1].split(" +"))
                .filter(s -> !s.isEmpty())
                .filter(n -> arr[Integer.parseInt(n)])
                .count();
        return exp == -1 ? 0L : (long) Math.pow(2, exp);
    }
}
