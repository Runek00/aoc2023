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


}
