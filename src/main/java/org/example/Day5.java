package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day5 {

    static long aoc5(String input) {
        String[] splinput = input.split("\r\n\r\n");
        Stream<Long> seedStream = Arrays.stream(splinput[0].split(": ")[1].split(" ")).map(Long::parseLong);
        List<RangesMapper> transformers = IntStream.range(1, splinput.length)
                .mapToObj(i -> splinput[i].split(":\r\n")[1])
                .map(mapString -> Arrays.stream(mapString.split("\r\n"))
                        .map(s -> new RangeMapper(s.split(" ")))
                        .toList())
                .map(RangesMapper::new)
                .toList();
        for (RangesMapper transformer : transformers) {
            seedStream = seedStream.map(transformer::map);
        }
        return seedStream.mapToLong(n -> n).min().orElse(0);
    }

    record RangesMapper(List<RangeMapper> ranges) {
        long map(long input) {
            for (RangeMapper range : ranges) {
                long output = range.map(input);
                if (input != output) {
                    return output;
                }
            }
            return input;
        }
    }

    record RangeMapper(long targetStart, long sourceStart, long length, long diff) {
        RangeMapper(String[] line) {
            this(Long.parseLong(line[0]), Long.parseLong(line[1]), Long.parseLong(line[2]));
        }

        RangeMapper(long targetStart, long sourceStart, long length) {
            this(targetStart, sourceStart, length, targetStart - sourceStart);
        }

        long map(long input) {
            if (input >= sourceStart && input < sourceStart + length) {
                return input + diff;
            } else {
                return input;
            }
        }
    }
}
