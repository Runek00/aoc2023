package org.example;

import org.example.Utils.Direction;
import org.example.Utils.Point;
import org.example.Utils.Step;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.Utils.Direction.*;
import static org.example.Utils.inTab;

public class Day18 {

    record Dig(Direction dir, int count) {
    }

    record Line(int start, int end) {
        boolean lConnected(Line other) {
            return start() == other.end();
        }

        boolean rConnected(Line other) {
            return end() == other.start();
        }

        boolean hasInside(Line other) {
            return start() < other.start() && end() > other.end();
        }

        boolean fullyClosedBy(Line other) {
            return this.equals(other);
        }

        boolean lClosedBy(Line other) {
            return start() == other.start();
        }

        boolean rClosedBy(Line other) {
            return end() == other.end();
        }
    }

    public static int aoc18(Stream<String> input) {
        List<String> inputList = input.toList();
        Map<Direction, Integer> tabSize = getSizeMap(inputList, Day18::toDig);
        int h = tabSize.getOrDefault(S, 0) - tabSize.getOrDefault(N, 0) + 1;
        int w = tabSize.getOrDefault(E, 0) - tabSize.getOrDefault(W, 0) + 1;
        char[][] tab = new char[h][w];
        for (char[] cTab : tab) {
            Arrays.fill(cTab, '.');
        }
        Point start = new Point(-tabSize.getOrDefault(N, 0), -tabSize.getOrDefault(W, 0));
        drawInTab(tab, inputList, start, Day18::toDig);
        return getCnt(tab);
    }

    public static long aoc18a(Stream<String> input) {
        List<String> inputList = input.toList();
        TreeMap<Integer, List<Line>> verticals = new TreeMap<>();
        TreeMap<Integer, List<Line>> horizontals = new TreeMap<>();
        Point start = new Point(0, 0);
        for (String line : inputList) {
            Dig dig = toColorDig(line);
            switch (dig.dir()) {
                case N -> {
                    int newA = start.a() - dig.count();
                    List<Line> vList = verticals.getOrDefault(start.b(), new ArrayList<>());
                    vList.add(new Line(newA, start.a()));
                    verticals.put(start.b(), vList);
                    start = new Point(newA, start.b());
                }
                case S -> {
                    int newA = start.a() + dig.count();
                    List<Line> vList = verticals.getOrDefault(start.b(), new ArrayList<>());
                    vList.add(new Line(start.a(), newA));
                    verticals.put(start.b(), vList);
                    start = new Point(newA, start.b());
                }
                case E -> {
                    int newB = start.b() + dig.count();
                    List<Line> hList = horizontals.getOrDefault(start.a(), new ArrayList<>());
                    hList.add(new Line(start.b(), newB));
                    horizontals.put(start.a(), hList);
                    start = new Point(start.a(), newB);
                }
                case W -> {
                    int newB = start.b() - dig.count();
                    List<Line> hList = horizontals.getOrDefault(start.a(), new ArrayList<>());
                    hList.add(new Line(newB, start.b()));
                    horizontals.put(start.a(), hList);
                    start = new Point(start.a(), newB);
                }
                default -> throw new IllegalStateException("Unexpected value: " + dig.dir());
            }
        }
        return countFields(horizontals);
    }

    private static long countFields(TreeMap<Integer, List<Line>> horizontals) {
        long sum = 0;
        int top = horizontals.firstKey();
        List<Line> horLines = horizontals.get(top);
        sum += horLines.stream().mapToLong(l -> l.end() - l.start() + 1).sum();
        top++;
        int next = top;
        while (horizontals.higherKey(top) != null) {
            next = horizontals.higherKey(next);
            long lineSum = horLines.stream().mapToLong(l -> l.end() - l.start() + 1).sum();
            sum += lineSum * ((long) (next - top));
            long fullSum = getFullSum(horLines, horizontals.get(next));
            sum += Math.max(lineSum, fullSum);
            horLines = normalizeRanges(horLines, horizontals.get(next));
            top = next + 1;
        }
        return sum;
    }

