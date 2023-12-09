package org.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day9 {

    public static long aoc9(Stream<String> input) {
        return input
                .map(list -> Arrays.stream(list.split(" ")).map(Long::parseLong).collect(Collectors.toList()))
                .mapToLong(arr -> getNextValue(arr, false))
                .sum();
    }

    public static long aoc9a(Stream<String> input) {
        return input
                .map(list -> Arrays.stream(list.split(" ")).map(Long::parseLong).collect(Collectors.toList()))
                .mapToLong(arr -> getNextValue(arr, true))
                .sum();
    }

    private static long getNextValue(List<Long> arr, boolean backwards) {
        LinkedList<List<Long>> histStack = new LinkedList<>();
        histStack.push(arr);
        while (!isZeros(histStack.peek())) {
            List<Long> oldDer = histStack.peek();
            ArrayList<Long> newDer = new ArrayList<>();
            if(oldDer == null) {
                break;
            }
            for (int i = 1; i < oldDer.size(); i++) {
                newDer.add(oldDer.get(i) - oldDer.get(i - 1));
            }
            histStack.push(newDer);
        }
        long change = 0L;
        while (!histStack.isEmpty()) {
            List<Long> currentList = histStack.pop();
            change = currentList.get(backwards ? 0 : (currentList.size() - 1)) + (backwards ? -1 : 1) * change;
        }
        return change;
    }

    private static boolean isZeros(List<Long> list) {
        if(list == null) {
            return true;
        }
        return list.get(0) == 0L && new HashSet<>(list).size() == 1;
    }
}
