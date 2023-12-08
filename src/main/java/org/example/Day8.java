package org.example;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Day8 {

    record LR(String left, String right) {
        public LR(String both) {
            this(both.split(", ")[0].replace("(", "").trim(), both.split(", ")[1].replace(")", "").trim());
        }
    }

    record StartAndLR(String startNode, LR lr) {
    }

    static long aoc8(Stream<String> input) {
        Map<String, StartAndLR> nodes = new HashMap<>();
        AtomicReference<String> instructionsRef = new AtomicReference<>();
        input.filter(line -> !line.trim().isEmpty()).forEach(line -> {
            if (line.contains("=")) {
                nodes.put(line.split(" = ")[0], new StartAndLR(line.split(" = ")[0], new LR(line.split(" = ")[1])));
            } else {
                instructionsRef.set(line);
            }
        });
        String instructions = instructionsRef.get();
        String currentNode = "AAA";
        long i = 0L;
        while (!currentNode.equals("ZZZ")) {
            char step = instructions.charAt((int) i % instructions.length());
            i++;
            if (step == 'L') {
                currentNode = nodes.get(currentNode).lr().left();
            } else {
                currentNode = nodes.get(currentNode).lr().right();
            }
        }
        return i;
    }
}