    private static long getFullSum(List<Line> currentLines, List<Line> nextLines) {
        TreeMap<Integer, Line> byStarts = getMapByStarts(currentLines, nextLines);
        List<Line> output = new ArrayList<>();
        Line firstRange = byStarts.pollFirstEntry().getValue();
        int start = firstRange.start();
        int end = firstRange.end();
        while (!byStarts.isEmpty()) {
            Line newRange = byStarts.pollFirstEntry().getValue();
            if (newRange.start() <= end) {
                end = Math.max(end, newRange.end());
            } else {
                output.add(new Line(start, end));
                start = newRange.start();
                end = newRange.end();
            }
        }
        output.add(new Line(start, end));
        return output.stream().mapToLong(l -> l.end() - l.start() + 1).sum();
    }

    private static TreeMap<Integer, Line> getMapByStarts(List<Line> currentLines, List<Line> nextLines) {
        TreeMap<Integer, Line> byStarts = new TreeMap<>();
        for (Line line : currentLines) {
            Line l = byStarts.getOrDefault(line.start(), new Line(line.start(), Integer.MIN_VALUE));
            if (l.end() < line.end()) {
                byStarts.put(line.start(), line);
            }
        }
        for (Line line : nextLines) {
            Line l = byStarts.getOrDefault(line.start(), new Line(line.start(), Integer.MIN_VALUE));
            if (l.end() < line.end()) {
                byStarts.put(line.start(), line);
            }
        }
        return byStarts;
    }

    private static List<Line> normalizeRanges(List<Line> oldLines, List<Line> newLines) {
        NavigableMap<Integer, Line> oldByStarts = new TreeMap<>();
        for (Line integers : oldLines) {
            oldByStarts.put(integers.start(), integers);
        }
        NavigableMap<Integer, Line> newByStarts = new TreeMap<>();
        for (Line integers : newLines) {
            newByStarts.put(integers.start(), integers);
        }
        List<Line> output = new ArrayList<>();
        Set<Line> usedNewLines = new HashSet<>();
        OldLoop:
        for (Line oldLine : oldByStarts.values()) {
            for (Line newLine : newByStarts.values()) {
                if (usedNewLines.contains(newLine)) {
                    continue;
                }
                if (oldLine.lConnected(newLine)) {
                    oldLine = new Line(newLine.start(), oldLine.end());
                    usedNewLines.add(newLine);
                } else if (oldLine.rConnected(newLine)) {
                    oldLine = new Line(oldLine.start(), newLine.end());
                    usedNewLines.add(newLine);
                } else if (oldLine.hasInside(newLine)) {
                    output.add(new Line(oldLine.start(), newLine.start()));
                    oldLine = new Line(newLine.end(), oldLine.end);
                    usedNewLines.add(newLine);
                } else if (oldLine.fullyClosedBy(newLine)) {
                    usedNewLines.add(newLine);
                    continue OldLoop;
                } else if (oldLine.lClosedBy(newLine)) {
                    oldLine = new Line(newLine.end(), oldLine.end());
                    usedNewLines.add(newLine);
                } else if (oldLine.rClosedBy(newLine)) {
                    oldLine = new Line(oldLine.start(), newLine.start());
                    usedNewLines.add(newLine);
                }
            }
            output.add(oldLine);
        }
        output.addAll(newByStarts.values().stream().filter(l -> !usedNewLines.contains(l)).collect(Collectors.toSet()));
        return joinRanges(output);
    }

    private static List<Line> joinRanges(List<Line> input) {
        if (input.isEmpty()) {
            return input;
        }
        List<Line> output = new ArrayList<>();
        NavigableMap<Integer, Line> lines = new TreeMap<>();
        for (Line integers : input) {
            lines.put(integers.start(), integers);
        }

        Line currLine = lines.pollFirstEntry().getValue();
        while (!lines.isEmpty()) {
            Line nextLine = lines.pollFirstEntry().getValue();
            if (currLine.rConnected(nextLine)) {
                currLine = new Line(currLine.start(), nextLine.end());
            } else {
                output.add(currLine);
                currLine = nextLine;
            }
        }
        output.add(currLine);
        return output;
    }

