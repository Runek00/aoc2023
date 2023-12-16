package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day12 {

    private static final int repeat = 5;

    public static long aoc12(Stream<String> input) {
        return input
                .mapToLong(Day12::permutations)
                .sum();
    }

    public static long aoc12a(Stream<String> input) {
        return input
                .parallel()
                .map(Day12::unfold)
                .mapToLong(Day12::permutations)
                .sum();
    }

    private static String unfold(String line) {
        String[] spLine = line.split(" ");
        StringBuilder orderListBuilder = new StringBuilder();
        StringBuilder rowBuilder = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            orderListBuilder.append(spLine[1]);
            orderListBuilder.append(',');
            rowBuilder.append(spLine[0]);
            rowBuilder.append('?');
        }
        orderListBuilder.deleteCharAt(orderListBuilder.length() - 1);
        rowBuilder.deleteCharAt(rowBuilder.length() - 1);
        String orderList = orderListBuilder.toString();
        String row = rowBuilder.toString();
        return row + " " + orderList;
    }

    static long permutations(String line) {
        String[] spLine = line.split(" ");
        String row = "." + spLine[0];
        List<Long> orderList = Arrays.stream(spLine[1].split(",")).map(Long::parseLong).toList();
        return arrangements(row, orderList);
    }

    private static long arrangements(String row, List<Long> orderList) {
        if (onlyDots(row)) {
            if (orderList.isEmpty()) {
                return 1;
            } else if (!row.contains("?")) {
                return 0;
            }
        }
        if (row.charAt(0) == '#') {
            return 0;
        }
        if (orderList.isEmpty()) {
            return 0;
        }

        long orderSum = orderList.stream().mapToLong(l -> l).sum();
        long output = 0L;
        long l = orderList.getFirst();
        for (int i = 0; i < row.length() - orderSum + l - 1; i++) {
            if (row.charAt(i) == '#') {
                break;
            }
            if ((row.charAt(i) != '.' && row.charAt(i) != '?') || row.charAt(i + 1) != '#' && row.charAt(i + 1) != '?') {
                continue;
            }
            String[] spl = row.substring(i).split("[.?][?#]{" + l + "}", 2);
            if (!spl[0].isEmpty()) {
                continue;
            }
            String restOfRow = spl.length == 2 ? spl[1] : "";
            output += arrangements(restOfRow, orderList.subList(1, orderList.size()));
        }
        return output;
    }

    private static boolean onlyDots(String input) {
        return !input.contains("#");
    }
}
