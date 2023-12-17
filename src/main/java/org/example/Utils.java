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

    record Point(int a, int b) {

        public Point minus(Direction dir) {
            return new Point(a - dir.change().a(), b - dir.change().b());
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
    }

}
