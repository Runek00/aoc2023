package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Utils {

    static Stream<String> fileToStream(String filePath) throws IOException {
        return Files.lines(Path.of(filePath));
    }

    static String fileToString(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    static char[][] streamTo2DCharArray(Stream<String> input) {
        return input
                .map(String::toCharArray)
                .toArray(char[][]::new);
    }

    static long lcm(long number1, long number2) {
        return number1 * number2 / gcd(number1, number2);
    }

    static long gcd(long number1, long number2) {
        if (number1 == 0 || number2 == 0) {
            return number1 + number2;
        } else {
            long absNumber1 = Math.abs(number1);
            long absNumber2 = Math.abs(number2);
            long biggerValue = Math.max(absNumber1, absNumber2);
            long smallerValue = Math.min(absNumber1, absNumber2);
            return gcd(biggerValue % smallerValue, smallerValue);
        }
    }

    static boolean inTab(Step step, char[][] tab) {
        return step.a() >= 0 && step.a() < tab.length && step.b() >= 0 && step.b() < tab[0].length;
    }

    static boolean inTab(Point point, char[][] tab) {
        return point.a() >= 0 && point.a() < tab.length && point.b() >= 0 && point.b() < tab[0].length;
    }

    record Point(int a, int b) {

        public Point minus(Direction dir) {
            return new Point(a - dir.change().a(), b - dir.change().b());
        }

        public Point plus(Direction dir) {
            return new Point(a + dir.change().a(), b + dir.change().b());
        }
    }

    record Step(Point p, Direction from) {
        Step(int a, int b, Direction from) {
            this(new Point(a, b), from);
        }

        int a() {
            return p.a();
        }

        int b() {
            return p.b();
        }
    }

    enum Direction {
        N(-1, 0),
        S(1, 0),
        E(0, 1),
        W(0, -1),
        ;

        private final Point change;

        Direction(int a, int b) {
            this.change = new Point(a, b);
        }

        public Point change() {
            return change;
        }

        public Direction opposite() {
            return switch (this) {
                case N -> S;
                case S -> N;
                case E -> W;
                case W -> E;
            };
        }

        public static Direction[] getAll() {
            return new Direction[]{N, S, E, W};
        }
    }

}
