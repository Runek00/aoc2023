package org.example;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Day12 {
    public static long aoc12(Stream<String> input) {
        return input
                .flatMap(Day12::permutations)
                .count();
    }

    static Stream<String> permutations(String line) {
        String[] spLine = line.split(" ");
        Set<String> output = new HashSet<>();
        String orderList = spLine[1];
        String row = spLine[0];
        long hashCount = row.chars().filter(c -> c == '#').count();
        long missingHashes = Arrays.stream(orderList.split(",")).mapToLong(Long::parseLong).sum() - hashCount;
        permutate(0, row, missingHashes, output, orderList);
        return output.stream();
    }

    private static void permutate(int idx, String row, long listSum, Set<String> output, String orderList) {
        if (listSum == 0) {
            row = row.replace("?", ".");
            if (orderList.equals(orderList(row))) {
                output.add(row);
            }
            return;
        }
        for (int i = idx; i < row.length(); i++) {
            if (row.charAt(i) != '?') {
                continue;
            }
            String temp = row.substring(0, i) + "#" + row.substring(i + 1);
            permutate(i, temp, listSum - 1, output, orderList);
        }
    }

    static String orderList(String line) {
        StringBuilder output = new StringBuilder();
        int counter = 0;
        for (char c : line.toCharArray()) {
            if (c == '#') {
                counter++;
            } else if (counter != 0) {
                output.append(counter);
                output.append(',');
                counter = 0;
            }
        }
        if (counter > 0) {
            output.append(counter);
        } else {
            output.deleteCharAt(output.length() - 1);
        }
        return output.toString();
    }
}
