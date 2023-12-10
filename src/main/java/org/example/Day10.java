package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day10 {

    record Point(int x, int y) {
    }

    record Step(Point now, char from) {
    }

    public static long aoc10(Stream<String> input) {
        char[][] tab = input
                .map(String::toCharArray)
                .toArray(char[][]::new);
        Point start = findStart(tab);
        Step[] steps = findFirstSteps(start, tab);
        if (steps.length != 2) {
            System.out.println("something went wrong");
            return 0L;
        }
        long counter = 1L;
        while (steps[0] != null && steps[1] != null && !steps[0].now().equals(steps[1].now())) {
            steps[0] = nextStep(steps[0], tab);
            steps[1] = nextStep(steps[1], tab);
            counter++;
        }
        return counter;
    }

    public static long aoc10a(Stream<String> input) {
        char[][] tab = input
                .map(String::toCharArray)
                .toArray(char[][]::new);
        char[][] tab2 = new char[tab.length][tab[0].length];
        for (char[] cTab : tab2) {
            Arrays.fill(cTab, 'O');
        }
        Point start = findStart(tab);
        tab2[start.x()][start.y()] = 'S';
        Step[] steps = findFirstSteps(start, tab);
        if (steps.length != 2) {
            System.out.println("something went wrong");
            return 0L;
        }
        while (steps[0] != null && steps[1] != null && !steps[0].now().equals(steps[1].now())) {
            Step[] newSteps = new Step[2];
            newSteps[0] = nextStep(steps[0], tab);
            newSteps[1] = nextStep(steps[1], tab);
            tab2[steps[0].now().x()][steps[0].now().y()] = 'S';
            tab2[steps[1].now().x()][steps[1].now().y()] = 'S';
            steps = newSteps;
        }
        assert steps[0] != null;
        tab2[steps[0].now().x()][steps[0].now().y()] = 'S';
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
                                .filter(step -> step.now().x() != finalI)
                                .map(step -> getDirectionDiff(finalI, finalJ, step.now().x(), step.now().y()))
                                .filter(d -> d == 'N' || d == 'S')
                                .toList();
                        if (froms.size() == 1) {
                            if (from != 'X') {
                                if (from != froms.get(0)) {
                                    inside = !inside;
                                }
                                from = 'X';
                            } else {
                                from = froms.get(0);
                            }
                        } else if (froms.size() == 2) {
                            inside = !inside;
                        }

                        matched = Arrays.stream(neighbors).map(step -> step.now().y()).anyMatch(y -> y == finalJ + 1);
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

    private static char getDirectionDiff(int finalI, int finalJ, int x, int y) {
        if (finalI == x) {
            if (finalJ - y == 1) {
                return 'W';
            } else if (finalJ - y == -1) {
                return 'E';
            }
        } else if (finalJ == y) {
            if (finalI - x == 1) {
                return 'N';
            } else if (finalI - x == -1) {
                return 'S';
            }
        }
        return 'X';
    }

    private static Step nextStep(Step step, char[][] tab) {
        char dir = direction(step.from(), tab[step.now().x()][step.now().y()]);
        return switch (dir) {
            case 'N' -> new Step(new Point(step.now().x() - 1, step.now().y()), 'S');
            case 'S' -> new Step(new Point(step.now().x() + 1, step.now().y()), 'N');
            case 'E' -> new Step(new Point(step.now().x(), step.now().y() + 1), 'W');
            case 'W' -> new Step(new Point(step.now().x(), step.now().y() - 1), 'E');
            default -> null;
        };
    }

    private static Step[] findFirstSteps(Point start, char[][] tab) {
        ArrayList<Step> output = new ArrayList<>(2);
        if (start.x() > 0) {
            if (direction('S', tab[start.x() - 1][start.y()]) != 'X') {
                output.add(new Step(new Point(start.x() - 1, start.y()), 'S'));
            }
        }
        if (start.x() < tab.length - 1) {
            if (direction('N', tab[start.x() + 1][start.y()]) != 'X') {
                output.add(new Step(new Point(start.x() + 1, start.y()), 'N'));
            }
        }
        if (start.y() > 0) {
            if (direction('E', tab[start.x()][start.y() - 1]) != 'X') {
                output.add(new Step(new Point(start.x(), start.y() - 1), 'E'));
            }
        }
        if (start.y() < tab[0].length - 1) {
            if (direction('W', tab[start.x()][start.y() + 1]) != 'X') {
                output.add(new Step(new Point(start.x(), start.y() + 1), 'W'));
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
