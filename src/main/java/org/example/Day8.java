package org.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    static long aoc8a(Stream<String> input) {
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
        record StringPair(String start, String current) {
        }
        List<StringPair> currentNodes = nodes.keySet().stream().filter(k -> k.endsWith("A")).map(n -> new StringPair(n, n)).toList();
        Map<String, long[]> nodeSteps = new HashMap<>();
        for (StringPair node : currentNodes) {
            long i = 0L;
            long init = 0L;
            while (!node.current().endsWith("Z")) {
                char step = instructions.charAt((int) i % instructions.length());
                node = new StringPair(node.start(), step == 'L' ? nodes.get(node.current()).lr().left() : nodes.get(node.current()).lr().right());
                i++;
                init++;
            }
            long diff = 0L;
            do {
                char step = instructions.charAt((int) i % instructions.length());
                node = new StringPair(node.start(), step == 'L' ? nodes.get(node.current()).lr().left() : nodes.get(node.current()).lr().right());
                i++;
                diff++;
            } while (!node.current().endsWith("Z"));
            nodeSteps.put(node.start(), new long[]{init, diff});
        }
        return nodeSteps.values().stream().map(longs -> longs[0]).reduce(1L, Utils::lcm);
    }

}
