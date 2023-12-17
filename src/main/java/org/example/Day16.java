package org.example;

import org.example.Utils.Point;
import org.example.Utils.Step;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.example.Utils.streamTo2DCharArray;

public class Day16 {


    public static int aoc16(Stream<String> input) {
        Step entryPoint = new Step(0, 0, 'W');
        char[][] tab = streamTo2DCharArray(input);
        return energizedFromPoint(entryPoint, tab);
    }

    public static int aoc16a(Stream<String> input) {
        char[][] tab = streamTo2DCharArray(input);

        int out1 = IntStream.range(0, tab.length).parallel()
                .map(i -> {
                    int output = 0;
                    Step entryPoint = new Step(i, 0, 'W');
                    output = Math.max(output, energizedFromPoint(entryPoint, tab));
                    entryPoint = new Step(i, tab[0].length - 1, 'E');
                    output = Math.max(output, energizedFromPoint(entryPoint, tab));
                    return output;
                })
                .max().orElse(0);

        int out2 = IntStream.range(0, tab[0].length).parallel()
                .map(i -> {
                    int output = 0;
                    Step entryPoint = new Step(0, i, 'N');
                    output = Math.max(output, energizedFromPoint(entryPoint, tab));
                    entryPoint = new Step(tab.length - 1, i, 'S');
                    output = Math.max(output, energizedFromPoint(entryPoint, tab));
                    return output;
                })
                .max().orElse(0);

        return Math.max(out1, out2);
    }

    private static int energizedFromPoint(Step entryPoint, char[][] tab) {
        Map<Point, Set<Step>> stepMap = new HashMap<>();
        Queue<Step> branching = new ArrayDeque<>();
        branching.add(entryPoint);

        while (!branching.isEmpty()) {
            Step step = branching.poll();

            while (inTab(step, tab)) {
                if (checkNewStep(stepMap, step)) break;
                List<Step> steps = switch (tab[step.a()][step.b()]) {
                    case '.' -> onEmpty(step);
                    case '/' -> onMirror(step);
                    case '\\' -> onBackMirror(step);
                    case '-' -> onHSplitter(step);
                    case '|' -> onVSplitter(step);
                    default -> throw new IllegalStateException("Unexpected value: " + tab[step.a()][step.b()]);
                };
                for (int i = 1; i < steps.size(); i++) {
                    branching.add(steps.get(i));
                }
                step = steps.getFirst();
            }
        }

        return stepMap.size();
    }

    private static List<Step> onMirror(Step step) {
        return switch (step.from()) {
            case 'E' -> List.of(new Step(step.a() + 1, step.b(), 'N'));
            case 'W' -> List.of(new Step(step.a() - 1, step.b(), 'S'));
            case 'N' -> List.of(new Step(step.a(), step.b() - 1, 'E'));
            case 'S' -> List.of(new Step(step.a(), step.b() + 1, 'W'));
            default -> throw new IllegalStateException("Unexpected value: " + step.from());
        };
    }

    private static List<Step> onBackMirror(Step step) {
        return switch (step.from()) {
            case 'W' -> List.of(new Step(step.a() + 1, step.b(), 'N'));
            case 'E' -> List.of(new Step(step.a() - 1, step.b(), 'S'));
            case 'S' -> List.of(new Step(step.a(), step.b() - 1, 'E'));
            case 'N' -> List.of(new Step(step.a(), step.b() + 1, 'W'));
            default -> throw new IllegalStateException("Unexpected value: " + step.from());
        };
    }

    private static List<Step> onVSplitter(Step step) {
        return switch (step.from()) {
            case 'E', 'W' -> List.of(new Step(step.a() + 1, step.b(), 'N'), new Step(step.a() - 1, step.b(), 'S'));
            case 'N', 'S' -> onEmpty(step);
            default -> throw new IllegalStateException("Unexpected value: " + step.from());
        };
    }

    private static List<Step> onHSplitter(Step step) {
        return switch (step.from()) {
            case 'E', 'W' -> onEmpty(step);
            case 'N', 'S' -> List.of(new Step(step.a(), step.b() - 1, 'E'), new Step(step.a(), step.b() + 1, 'W'));
            default -> throw new IllegalStateException("Unexpected value: " + step.from());
        };
    }

    private static boolean checkNewStep(Map<Point, Set<Step>> stepMap, Step step) {
        Set<Step> stepSet = stepMap.getOrDefault(step.p(), new HashSet<>());
        if (stepSet.contains(step)) {
            return true;
        } else {
            stepSet.add(step);
        }
        stepMap.put(step.p(), stepSet);
        return false;
    }

    private static List<Step> onEmpty(Step step) {
        return switch (step.from()) {
            case 'N' -> List.of(new Step(step.a() + 1, step.b(), 'N'));
            case 'S' -> List.of(new Step(step.a() - 1, step.b(), 'S'));
            case 'E' -> List.of(new Step(step.a(), step.b() - 1, 'E'));
            case 'W' -> List.of(new Step(step.a(), step.b() + 1, 'W'));
            default -> throw new IllegalStateException("Unexpected value: " + step.from());
        };
    }

    private static boolean inTab(Step step, char[][] tab) {
        return step.a() >= 0 && step.a() < tab.length && step.b() >= 0 && step.b() < tab[0].length;
    }
}
