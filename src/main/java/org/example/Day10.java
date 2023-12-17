package org.example;

import org.example.Utils.Direction;
import org.example.Utils.Point;
import org.example.Utils.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.example.Utils.Direction.*;
import static org.example.Utils.streamTo2DCharArray;

public class Day10 {

    public static long aoc10(Stream<String> input) {
        char[][] tab = streamTo2DCharArray(input);
        Point start = findStart(tab);
        Step[] steps = findFirstSteps(start, tab);
        if (steps.length != 2) {
            System.out.println("something went wrong");
            return 0L;
        }
        long counter = 1L;
        while (steps[0] != null && steps[1] != null && !steps[0].p().equals(steps[1].p())) {
            steps[0] = nextStep(steps[0], tab);
            steps[1] = nextStep(steps[1], tab);
            counter++;
        }
        return counter;
    }

    public static long aoc10a(Stream<String> input) {
        char[][] tab = streamTo2DCharArray(input);
        char[][] tab2 = new char[tab.length][tab[0].length];
        for (char[] cTab : tab2) {
            Arrays.fill(cTab, 'O');
        }
        Point start = findStart(tab);
        tab2[start.a()][start.b()] = 'S';
        Step[] steps = findFirstSteps(start, tab);
        if (steps.length != 2) {
            System.out.println("something went wrong");
            return 0L;
        }
        while (steps[0] != null && steps[1] != null && !steps[0].p().equals(steps[1].p())) {
            Step[] newSteps = new Step[2];
            newSteps[0] = nextStep(steps[0], tab);
            newSteps[1] = nextStep(steps[1], tab);
            tab2[steps[0].a()][steps[0].b()] = 'S';
            tab2[steps[1].a()][steps[1].b()] = 'S';
            steps = newSteps;
        }
        assert steps[0] != null;
        tab2[steps[0].a()][steps[0].b()] = 'S';
        for (int i = 0; i < tab.length; i++) {
            boolean inside = false;
            for (int j = 0; j < tab[0].length; j++) {
                if (tab2[i][j] == 'S') {
                    boolean matched = true;
                    Direction from = null;
                    while (matched) {
                        Step[] neighbors = tab[i][j] == 'S' ? findFirstSteps(new Point(i, j), tab) : findNeighborSteps(new Point(i, j), tab);
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
                        tab2[i][j] = 'I';
                    }
                }
            }
        }
        return Arrays.stream(tab2)
                .mapToLong(chars -> IntStream.range(0, tab2[0].length)
                        .filter(j -> chars[j] == 'I')
                        .count())
                .sum();
    }

    private static Step[] findNeighborSteps(Point point, char[][] tab) {
        Direction[] dirs = new Direction[]{N, S, E, W};
        return Arrays.stream(dirs)
                .map(dir -> new Step(point, dir))
                .map(step -> nextStep(step, tab))
                .filter(Objects::nonNull)
                .toArray(Step[]::new);
    }

    private static Step nextStep(Step step, char[][] tab) {
        Direction dir = direction(step.from(), tab[step.a()][step.b()]);
        return switch (dir) {
            case N -> new Step(step.p().minus(S), S);
            case S -> new Step(step.p().minus(N), N);
            case E -> new Step(step.p().minus(W), W);
            case W -> new Step(step.p().minus(E), E);
        };
    }

    private static Step[] findFirstSteps(Point start, char[][] tab) {
        ArrayList<Step> output = new ArrayList<>(2);
        if (start.a() > 0) {
            if (direction(S, tab[start.a() - 1][start.b()]) != null) {
                output.add(new Step(start.minus(S), S));
            }
        }
        if (start.a() < tab.length - 1) {
            if (direction(N, tab[start.a() + 1][start.b()]) != null) {
                output.add(new Step(start.minus(N), N));
            }
        }
        if (start.b() > 0) {
            if (direction(E, tab[start.a()][start.b() - 1]) != null) {
                output.add(new Step(start.minus(E), E));
            }
        }
        if (start.b() < tab[0].length - 1) {
            if (direction(W, tab[start.a()][start.b() + 1]) != null) {
                output.add(new Step(start.minus(W), W));
            }
        }
        return output.toArray(Step[]::new);
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

    private static Direction direction(Direction from, char pipe) {
        return switch (pipe) {
            case '|' -> switch (from) {
                case N -> S;
                case S -> N;
                default -> null;
            };
            case '-' -> switch (from) {
                case E -> W;
                case W -> E;
                default -> null;
            };
            case 'L' -> switch (from) {
                case N -> E;
                case E -> N;
                default -> null;
            };
            case 'J' -> switch (from) {
                case N -> W;
                case W -> N;
                default -> null;
            };
            case '7' -> switch (from) {
                case S -> W;
                case W -> S;
                default -> null;
            };
            case 'F' -> switch (from) {
                case S -> E;
                case E -> S;
                default -> null;
            };
            default -> null;
        };
    }
}
