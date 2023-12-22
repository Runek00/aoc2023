package org.example;

import org.example.Utils.Direction;
import org.example.Utils.Point;

import java.util.*;
import java.util.stream.Stream;

import static org.example.Utils.inTab;
import static org.example.Utils.streamTo2DCharArray;

public class Day21 {
    public static long aoc21(Stream<String> input) {
        final int maxSteps = 64;
        return bfWalking(input, maxSteps, false);
    }

    public static long aoc21a(Stream<String> input) {
        final int maxSteps = 50;
        return bfWalking(input, maxSteps, true);
    }

    private static long bfWalking(Stream<String> input, int maxSteps, boolean repeating) {
        long out = 0;
        char[][] tab = streamTo2DCharArray(input);
        Point start = findStart(tab);
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
                    if (tab[toTabSizeA(nPoint.a(), tab)][toTabSizeB(nPoint.b(), tab)] != '.') {
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
//        for(int i = 0; i < tab2.length; i++) {
//            System.out.println(Arrays.toString(tab2[i]));
//        }
        return out;
    }

    private static int toTabSizeA(int a, char[][] tab) {
        while (a < 0) {
            a += tab.length;
        }
        while (a >= tab.length) {
            a -= tab.length;
        }
        return a;
    }

    private static int toTabSizeB(int b, char[][] tab) {
        while (b < 0) {
            b += tab[0].length;
        }
        while (b >= tab[0].length) {
            b -= tab[0].length;
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