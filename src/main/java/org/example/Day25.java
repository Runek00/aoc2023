package org.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day25 {
    public static int aoc25(Stream<String> input) {
//        input.forEach(line -> {
//            String s1 = line.split(": ")[0].trim();
//            for (String n : line.split(": ")[1].trim().split(" ")) {
//                System.out.println(s1 + " " + n.trim());
//            }
//        });
//        return 0;
        Map<String, Set<String>> nodeMap = new HashMap<>();
        input.forEach(line -> {
            String s1 = line.split(": ")[0].trim();
            for (String n : line.split(": ")[1].trim().split(" ")) {
                Set<String> set = nodeMap.getOrDefault(s1, new HashSet<>());
                set.add(n);
                nodeMap.put(s1, set);
                Set<String> set2 = nodeMap.getOrDefault(n, new HashSet<>());
                set2.add(s1);
                nodeMap.put(n, set2);
            }
        });
        int counter = 0;
        Map<String, Integer> nodeIds = new HashMap<>();
        for (String s : nodeMap.keySet()) {
            nodeIds.put(s, counter++);
        }

        nodeIds.forEach((key, value) -> System.out.println(value + ", " + key));
        nodeMap.forEach((key, value) -> value.forEach(val -> System.out.println(nodeIds.get(key) + ", " + nodeIds.get(val))));
        return 0;


//        if(nodeMap.keySet().stream().findFirst().isEmpty()) {
//            return 0;
//        }
//        Set<String> part = new HashSet<>();
//        String start = nodeMap.keySet().stream().findFirst().get();
//        part.add(start);
//        part.addAll(nodeMap.get(start));
//        part = expandDense(part, nodeMap);
//        part = trimLonelyOnes(part, nodeMap);
//        return part.size() * (nodeMap.size() - part.size());
    }

    private static Set<String> trimLonelyOnes(Set<String> part, Map<String, Set<String>> nodeMap) {
        return part.stream().filter(node -> {
            Set<String> neighbours = new HashSet<>(nodeMap.get(node));
            int s1 = neighbours.size();
            neighbours.removeAll(part);
            int s2 = neighbours.size();
            return s1-s2 != 1;
        })
                .collect(Collectors.toSet());
    }

    private static Set<String> expandDense(Set<String> part, Map<String, Set<String>> nodeMap) {
        Set<String> toAdd;
        do {
            Set<String> finalPart = part;
            Map<String, Long> candidates = part.stream()
                    .flatMap(node -> nodeMap.get(node).stream())
                    .filter(cand -> !finalPart.contains(cand))
                    .collect(Collectors.groupingBy(n -> n, Collectors.counting()));
            toAdd = candidates.entrySet()
                    .stream()
//                    .filter(entry -> entry.getValue() > 1)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            part.addAll(toAdd);
//            part = trimLonelyOnes(part, nodeMap);
        } while (externalNodes(part, nodeMap) > 3L);
        return part;
    }

    private static long externalNodes(Set<String> part, Map<String, Set<String>> nodeMap) {
        return part.stream().flatMap(node -> nodeMap.get(node).stream()).filter(node -> !part.contains(node)).count();
    }
}
