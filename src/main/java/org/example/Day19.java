package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 {

    record Part(int x, int m, int a, int s) {
    }

    record Workflow(String name, Function<Part, String> condition) {
    }


    static class PartProcessor {
        Map<String, Function<Part, String>> flowMap;
        List<Part> accepted = new ArrayList<>();

        PartProcessor(Map<String, Function<Part, String>> flowMap) {
            this.flowMap = flowMap;
            flowMap.put("R", (Part part) -> null);
            flowMap.put("A", (Part part) -> {
                accepted.add(part);
                return null;
            });

        }

        long processParts(Stream<Part> parts) {
            parts.forEach(part -> {
                String result = "in";
                do {
                    result = flowMap.get(result).apply(part);
                } while (result != null);
            });
            return accepted.stream().mapToLong(part -> part.x() + part.m() + part.a() + part.s()).sum();
        }

    }

    public static long aoc19(String input) {
        String[] splinput = input.split("\r\n\r\n");
        PartProcessor flowBoi = prepareFlows(splinput[0]);
        Stream<Part> parts = prepareParts(splinput[1]);
        return flowBoi.processParts(parts);
    }

    private static Stream<Part> prepareParts(String s) {
        return Arrays.stream(s.split("\r\n")).map(Day19::partFromLine);
    }

    private static Part partFromLine(String line) {
        int x = 0;
        int m = 0;
        int a = 0;
        int s = 0;
        line = line.substring(1, line.length() - 1);
        for (String cat : line.split(",")) {
            switch (cat.charAt(0)) {
                case 'x' -> x = Integer.parseInt(cat.substring(2));
                case 'm' -> m = Integer.parseInt(cat.substring(2));
                case 'a' -> a = Integer.parseInt(cat.substring(2));
                case 's' -> s = Integer.parseInt(cat.substring(2));
            }
        }
        return new Part(x, m, a, s);
    }

    private static PartProcessor prepareFlows(String s) {
        Map<String, Function<Part, String>> map = Arrays.stream(s.split("\r\n"))
                .map(Day19::flowFromLine)
                .collect(Collectors.toMap(Workflow::name, Workflow::condition));
        return new PartProcessor(map);
    }

    private static Workflow flowFromLine(String line) {
        String[] spline = line.split("\\{");
        String name = spline[0];
        String[] conditions = spline[1].substring(0, spline[1].length() - 1).split(",");

        Function<Part, String> condition = null;
        for (int i = conditions.length - 1; i >= 0; i--) {
            String cLine = conditions[i];
            if (cLine.contains(">")) {
                String end = cLine.split(":")[1];
                cLine = cLine.split(":")[0];
                Function<Part, String> conditionSoFar = condition;
                String[] scline = cLine.split(">");
                condition = switch (scline[0]) {
                    case "x" ->
                            (Part part) -> part.x() > Integer.parseInt(scline[1]) ? end : conditionSoFar.apply(part);
                    case "m" ->
                            (Part part) -> part.m() > Integer.parseInt(scline[1]) ? end : conditionSoFar.apply(part);
                    case "a" ->
                            (Part part) -> part.a() > Integer.parseInt(scline[1]) ? end : conditionSoFar.apply(part);
                    case "s" ->
                            (Part part) -> part.s() > Integer.parseInt(scline[1]) ? end : conditionSoFar.apply(part);
                    default -> throw new IllegalStateException("Unexpected value: " + scline[0]);
                };
            } else if (cLine.contains("<")) {
                String end = cLine.split(":")[1];
                cLine = cLine.split(":")[0];
                Function<Part, String> conditionSoFar = condition;
                String[] scline = cLine.split("<");
                condition = switch (scline[0]) {
                    case "x" ->
                            (Part part) -> part.x() < Integer.parseInt(scline[1]) ? end : conditionSoFar.apply(part);
                    case "m" ->
                            (Part part) -> part.m() < Integer.parseInt(scline[1]) ? end : conditionSoFar.apply(part);
                    case "a" ->
                            (Part part) -> part.a() < Integer.parseInt(scline[1]) ? end : conditionSoFar.apply(part);
                    case "s" ->
                            (Part part) -> part.s() < Integer.parseInt(scline[1]) ? end : conditionSoFar.apply(part);
                    default -> throw new IllegalStateException("Unexpected value: " + scline[0]);
                };
            } else {
                String finalCLine = cLine;
                condition = (Part p) -> finalCLine;
            }
        }

        return new Workflow(name, condition);
    }
}
