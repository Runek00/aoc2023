package org.example;

import org.example.Utils.Direction;
import org.example.Utils.Point;
import org.example.Utils.Step;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.example.Utils.Direction.*;
import static org.example.Utils.inTab;

public class Day18 {

    record Dig(Direction dir, int count) {
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
        int cnt = drawInTab(tab, inputList, start, Day18::toDig);
//        for(int i = 0; i< tab.length; i++){
//            System.out.println(Arrays.toString(tab[i]));
//        }
        cnt += getCnt(tab);
        return cnt;
    }

    public static long aoc18a(Stream<String> input) {
        List<String> inputList = input.toList();
        TreeMap<Integer, List<List<Integer>>> verticals = new TreeMap<>();
        TreeMap<Integer, List<List<Integer>>> horizontals = new TreeMap<>();
        Point start = new Point(0, 0);
        for (String line : inputList) {
            Dig dig = toColorDig(line);
            switch (dig.dir()) {
                case N -> {
                    int newA = start.a() - dig.count();
                    List<List<Integer>> vList = verticals.getOrDefault(start.b(), new ArrayList<>());
                    vList.add(List.of(newA, start.a()));
                    verticals.put(start.b(), vList);
                    start = new Point(newA, start.b());
                }
                case S -> {
                    int newA = start.a() + dig.count();
                    List<List<Integer>> vList = verticals.getOrDefault(start.b(), new ArrayList<>());
                    vList.add(List.of(start.a(), newA));
                    verticals.put(start.b(), vList);
                    start = new Point(newA, start.b());
                }
                case E -> {
                    int newB = start.b() + dig.count();
                    List<List<Integer>> hList = horizontals.getOrDefault(start.a(), new ArrayList<>());
                    hList.add(List.of(start.b(), newB));
                    horizontals.put(start.a(), hList);
                    start = new Point(start.a(), newB);
                }
                case W -> {
                    int newB = start.b() - dig.count();
                    List<List<Integer>> hList = horizontals.getOrDefault(start.a(), new ArrayList<>());
                    hList.add(List.of(newB, start.b()));
                    horizontals.put(start.a(), hList);
                    start = new Point(start.a(), newB);
                }
                default -> throw new IllegalStateException("Unexpected value: " + dig.dir());
            }
        }
        return countFields(horizontals);
    }

    private static long countFields(TreeMap<Integer, List<List<Integer>>> horizontals) {
        long sum = 0;
        int top = horizontals.firstKey();
        List<List<Integer>> horLines = horizontals.get(top);
        sum += horLines.stream().mapToLong(l -> l.getLast() - l.getFirst() + 1).sum();
        top++;
        while (horizontals.higherKey(top) != null) {
            int next = horizontals.higherKey(top);
            long lineSum = horLines.stream().mapToLong(l -> l.getLast() - l.getFirst() + 1).sum();
            sum += lineSum * ((long) (next - top));
            long fullSum = getFullSum(horLines, horizontals.get(next));
            horLines.addAll(horizontals.get(next));
            horLines = normalizeRanges(horLines);
            sum += Math.max(lineSum, fullSum);
            top = next + 1;
        }
        return sum;
    }

    private static long getFullSum(List<List<Integer>> currentLines, List<List<Integer>> nextLines) {
        TreeMap<Integer, List<Integer>> byStarts = new TreeMap<>();
        currentLines.addAll(nextLines);
        for (List<Integer> integers : currentLines) {
            List<Integer> l = byStarts.getOrDefault(integers.getFirst(), List.of(integers.getFirst(), Integer.MIN_VALUE));
            if (l.getLast() < integers.getLast()) {
                byStarts.put(integers.getFirst(), integers);
            }
        }
        List<List<Integer>> output = new ArrayList<>();
        List<Integer> firstRange = byStarts.pollFirstEntry().getValue();
        Integer start = firstRange.getFirst();
        Integer end = firstRange.getLast();
        while (!byStarts.isEmpty()) {
            List<Integer> newRange = byStarts.pollFirstEntry().getValue();
            if (newRange.getFirst() <= end) {
                end = Math.max(end, newRange.getLast());
            } else {
                output.add(List.of(start, end));
                start = newRange.getFirst();
                end = newRange.getLast();
            }
        }
        output.add(List.of(start, end));
        return output.stream().mapToLong(l -> l.getLast() - l.getFirst() + 1).sum();
    }

    private static List<List<Integer>> normalizeRanges(List<List<Integer>> ranges) {
        TreeMap<Integer, List<List<Integer>>> byStarts = new TreeMap<>();
        for (List<Integer> integers : ranges) {
            List<List<Integer>> l = byStarts.getOrDefault(integers.getFirst(), new ArrayList<>());
            l.add(integers);
            byStarts.put(integers.getFirst(), l);
        }
        List<List<Integer>> output = new ArrayList<>();
        List<Integer> firstRange = onlyOne(byStarts.pollFirstEntry().getValue());
        Integer start = firstRange.getFirst();
        Integer end = firstRange.getLast();
        while (!byStarts.isEmpty()) {
            if (start == null || end == null) {
                List<Integer> range = onlyOne(byStarts.pollFirstEntry().getValue());
                start = range.getFirst();
                end = range.getLast();
                continue;
            }
            List<Integer> newRange = onlyOne(byStarts.pollFirstEntry().getValue());
            if (newRange.getFirst() < end) {
                output.add(List.of(start, newRange.getFirst()));
                if (newRange.getLast() < end) {
                    start = newRange.getLast();
                } else {
                    start = null;
                    end = null;
                }
            } else if (Objects.equals(newRange.getFirst(), end)) {
                end = newRange.getLast();
            } else {
                output.add(List.of(start, end));
                start = newRange.getFirst();
                end = newRange.getLast();
            }
        }
        if (start != null && end != null) {
            output.add(List.of(start, end));
        }
        return output;
    }

    private static List<Integer> onlyOne(List<List<Integer>> value) {
        if (value.size() == 1) {
            return value.getFirst();
        }
        Integer v1 = value.getFirst().getLast();
        Integer v2 = value.getLast().getLast();
        if (v1 < v2) {
            return List.of(v1, v2);
        } else {
            return List.of(v2, v1);
        }
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

    private static int drawInTab(char[][] tab, List<String> inputList, Point start, Function<String, Dig> toDig) {
        tab[start.a()][start.b()] = '#';
        int cnt = 0;
        for (String line : inputList) {
            Dig dig = toDig.apply(line);
            for (int i = 0; i < dig.count(); i++) {
                cnt++;
                start = start.plus(dig.dir());
                tab[start.a()][start.b()] = '#';
            }
        }
        return cnt;
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
