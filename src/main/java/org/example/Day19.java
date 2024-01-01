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

    record Condition(Function<Part, Integer> valueAccessor, boolean greater, Integer condValue, String redir) {

        Condition(String output) {
            this(part -> 0, false, 1, output);
        }

        boolean passes(Part part) {
            Integer partVal = valueAccessor().apply(part);
            return (greater() && partVal > condValue()) || (!greater() && partVal < condValue());
        }
    }

    record Workflow(String name, List<Condition> conditions) {
        String process(Part part) {
            for (Condition cond : conditions()) {
                if (cond.passes(part)) {
                    return cond.redir();
                }
            }
            return "R";
        }
    }


    static class PartProcessor {
        Map<String, Workflow> flowMap;
        List<Part> accepted = new ArrayList<>();

        PartProcessor(Map<String, Workflow> flowMap) {
            this.flowMap = flowMap;
            flowMap.put("R", new Workflow("R", List.of(new Condition(null))));
            flowMap.put("A", new Workflow("A", List.of(new Condition((Part part) -> {
                accepted.add(part);
                return 0;
            },
                    false,
                    1,
                    null))));
        }

        long processParts(Stream<Part> parts) {
            parts.forEach(part -> {
                String result = "in";
                do {
                    result = flowMap.get(result).process(part);
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

    public static long aoc19a(String input) {
        String[] splinput = input.split("\r\n\r\n");
        PartProcessor flowBoi = prepareFlows(splinput[0]);
        return 0L;
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
        Map<String, Workflow> map = Arrays.stream(s.split("\r\n"))
                .map(Day19::flowFromLine)
                .collect(Collectors.toMap(Workflow::name, wf -> wf));
        return new PartProcessor(map);
    }

    private static Workflow flowFromLine(String line) {
        String[] spline = line.split("\\{");
        String name = spline[0];
        String[] conditions = spline[1].substring(0, spline[1].length() - 1).split(",");

        List<Condition> condList = new ArrayList<>();
        for (String condition : conditions) {
            String cLine = condition;
            boolean greater = cLine.contains(">");
            if (!greater && !cLine.contains("<")) {
                condList.add(new Condition(part -> 0, false, 1, cLine));
                continue;
            }
            String redir = cLine.split(":")[1];
            cLine = cLine.split(":")[0];
            String[] scLine = cLine.split(greater ? ">" : "<");
            int condValue = Integer.parseInt(scLine[1]);
            Function<Part, Integer> valueAccessor = switch (scLine[0].trim()) {
                case "x" -> Part::x;
                case "m" -> Part::m;
                case "a" -> Part::a;
                case "s" -> Part::s;
                default -> throw new IllegalStateException("Unexpected value: " + scLine[0]);
            };
            condList.add(new Condition(valueAccessor, greater, condValue, redir));
        }
        return new Workflow(name, condList);
    }
}
