package org.example;

import java.util.Arrays;

public class Day15 {
    public static long aoc15(String input) {
        return Arrays.stream(input.split(","))
                .mapToLong(Day15::hash)
                .sum();
    }

    private static long hash(String s) {
        long output = 0L;
        for (char c : s.toCharArray()) {
            output = ((output + c) * 17) % 256;
        }
        return output;
    }
}
