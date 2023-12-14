package org.example;

import java.util.Arrays;
import java.util.function.Function;

public class Day13 {

    record CompResult(boolean smudge, boolean madeit) {
    }

    record SamesiesInput(char[][]tab, int a, int b, boolean smudge){}

    public static long aoc13(String input) {
        return Arrays.stream(input.split("\r\n\r\n"))
                .map(part -> Arrays.stream(part.split("\r\n"))
                        .map(String::toCharArray)
                        .toArray(char[][]::new))
                .mapToLong(part -> getResult(part, false))
                .sum();
    }

    public static long aoc13a(String input) {
        return Arrays.stream(input.split("\r\n\r\n"))
                .map(part -> Arrays.stream(part.split("\r\n"))
                        .map(String::toCharArray)
                        .toArray(char[][]::new))
                .mapToLong(part -> getResult(part, true))
                .sum();
    }

    private static long getResult(char[][] part, boolean smudge) {
        long count = 100 * countMirroredLines(part, smudge, part.length, Day13::sameRows);
        if (count == 0L) {
            count = countMirroredLines(part, smudge, part[0].length, Day13::sameColumns);
        }
        return count;
    }

    private static long countMirroredLines(char[][] part, boolean initialSmudge, int maxLength, Function<SamesiesInput, CompResult> comparing) {
        FindStart:
        for (int i = 0; i < maxLength - 1; i++) {
            CompResult compResult = comparing.apply(new SamesiesInput(part, i, i + 1, initialSmudge));
            boolean smudge = compResult.smudge();
            if (compResult.madeit()) {
                int a = i - 1;
                int b = i + 2;
                while (a >= 0 && b < maxLength) {
                    compResult = comparing.apply(new SamesiesInput(part, a, b, smudge));
                    smudge = compResult.smudge();
                    if (!compResult.madeit()) {
                        continue FindStart;
                    }
                    a--;
                    b++;
                }
                if (!smudge) {
                    return (long) i + 1;
                }
            }
        }
        return 0L;
    }

    private static CompResult sameRows(SamesiesInput params) {
        boolean smudge = params.smudge();
        for (int i = 0; i < params.tab()[0].length; i++) {
            if (params.tab()[params.a()][i] != params.tab()[params.b()][i]) {
                if (!smudge) {
                    return new CompResult(false, false);
                } else {
                    smudge = false;
                }
            }
        }
        return new CompResult(smudge, true);
    }

    private static CompResult sameColumns(SamesiesInput params) {
        boolean smudge = params.smudge();
        for (char[] chars : params.tab()) {
            if (chars[params.a()] != chars[params.b()]) {
                if (!smudge) {
                    return new CompResult(false, false);
                } else {
                    smudge = false;
                }
            }
        }
        return new CompResult(smudge, true);
    }
}