    private static int getCnt(char[][] tab) {
        int cnt = 0;
        for (int i = 0; i < tab.length; i++) {
            boolean inside = false;
            for (int j = 0; j < tab[0].length; j++) {
                if (tab[i][j] == '#') {
                    boolean matched = true;
                    Direction from = null;
                    while (matched) {
                        cnt++;
                        Step[] neighbors = findNeighborSteps(new Point(i, j), tab);
                        int finalI = i;
                        int finalJ = j;
                        List<Direction> froms = Arrays.stream(neighbors)
                                .filter(step -> step.a() != finalI)
                                .map(Step::from)
                                .filter(d -> d == N || d == S)
                                .toList();
                        if (froms.size() == 1) {
                            if (from != null) {
                                if (from != froms.getFirst()) {
                                    inside = !inside;
                                }
                                from = null;
                            } else {
                                from = froms.getFirst();
                            }
                        } else if (froms.size() == 2) {
                            inside = !inside;
                        }

                        matched = Arrays.stream(neighbors).map(Step::b).anyMatch(y -> y == finalJ + 1);
                        j++;
                    }
                    j--;
                } else {
                    if (inside) {
                        cnt++;
                    }
                }
            }
        }
        return cnt;
    }

    private static void drawInTab(char[][] tab, List<String> inputList, Point start, Function<String, Dig> toDig) {
        tab[start.a()][start.b()] = '#';
        for (String line : inputList) {
            Dig dig = toDig.apply(line);
            for (int i = 0; i < dig.count(); i++) {
                start = start.plus(dig.dir());
                tab[start.a()][start.b()] = '#';
            }
        }
    }

    private static Map<Direction, Integer> getSizeMap(List<String> inputList, Function<String, Dig> toDig) {
        Map<Direction, Integer> tabSize = new HashMap<>();
        int i = 0;
        int j = 0;
        for (String line : inputList) {
            Dig dig = toDig.apply(line);
            switch (dig.dir()) {
                case N -> {
                    i -= dig.count();
                    if (tabSize.getOrDefault(N, 0) > i) {
                        tabSize.put(N, i);
                    }
                }
                case S -> {
                    i += dig.count();
                    if (tabSize.getOrDefault(S, 0) < i) {
                        tabSize.put(S, i);
                    }
                }
                case E -> {
                    j += dig.count();
                    if (tabSize.getOrDefault(E, 0) < j) {
                        tabSize.put(E, j);
                    }
                }
                case W -> {
                    j -= dig.count();
                    if (tabSize.getOrDefault(W, 0) > j) {
                        tabSize.put(W, j);
                    }
                }
            }
        }
        return tabSize;
    }

    private static Step[] findNeighborSteps(Point point, char[][] tab) {
        return Arrays.stream(getAll())
                .map(dir -> new Step(point.plus(dir), dir))
                .filter(step -> inTab(step, tab))
                .filter(step -> tab[step.a()][step.b()] == '#')
                .toArray(Step[]::new);
    }


    private static Dig toDig(String line) {
        String[] spline = line.split(" ");
        int len = Integer.parseInt(spline[1]);
        return switch (spline[0]) {
            case "R" -> new Dig(E, len);
            case "L" -> new Dig(W, len);
            case "U" -> new Dig(N, len);
            case "D" -> new Dig(S, len);
            default -> throw new IllegalStateException("Unexpected value: " + spline[0]);
        };
    }

    private static Dig toColorDig(String line) {
        String[] spline = line.split(" ");
        int len = Integer.parseInt(spline[2].substring(2, 7), 16);
        return switch (spline[2].substring(7, 8)) {
            case "0" -> new Dig(E, len);
            case "1" -> new Dig(S, len);
            case "2" -> new Dig(W, len);
            case "3" -> new Dig(N, len);
            default -> throw new IllegalStateException("Unexpected value: " + spline[0]);
        };
    }
}
