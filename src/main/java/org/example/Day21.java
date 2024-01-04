package org.example;

import org.example.Utils.Direction;
import org.example.Utils.Point;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.Utils.inTab;
import static org.example.Utils.streamTo2DCharArray;

public class Day21 {
    record Tile(Point startPoint, long fillSteps, Map<Point, Long> endPointsDist) {
        public Tile fromStart(Point start) {
            return new Tile(start, fillSteps(), endPointsDist());
        }
    }

    public static long aoc21(Stream<String> input) {
        final int maxSteps = 64;
        char[][] tab = streamTo2DCharArray(input);
        Point start = findStart(tab);
        return bfWalking(start, tab, maxSteps, false);
    }

    public static long aoc21a(Stream<String> input) {
        final int maxSteps = 100;
        char[][] tab = streamTo2DCharArray(input);
        Point start = findStart(tab);
        return bfWalking(start, tab, maxSteps, true);
    }

    private static long dist(Point start, Point end, char[][] tab) {
        record CountedPoint(Point point, long count) {
        }
        Queue<CountedPoint> starts = new ArrayDeque<>();
        Set<Point> visited = new HashSet<>();
        visited.add(start);
        starts.add(new CountedPoint(start, 0L));
        CountedPoint p = starts.poll();
        while (!starts.isEmpty() && p!= null && !p.point().equals(end)) {
            for (Direction dir : Direction.getAll()) {
                Point nPoint = p.point().plus(dir);
                if (visited.contains(nPoint)) {
                    continue;
                }
                if (!inTab(nPoint, tab)) {
                    continue;
                }
                if (pointInTab(tab, nPoint) == '#') {
                    continue;
                }
                starts.add(new CountedPoint(nPoint, p.count() + 1));
                visited.add(nPoint);
            }
            p = starts.poll();
        }
        return p != null ? p.count() : 0L;
    }

    private static long fillTime(Point start, char[][] tab) {
        return dist(start, null, tab);
    }

    private static Map<String, Tile> baseTiles(char[][] tab) {
        Map<String, Point> basePoints = new HashMap<>();
        basePoints.put("tl", new Point(0, 0));
        basePoints.put("t", new Point(0, tab[0].length / 2));
        basePoints.put("tr", new Point(0, tab[0].length - 1));
        basePoints.put("l", new Point(tab.length / 2, 0));
        basePoints.put("r", new Point(tab.length / 2, tab[0].length - 1));
        basePoints.put("bl", new Point(tab.length - 1, 0));
        basePoints.put("br", new Point(tab.length - 1, tab[0].length - 1));
        Map<String, List<String>> neighbors = new HashMap<>();
        neighbors.put("tl", List.of("tr", "bl"));
        neighbors.put("bl", List.of("br", "tl"));
        neighbors.put("tr", List.of("tl", "br"));
        neighbors.put("br", List.of("tr", "bl"));
        neighbors.put("t", List.of("tr", "tl", "b"));
        neighbors.put("b", List.of("br", "bl", "t"));
        neighbors.put("l", List.of("bl", "tl", "r"));
        neighbors.put("r", List.of("br", "tr", "l"));
        Map<String, List<Point>> neighborPoints = neighbors.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream().map(basePoints::get).toList()));
        return neighborPoints.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> toTile(entry, basePoints, tab)));
    }

    private static Tile toTile(Map.Entry<String, List<Point>> entry, Map<String, Point> basePoints, char[][] tab) {
        Point start = basePoints.get(entry.getKey());
        return new Tile(start, fillTime(start, tab), distToPoints(start, entry.getValue(), tab));
    }

    private static Map<Point, Long> distToPoints(Point start, List<Point> ends, char[][] tab) {
        return ends.stream().collect(Collectors.toMap(end -> end, end -> dist(start, end, tab)));
    }

    Tile fromLeft(Point start, Map<String, Tile> baseTiles) {
        return baseTiles.get("l").fromStart(start);
    }

    private static long bfWalking(Point start, char[][] tab, int maxSteps, boolean repeating) {
        long out = 0;
        if (maxSteps % 2 == 0) {
            out++;
        }
        Queue<Point> starts = new ArrayDeque<>();
        Set<Point> visited = new HashSet<>();
        visited.add(start);
        starts.add(start);
        for (int i = 0; i < maxSteps; i++) {
            List<Point> newStarts = new ArrayList<>();
            while (!starts.isEmpty()) {
                start = starts.poll();
                for (Direction dir : Direction.getAll()) {
                    Point nPoint = start.plus(dir);
                    if (visited.contains(nPoint)) {
                        continue;
                    }
                    if (!repeating && !inTab(nPoint, tab)) {
                        continue;
                    }
                    if (pointInTab(tab, nPoint) == '#') {
                        continue;
                    }
                    newStarts.add(nPoint);
                    visited.add(nPoint);
                    if (i % 2 != maxSteps % 2) {
                        out++;
                    }
                }
            }
            starts.addAll(newStarts);
        }
        return out;
    }

    private static char pointInTab(char[][] tab, Point point) {
        int a = toTabSizeA(point.a(), tab);
        int b = toTabSizeB(point.b(), tab);
        return tab[a][b];
    }

    private static int toTabSizeA(int a, char[][] tab) {
        while (a < 0) {
            a += tab.length;
        }
        while (a >= tab.length) {
            a %= tab.length;
        }
        return a;
    }

    private static int toTabSizeB(int b, char[][] tab) {
        while (b < 0) {
            b += tab[0].length;
        }
        while (b >= tab[0].length) {
            b %= tab[0].length;
        }
        return b;
    }

    private static Point findStart(char[][] tab) {
        for (int i = 0; i < tab.length; i++) {
            for (int j = 0; j < tab[0].length; j++) {
                if (tab[i][j] == 'S') {
                    return new Point(i, j);
                }
            }
        }
        return new Point(-1, -1);
    }
}