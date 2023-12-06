package org.example;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

public class Day6 {
    static long aoc6(String input) {
        String timeLine = input.split("\n")[0];
        String distLine = input.split("\n")[1];
        long[] times = getArray(timeLine);
        long[] distances = getArray(distLine);
        return IntStream.range(0, times.length)
                .mapToLong(i -> getMargin(i, times, distances))
                .reduce(1L, (a, b) -> a * b);
    }

    static long aoc6a(String input) {
        return aoc6(input.replace(" ", ""));
    }

    private static long getMargin(int i, long[] times, long[] distances) {
        double sqdel = Math.sqrt(times[i] * times[i] - (4 * distances[i]));
        long t1 = (long) Math.ceil((times[i] - sqdel) / 2);
        if(t1 * (times[i] - t1) == distances[i]) {
            t1++;
        }
        long t2 = (long) Math.floor((times[i] + sqdel) / 2);
        if(t2 * (times[i] - t2) == distances[i]) {
            t2--;
        }
        return t2 - t1 + 1;
    }

    private static long[] getArray(String timeLine) {
        return Arrays.stream(timeLine.split(":")[1].trim().split(" +"))
                .filter(Objects::nonNull)
                .mapToLong(Long::parseLong)
                .toArray();
    }
}
