package org.example;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day19 {

    record Part(int x, int m, int a, int s) {
        Part partX(int x) {
            return new Part(x, this.m(), this.a(), this.s());
        }

        Part partM(int m) {
            return new Part(this.x(), m, this.a(), this.s());
        }

        Part partA(int a) {
            return new Part(this.x(), this.m(), a, this.s());
        }

        Part partS(int s) {
            return new Part(this.x(), this.m(), this.a(), s);
        }
    }

    record PartRange(Part minVals, Part maxVals) {
        public long combinations() {
            long xCombo = maxVals().x() - minVals().x() + 1;
            long mCombo = maxVals().m() - minVals().m() + 1;
            long aCombo = maxVals().a() - minVals().a() + 1;
            long sCombo = maxVals().s() - minVals().s() + 1;
            if(xCombo < 0 || mCombo < 0 || aCombo < 0 || sCombo < 0) {
                return 0L;
            } else {
                return xCombo * mCombo * aCombo * sCombo;
            }
        }
    }

    record Condition(Function<Part, Integer> valueAccessor, char cat, boolean greater, Integer condValue,
                     String redir) {

        Condition(String output) {
            this(part -> 0, ' ', false, 1, output);
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

    static class WorkFlowTrie {
        String name;
        Map<PartRange, WorkFlowTrie> conditionMap = new HashMap<>();

        void addWorkflow(Workflow wf, Map<String, Workflow> flowMap, PartRange pr) {
            this.name = wf.name();
            if (wf.name().equals("A") || wf.name().equals("R")) {
                return;
            }
            for (Condition cond : wf.conditions()) {
                if (cond.cat() == ' ') {
                    WorkFlowTrie wft = new WorkFlowTrie();
                    wft.addWorkflow(flowMap.get(cond.redir()), flowMap, pr);
                    conditionMap.put(pr, wft);
                    break;
                }
                if (cond.greater()) {
                    Part minPart = getMinPart(cond, pr, 1);
                    WorkFlowTrie wft = new WorkFlowTrie();
                    PartRange newPr = new PartRange(minPart, pr.maxVals());
                    wft.addWorkflow(flowMap.get(cond.redir()), flowMap, newPr);
                    conditionMap.put(newPr, wft);
                    pr = new PartRange(pr.minVals(), getMaxPart(cond, pr, 0));
                } else {
                    Part maxPart = getMaxPart(cond, pr, 1);
                    WorkFlowTrie wft = new WorkFlowTrie();
                    PartRange newPr = new PartRange(pr.minVals(), maxPart);
                    wft.addWorkflow(flowMap.get(cond.redir()), flowMap, newPr);
                    conditionMap.put( newPr, wft);
                    pr = new PartRange(getMinPart(cond, pr, 0), pr.maxVals());
                }
            }
        }

        private Part getMaxPart(Condition cond, PartRange pr, int offset) {
            Part minPart = pr.maxVals();
            minPart = switch (cond.cat()) {
                case 'x' -> minPart.partX(Math.min(minPart.x(), cond.condValue - offset));
                case 'm' -> minPart.partM(Math.min(minPart.m(), cond.condValue - offset));
                case 'a' -> minPart.partA(Math.min(minPart.a(), cond.condValue - offset));
                case 's' -> minPart.partS(Math.min(minPart.s(), cond.condValue - offset));
                case ' ' -> minPart;
                default -> throw new IllegalArgumentException("wrong condition " + cond.cat());
            };
            return minPart;
        }

        private static Part getMinPart(Condition cond, PartRange pr, int offset) {
            Part minPart = pr.minVals();
            minPart = switch (cond.cat()) {
                case 'x' -> minPart.partX(Math.max(minPart.x(), cond.condValue + offset));
                case 'm' -> minPart.partM(Math.max(minPart.m(), cond.condValue + offset));
                case 'a' -> minPart.partA(Math.max(minPart.a(), cond.condValue + offset));
                case 's' -> minPart.partS(Math.max(minPart.s(), cond.condValue + offset));
                case ' ' -> minPart;
                default -> throw new IllegalArgumentException("wrong condition " + cond.cat());
            };
            return minPart;
        }

        public long countAs() {
            if (name.equals("R")) {
                return 0L;
            }
            return conditionMap.entrySet().stream()
                    .mapToLong(entry -> entry.getValue().name.equals("A") ? entry.getKey().combinations() : entry.getValue().countAs())
                    .sum();
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
                    ' ',
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

        public long buildAndCountTree() {
            PartRange pr = new PartRange(
                    new Part(1, 1, 1, 1),
                    new Part(4000, 4000, 4000, 4000)
            );
            WorkFlowTrie wft = new WorkFlowTrie();
            wft.addWorkflow(flowMap.get("in"), flowMap, pr);
            return wft.countAs();
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
        return flowBoi.buildAndCountTree();
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
                condList.add(new Condition(part -> 0, ' ', false, 1, cLine));
                continue;
            }
            String redir = cLine.split(":")[1];
            cLine = cLine.split(":")[0];
            String[] scLine = cLine.split(greater ? ">" : "<");
            int condValue = Integer.parseInt(scLine[1]);
            Function<Part, Integer> valueAccessor = switch (scLine[0]) {
                case "x" -> Part::x;
                case "m" -> Part::m;
                case "a" -> Part::a;
                case "s" -> Part::s;
                default -> throw new IllegalStateException("Unexpected value: " + scLine[0]);
            };
            condList.add(new Condition(valueAccessor, scLine[0].charAt(0), greater, condValue, redir));
        }
        return new Workflow(name, condList);
    }
}
