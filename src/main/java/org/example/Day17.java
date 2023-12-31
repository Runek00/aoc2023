package org.example;

import org.example.Utils.Direction;
import org.example.Utils.Point;

import java.util.*;
import java.util.stream.Stream;

import static org.example.Utils.inTab;
import static org.example.Utils.streamTo2DCharArray;

public class Day17 {

    record DirPoint(Point p, Direction dir) {
    }

    record CountedDirPoint(Point p, Direction dir, short count) {
        public CountedDirPoint(Point p, Direction dir, int count) {
            this(p, dir, (short) count);
        }

        public DirPoint dp() {
            return new DirPoint(p, dir);
        }
    }

    public static long aoc17(Stream<String> input) {
        char[][] tab = streamTo2DCharArray(input);
        return pathSearch(new Point(0, 0), new Point(tab.length - 1, tab[0].length - 1), tab, 0, 3);
    }

    public static long aoc17a(Stream<String> input) {
        char[][] tab = streamTo2DCharArray(input);
        return pathSearch(new Point(0, 0), new Point(tab.length - 1, tab[0].length - 1), tab, 4, 8);
    }

    private static long pathSearch(Point start, Point goal, char[][] tab, int minSteps, int maxSteps) {

        Map<DirPoint, Map<Short, Long>> shortestPathMap = new HashMap<>();
        for (Direction dir : Direction.getAll()) {
            HashMap<Short, Long> zeroMap = new HashMap<>();
            zeroMap.put((short) 1, 0L);
            shortestPathMap.put(new DirPoint(start, dir), zeroMap);
        }

        Queue<CountedDirPoint> q = new ArrayDeque<>();
        for (Direction dir : Direction.getAll()) {
            CountedDirPoint cdp = new CountedDirPoint(start, dir, 1);
            q.add(cdp);
        }

        while (!q.isEmpty()) {
            CountedDirPoint step = q.poll();
            Point neighbor = step.p().plus(step.dir());
            if (!inTab(neighbor, tab)) {
                continue;
            }
            if (step.count() > maxSteps) {
                continue;
            }
            for (Direction nextDir : Direction.getAll()) {
                if (nextDir == step.dir().opposite()) {
                    continue;
                }
                DirPoint dirPoint = new DirPoint(neighbor, nextDir);
                Map<Short, Long> dirMap = shortestPathMap.getOrDefault(dirPoint, new HashMap<>());
                if (nextDir == step.dir()) {
                    if (step.count() == maxSteps) {
                        continue;
                    }
                    long newPath = shortestPathMap.getOrDefault(step.dp(), new HashMap<>()).getOrDefault(step.count(), (long) Integer.MAX_VALUE) + tab[neighbor.a()][neighbor.b()] - 48;
                    Long oldPath = dirMap.getOrDefault(step.count(), (long) Integer.MAX_VALUE);
                    if (oldPath > newPath) {
                        short cc = (short) (step.count() + 1);
                        while (cc <= maxSteps) {
                            if (dirMap.getOrDefault(cc, (long) Integer.MAX_VALUE) > newPath) {
                                dirMap.put(cc, newPath);
                            }
                            cc++;
                        }
                        shortestPathMap.put(dirPoint, dirMap);
                        q.add(new CountedDirPoint(neighbor, nextDir, step.count() + 1));
                    }
                } else {
                    if (step.count() < minSteps) {
                        continue;
                    }
                    long newPath = shortestPathMap.getOrDefault(step.dp(), new HashMap<>()).getOrDefault(step.count(), (long) Integer.MAX_VALUE) + tab[neighbor.a()][neighbor.b()] - 48;
                    Long oldPath = dirMap.getOrDefault((short) (1), (long) Integer.MAX_VALUE);
                    if (oldPath > newPath) {
                        short cc = (short) 1;
                        while (cc <= maxSteps) {
                            if (dirMap.getOrDefault(cc, (long) Integer.MAX_VALUE) > newPath) {
                                dirMap.put(cc, newPath);
                            }
                            cc++;
                        }
                        shortestPathMap.put(dirPoint, dirMap);
                        q.add(new CountedDirPoint(neighbor, nextDir, 1));
                    }
                }
            }
        }

        return Arrays.stream(Direction.getAll())
                .map(dir -> new DirPoint(goal, dir))
                .flatMap(dp -> shortestPathMap.getOrDefault(dp, new HashMap<>()).values().stream())
                .mapToLong(l -> l)
                .min()
                .orElse(0L);
    }
}
