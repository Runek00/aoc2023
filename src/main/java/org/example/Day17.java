package org.example;

import org.example.Utils.Direction;
import org.example.Utils.Point;

import java.util.*;
import java.util.stream.Stream;

import static org.example.Utils.inTab;
import static org.example.Utils.streamTo2DCharArray;

public class Day17 {

    public static long aoc17(Stream<String> input) {
        char[][] tab = streamTo2DCharArray(input);
        return aStar(new Point(0, 0), new Point(tab.length - 1, tab[0].length - 1), tab);
    }

    private static int aStar(Point start, Point goal, char[][] tab) {

        int infinity = 10000;

        HashMap<Point, Set<Point>> cameFrom = new HashMap<>();
        HashMap<Point, Integer> gScore = new HashMap<>();
        HashMap<Point, Integer> fScore = new HashMap<>();
        PriorityQueue<Point> openSet = new PriorityQueue<>(Comparator.comparing(point -> fScore.getOrDefault(point, infinity)));
        gScore.put(start, 0);
        fScore.put(start, 5*(tab.length + tab[0].length));
        openSet.add(start);

        int minSum = Integer.MAX_VALUE;
        while (!openSet.isEmpty()) {
            Point current = openSet.poll();
            if (current.equals(goal)) {
                minSum = Math.min(minSum, reconstructPath(current, cameFrom, tab));
            }
            for (Direction dir : Direction.getAll()) {
                Point neighbor = current.plus(dir);
//                System.out.println(neighbor);
                if (!inTab(neighbor, tab) || cameFrom.getOrDefault(current, Set.of(new Point(-1, -1))).contains(neighbor)) {
                    continue;
                }
                int tenativeScore = gScore.get(current) + dist(current, neighbor, cameFrom, tab);
                if (tenativeScore < gScore.getOrDefault(neighbor, infinity)) {
                    Set<Point> fromSet = new HashSet<>();
                    fromSet.add(current);
                    cameFrom.put(neighbor, fromSet);
                    gScore.put(neighbor, tenativeScore);
                    fScore.put(neighbor, tenativeScore + 5*((tab.length + tab[0].length) - neighbor.a() - neighbor.b()));
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }else if (tenativeScore < infinity && tenativeScore == gScore.getOrDefault(neighbor, infinity)) {
                    Set<Point> fromSet = cameFrom.getOrDefault(neighbor, new HashSet<>());
                    fromSet.add(current);
                    cameFrom.put(neighbor, fromSet);
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        return minSum;
    }

    private static Integer dist(Point current, Point neighbor, HashMap<Point, Set<Point>> cameFrom, char[][] tab) {
        if (!inTab(neighbor, tab)) {
            return 10000;
        }
        if (current.a() == neighbor.a()) {
            Point prev = cameFrom.getOrDefault(current, new HashSet<>()).stream().filter(point -> point.a() == current.a()).findFirst().orElse(null);
            Point antiprev = cameFrom.getOrDefault(current, new HashSet<>()).stream().filter(point -> point.a() != current.a()).findFirst().orElse(null);
            if (antiprev == null && prev != null) {
                Point prevprev = cameFrom.getOrDefault(prev, new HashSet<>()).stream().filter(point -> point != current && point.a() == current.a()).findFirst().orElse(null);
                Point antiprevprev = cameFrom.getOrDefault(prev, new HashSet<>()).stream().filter(point -> point != current && point.a() != current.a()).findFirst().orElse(null);
                if (antiprevprev == null && prevprev != null) {
                    Point prevprevprev = cameFrom.getOrDefault(prevprev, new HashSet<>()).stream().filter(point -> point != current && point.a() == current.a()).findFirst().orElse(null);
                    Point antiprevprevprev = cameFrom.getOrDefault(prevprev, new HashSet<>()).stream().filter(point -> point != current && point.a() != current.a()).findFirst().orElse(null);
                    if (antiprevprevprev == null && prevprevprev != null) {
                        return 10000;
                    }
                }
            }
        } else if (current.b() == neighbor.b()) {
            Point prev = cameFrom.getOrDefault(current, new HashSet<>()).stream().filter(point -> point.b() == current.b()).findFirst().orElse(null);
            Point antiprev = cameFrom.getOrDefault(current, new HashSet<>()).stream().filter(point -> point.b() != current.b()).findFirst().orElse(null);
            if (antiprev == null && prev != null) {
                Point prevprev = cameFrom.getOrDefault(prev, new HashSet<>()).stream().filter(point -> point != current && point.b() == current.b()).findFirst().orElse(null);
                Point antiprevprev = cameFrom.getOrDefault(prev, new HashSet<>()).stream().filter(point -> point != current && point.b() != current.b()).findFirst().orElse(null);
                if (antiprevprev == null && prevprev != null) {
                    Point prevprevprev = cameFrom.getOrDefault(prevprev, new HashSet<>()).stream().filter(point -> point != current && point.b() == current.b()).findFirst().orElse(null);
                    Point antiprevprevprev = cameFrom.getOrDefault(prevprev, new HashSet<>()).stream().filter(point -> point != current && point.b() != current.b()).findFirst().orElse(null);
                    if (antiprevprevprev == null && prevprevprev != null) {
                        return 10000;
                    }
                }
            }
        } else {
            return 10000;
        }
        return tab[neighbor.a()][neighbor.b()] - 48;
    }

    private static int reconstructPath(Point current, HashMap<Point, Set<Point>> cameFrom, char[][] tab) {
        int sum = 0;
        Set<Point> used = new HashSet<>();
        used.add(current);
        while (cameFrom.containsKey(current)) {
            sum += tab[current.a()][current.b()] - 48;
            current = cameFrom.get(current).stream().filter(point -> !used.contains(point)).findFirst().orElse(null);
            used.add(current);
        }
        return sum;
    }
}
