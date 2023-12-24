package org.example;

import org.example.Utils.Direction;
import org.example.Utils.Point;

import java.util.HashSet;
import java.util.stream.Stream;

import static org.example.Utils.Direction.*;
import static org.example.Utils.inTab;
import static org.example.Utils.streamTo2DCharArray;

public class Day23 {
    public static long aoc23(Stream<String> input) {
        char[][] tab = streamTo2DCharArray(input);
        Point start = findStart(tab);
        return maxBfs(tab, start, new HashSet<>());
    }

    private static Point findStart(char[][] tab) {
        for(int j = 0; j < tab[0].length; j++) {
            if(tab[0][j] == '.') {
                return new Point(0, j);
            }
        }
        return new Point(-1,-1);
    }

    private static long maxBfs(char[][] tab, Point point, HashSet<Point> visited) {
        if(!inTab(point, tab)) {
            return 0L;
        }
        if(visited.contains(point)) {
            return 0L;
        }
        if(tab[point.a()][point.b()] == '#') {
            return 0L;
        }
        if(point.a() == tab.length-1) {
            return 0L;
        }
        visited.add(point);
        long result = Long.MIN_VALUE;
        if(tab[point.a()][point.b()] == '.') {
            for(Direction dir : Direction.getAll()) {
                result = Math.max(result, maxBfs(tab, point.plus(dir), visited));
            }
        } else {
            result = switch (tab[point.a()][point.b()]) {
                case '^' -> maxBfs(tab, point.plus(N), visited);
                case '>' -> maxBfs(tab, point.plus(E), visited);
                case 'v' -> maxBfs(tab, point.plus(S), visited);
                case '<' -> maxBfs(tab, point.plus(W), visited);
                default -> 0;
            };
        }
        visited.remove(point);
        return result+1;
    }
}
