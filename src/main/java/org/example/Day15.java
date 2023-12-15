package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Day15 {
    public static long aoc15(String input) {
        return Arrays.stream(input.split(","))
                .mapToLong(Day15::hash)
                .sum();
    }

    record Lens(String label, int focal) {
    }

    record Instruction(Lens lens, boolean remove) {
        Instruction(String label) {
            this(new Lens(label, -1), true);
        }

        String label() {
            return lens.label();
        }
    }

    public static long aoc15a(String input) {
        ArrayList<Lens>[] boxes = new ArrayList[256];
        Map<String, Integer> position = new HashMap<>();
        Arrays.stream(input.split(","))
                .map(Day15::instruction)
                .forEach(instr -> {
                    int hash = (int) hash(instr.label());
                    if (instr.remove()) {
                        Integer pos = position.get(instr.label());
                        if (pos != null) {
                            boxes[hash].remove(pos.intValue());
                            position.remove(instr.label());
                            for (int i = pos; i < boxes[hash].size(); i++) {
                                position.put(boxes[hash].get(i).label(), i);
                            }
                        }
                    } else {
                        if (position.containsKey(instr.label())) {
                            boxes[hash].set(position.get(instr.label()), instr.lens());
                        } else {
                            if (boxes[hash] == null) {
                                boxes[hash] = new ArrayList<>();
                            }
                            boxes[hash].add(instr.lens());
                            position.put(instr.label(), boxes[hash].size() - 1);
                        }
                    }
                });
        long output = 0L;
        for (int i = 0; i < 256; i++) {
            if (boxes[i] == null) {
                continue;
            }
            for (int j = 0; j < boxes[i].size(); j++) {
                output += (long) (i + 1) * (j + 1) * boxes[i].get(j).focal();
            }
        }
        return output;
    }

    private static Instruction instruction(String s) {
        if (s.contains("-")) {
            return new Instruction(s.split("-")[0]);
        } else {
            String[] ss = s.split("=");
            return new Instruction(new Lens(ss[0], Integer.parseInt(ss[1])), false);
        }
    }

    private static long hash(String s) {
        long output = 0L;
        for (char c : s.toCharArray()) {
            output = ((output + c) * 17) % 256;
        }
        return output;
    }
}
