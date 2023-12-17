package org.example;

import org.example.Utils.Point;
import org.example.Utils.Step;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
            tab2[steps[0].p().a()][steps[0].p().b()] = 'S';
            tab2[steps[1].p().a()][steps[1].p().b()] = 'S';
            steps = newSteps;
        }
        assert steps[0] != null;
        tab2[steps[0].p().a()][steps[0].p().b()] = 'S';
        for (int i = 0; i < tab.length; i++) {
            boolean inside = false;
            for (int j = 0; j < tab[0].length; j++) {
                if (tab2[i][j] == 'S') {
                    boolean matched = true;
                    char from = 'X';
                    while (matched) {
                        Step[] neighbors = tab[i][j] == 'S' ? findFirstSteps(new Point(i, j), tab) : findNeighborSteps(new Point(i, j), tab);
                        int finalI = i;
                        int finalJ = j;
                        List<Character> froms = Arrays.stream(neighbors)
                                .filter(step -> step.p().a() != finalI)
                                .map(Step::from)
                                .filter(d -> d == 'N' || d == 'S')
                                .toList();
                        if (froms.size() == 1) {
                            if (from != 'X') {
                                if (from != froms.getFirst()) {
                                    inside = !inside;
                                }
                                from = 'X';
                            } else {
                                from = froms.getFirst();
                            }
                        } else if (froms.size() == 2) {
                            inside = !inside;
                        }

                        matched = Arrays.stream(neighbors).map(step -> step.p().b()).anyMatch(y -> y == finalJ + 1);
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
        Character[] dirs = new Character[]{'N', 'S', 'E', 'W'};
        return Arrays.stream(dirs)
                .map(dir -> new Step(point, dir))
                .map(step -> nextStep(step, tab))
                .filter(Objects::nonNull)
                .toArray(Step[]::new);
    }

    private static Step nextStep(Step step, char[][] tab) {
        char dir = direction(step.from(), tab[step.p().a()][step.p().b()]);
        return switch (dir) {
            case 'N' -> new Step(new Point(step.p().a() - 1, step.p().b()), 'S');
            case 'S' -> new Step(new Point(step.p().a() + 1, step.p().b()), 'N');
            case 'E' -> new Step(new Point(step.p().a(), step.p().b() + 1), 'W');
            case 'W' -> new Step(new Point(step.p().a(), step.p().b() - 1), 'E');
            default -> null;
        };
    }

    private static Step[] findFirstSteps(Point start, char[][] tab) {
        ArrayList<Step> output = new ArrayList<>(2);
        if (start.a() > 0) {
            if (direction('S', tab[start.a() - 1][start.b()]) != 'X') {
                output.add(new Step(new Point(start.a() - 1, start.b()), 'S'));
            }
        }
        if (start.a() < tab.length - 1) {
            if (direction('N', tab[start.a() + 1][start.b()]) != 'X') {
                output.add(new Step(new Point(start.a() + 1, start.b()), 'N'));
            }
        }
        if (start.b() > 0) {
            if (direction('E', tab[start.a()][start.b() - 1]) != 'X') {
                output.add(new Step(new Point(start.a(), start.b() - 1), 'E'));
            }
        }
        if (start.b() < tab[0].length - 1) {
            if (direction('W', tab[start.a()][start.b() + 1]) != 'X') {
                output.add(new Step(new Point(start.a(), start.b() + 1), 'W'));
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

    private static char direction(char fromDir, char pipe) {
        return switch (pipe) {
            case '|' -> switch (fromDir) {
                case 'N' -> 'S';
                case 'S' -> 'N';
                default -> 'X';
            };
            case '-' -> switch (fromDir) {
                case 'E' -> 'W';
                case 'W' -> 'E';
                default -> 'X';
            };
            case 'L' -> switch (fromDir) {
                case 'N' -> 'E';
                case 'E' -> 'N';
                default -> 'X';
            };
            case 'J' -> switch (fromDir) {
                case 'N' -> 'W';
                case 'W' -> 'N';
                default -> 'X';
            };
            case '7' -> switch (fromDir) {
                case 'S' -> 'W';
                case 'W' -> 'S';
                default -> 'X';
            };
            case 'F' -> switch (fromDir) {
                case 'S' -> 'E';
                case 'E' -> 'S';
                default -> 'X';
            };
            default -> 'X';
        };
    }
}
