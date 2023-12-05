package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day5 {

    static long aoc5(String input) {
        String[] splinput = input.split("\r\n\r\n");
        Stream<Long> seedStream = Arrays.stream(splinput[0].split(": ")[1].split(" ")).map(Long::parseLong);
        return getMinResult(splinput, seedStream);
    }

    static long aoc5a(String input) {
        String[] splinput = input.split("\r\n\r\n");
        String[] seedNums = splinput[0].split(": ")[1].split(" ");
        long cand = Long.MAX_VALUE;
        for (int i = 0; i < seedNums.length; i+=2) {
            Stream<Long> seedStream = LongStream.range(Long.parseLong(seedNums[i]), Long.parseLong(seedNums[i]) + Long.parseLong(seedNums[i+1])).boxed().parallel();
            cand = Math.min(cand, getMinResult(splinput, seedStream));
        }
        return cand;
    }

    private static long getMinResult(String[] splinput, Stream<Long> seedStream) {
        List<RangesMapper> transformers = getTransformers(splinput);
        for (RangesMapper transformer : transformers) {
            seedStream = seedStream.map(transformer::map);
        }
        return seedStream.mapToLong(n -> n).min().orElse(0);
    }

    private static List<RangesMapper> getTransformers(String[] splinput) {
        return IntStream.range(1, splinput.length)
                .mapToObj(i -> splinput[i].split(":\r\n")[1])
                .map(mapString -> Arrays.stream(mapString.split("\r\n"))
                        .map(s -> new RangeMapper(s.split(" ")))
                        .toList())
                .map(RangesMapper::new)
                .toList();
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
