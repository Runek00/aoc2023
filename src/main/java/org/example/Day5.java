package org.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Day5 {

    static long aoc5(String input) {
        String[] splinput = input.split("\r\n\r\n");
        Stream<Long> seedStream = Arrays.stream(splinput[0].split(": ")[1].split(" ")).map(Long::parseLong);
        return getMinResult(splinput, seedStream);
    }

    record Range(long start, long end) {
    }

    static long aoc5a(String input) {
        String[] splinput = input.split("\r\n\r\n");
        String[] seedNums = splinput[0].split(": ")[1].split(" ");
        List<Range> ranges = new ArrayList<>();
        for (int i = 0; i < seedNums.length; i += 2) {
            ranges.add(new Range(Long.parseLong(seedNums[i]), Long.parseLong(seedNums[i]) + Long.parseLong(seedNums[i + 1])));
        }
        ranges = normalizeRanges(ranges);
        List<RangesMapper> transformers = getTransformers(splinput);
        for (RangesMapper transformer : transformers) {
            ranges = mapRanges(ranges, transformer);
        }
        return ranges.get(0).start();
    }

    static List<Range> mapRanges(List<Range> ranges, RangesMapper mappers) {
        List<Range> output = new ArrayList<>();
        for (Range range : ranges) {
            for (RangeMapper mapRange : mappers.ranges()) {
                if (mapRange.inRange(range.start())) {
                    if (mapRange.inRange(range.end())) {
                        output.add(new Range(range.start() + mapRange.diff(), range.end() + mapRange.diff()));
                        range = null;
                        break;
                    } else {
                        output.add(new Range(range.start() + mapRange.diff(), mapRange.targetEnd()));
                        range = new Range(mapRange.sourceEnd(), range.end());
                    }
                } else if (mapRange.inRange(range.end())) {
                    output.add(new Range(range.start(), mapRange.sourceStart()));
                    output.add(new Range(mapRange.targetStart(), range.end() + mapRange.diff()));
                    range = null;
                    break;
                }
            }
            if (range != null) {
                output.add(range);
            }
        }
        return normalizeRanges(output);
    }

    static long aoc5aOld(String input) {
        String[] splinput = input.split("\r\n\r\n");
        String[] seedNums = splinput[0].split(": ")[1].split(" ");
        long cand = Long.MAX_VALUE;
        List<Range> ranges = new ArrayList<>();
        for (int i = 0; i < seedNums.length; i += 2) {
            ranges.add(new Range(Long.parseLong(seedNums[i]), Long.parseLong(seedNums[i]) + Long.parseLong(seedNums[i + 1])));
        }
        ranges = normalizeRanges(ranges);
        for (Range range : ranges) {
            Stream<Long> seedStream = LongStream.range(range.start(), range.end()).boxed().parallel();
            cand = Math.min(cand, getMinResult(splinput, seedStream));
        }
        return cand;
    }

    private static List<Range> normalizeRanges(List<Range> ranges) {
        TreeMap<Long, Range> byStarts = new TreeMap<>(ranges.stream().collect(Collectors.toMap(Range::start, range -> range)));
        Range firstRange = byStarts.pollFirstEntry().getValue();
        List<Range> output = new ArrayList<>();
        long start = firstRange.start();
        long end = firstRange.end();
        while (!byStarts.isEmpty()) {
            Range newRange = byStarts.pollFirstEntry().getValue();
            if (newRange.start() <= end) {
                end = Math.max(end, newRange.end());
            } else {
                output.add(new Range(start, end));
                start = newRange.start();
                end = newRange.end();
            }
        }
        output.add(new Range(start, end));
        return output;
    }

    private static long getMinResult(String[] splinput, Stream<Long> seedStream) {
        List<RangesMapper> transformers = getTransformers(splinput);
        Range minmax = transformers.stream()
                .collect(Collectors.teeing(
                        Collectors.minBy(Comparator.comparing(RangesMapper::minVal)),
                        Collectors.maxBy(Comparator.comparing(RangesMapper::maxVal)),
                        (min, max) -> min.isPresent() && max.isPresent() ? new Range(min.get().minVal(), max.get().maxVal()) : null));
        if (minmax != null) {
            seedStream = seedStream.filter(l -> l > minmax.start()).filter(l -> l < minmax.end());
        }
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
                        .sorted(Comparator.comparing(RangeMapper::sourceStart))
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

        long minVal() {
            return ranges.stream().mapToLong(range -> Math.max(range.targetStart(), range.sourceStart())).min().orElse(Long.MAX_VALUE);
        }

        long maxVal() {
            return ranges.stream().mapToLong(range -> Math.max(range.targetStart(), range.sourceStart()) + range.length()).max().orElse(Long.MIN_VALUE);
        }
    }

    record RangeMapper(long targetStart, long sourceStart, long length, long diff) {
        RangeMapper(String[] line) {
            this(Long.parseLong(line[0]), Long.parseLong(line[1]), Long.parseLong(line[2]));
        }

        RangeMapper(long targetStart, long sourceStart, long length) {
            this(targetStart, sourceStart, length, targetStart - sourceStart);
        }

        boolean inRange(long point) {
            return point >= sourceStart && point < sourceEnd();
        }

        long map(long input) {
            if (inRange(input)) {
                return input + diff;
            } else {
                return input;
            }
        }

        public long sourceEnd() {
            return sourceStart + length;
        }

        public long targetEnd() {
            return targetStart + length;
        }
    }
}
