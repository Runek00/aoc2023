package org.example;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.example.Day20.Pulse.H;
import static org.example.Day20.Pulse.L;

public class Day20 {


    enum Pulse {
        H, L

    }

    record Signal(String from, Pulse pulse, String to) {
    }


    abstract static class Module {
        final String name;
        final List<String> outputs;

        public Module(String name, List<String> outputs) {
            this.name = name;
            this.outputs = outputs;
        }

        String name() {
            return name;
        }


        List<Signal> processSignal(Signal sig) {
            modifyState(sig);
            return outputs.stream().map(outToSigFunction(sig)).toList();
        }

        void modifyState(Signal sig) {
        }


        abstract Function<String, Signal> outToSigFunction(Signal sig);

        abstract String stateString();

    }

    static class Broadcaster extends Module {
        public Broadcaster(List<String> outputs) {
            super("broadcaster", outputs);
        }

        Function<String, Signal> outToSigFunction(Signal sig) {
            return out -> new Signal(name, sig.pulse(), out);
        }

        @Override
        String stateString() {
            return "";
        }
    }

    static class FlipFlop extends Module {
        boolean state = false;

        public FlipFlop(String name, List<String> outputs) {
            super(name, outputs);
        }

        @Override
        void modifyState(Signal sig) {
            super.modifyState(sig);
            if (sig.pulse() == L) {
                state = !state;
            }
        }

        @Override
        Function<String, Signal> outToSigFunction(Signal sig) {
            if (sig.pulse() == H) {
                return out -> new Signal(name, null, null);
            } else {
                return out -> new Signal(name, state ? H : L, out);
            }
        }

        @Override
        String stateString() {
            return Boolean.toString(state);
        }
    }

    static class Conjunction extends Module {
        HashMap<String, Pulse> memory = new HashMap<>();
        boolean hasLowStates = true;

        public Conjunction(String name, List<String> outputs) {
            super(name, outputs);
        }

        void addInput(String input) {
            memory.put(input, L);
        }

        @Override
        void modifyState(Signal sig) {
            super.modifyState(sig);
            memory.put(sig.from(), sig.pulse());
            hasLowStates = sig.pulse() == L || memory.values().stream().anyMatch(val -> val == L);
        }

        @Override
        Function<String, Signal> outToSigFunction(Signal sig) {
            return out -> new Signal(name, hasLowStates ? H : L, out);
        }

        @Override
        String stateString() {
            return memory.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> entry.getKey() + entry.getValue().name())
                    .collect(Collectors.joining());
        }
    }

    public static long aoc20(Stream<String> input) {
        Map<String, Module> modMap = input.map(Day20::lineToModule).filter(Objects::nonNull).collect(Collectors.toMap(Module::name, module -> module));
        modMap.values().stream()
                .filter(val -> val instanceof Conjunction)
                .forEach(con -> modMap.values().stream()
                        .filter(module -> module.outputs.contains(con.name()))
                        .forEach(module -> ((Conjunction) con).addInput(module.name())));
        Map<String, int[]> rounds = new LinkedHashMap<>();
        String roundState = stateStrings(modMap);
        while (rounds.size() < 1000 && !rounds.containsKey(roundState)) {
            int[] roundResult = getRoundResult(modMap);
            rounds.put(roundState, roundResult);
            roundState = stateStrings(modMap);
        }
        return roundsAnalysis(rounds, roundState);
    }

    public static long aoc20a(Stream<String> input) {
        Map<String, Module> modMap = input.map(Day20::lineToModule).filter(Objects::nonNull).collect(Collectors.toMap(Module::name, module -> module));
        modMap.values().stream()
                .filter(val -> val instanceof Conjunction)
                .forEach(con -> modMap.values().stream()
                        .filter(module -> module.outputs.contains(con.name()))
                        .forEach(module -> ((Conjunction) con).addInput(module.name())));
        long counter = 1;
        while (!getRoundResult2(modMap)) {
            counter++;
        }
        return counter;
    }

    private static long roundsAnalysis(Map<String, int[]> rounds, String roundState) {
        int entryCounter = 0;
        int[] presum = new int[2];
        int[] repeatSum = new int[2];
        boolean rightOne = false;
        int presses = 1000;
        for (String k : rounds.keySet()) {
            if (Objects.equals(k, roundState)) {
                rightOne = true;
                presses -= entryCounter;
            }
            entryCounter++;
            if (!rightOne) {
                presum = addArrays(presum, rounds.get(k));
                continue;
            }
            repeatSum = addArrays(repeatSum, rounds.get(k));
        }
        int loop = entryCounter - 1000 + presses;
        repeatSum = new int[]{presses / loop * repeatSum[0], presses / loop * repeatSum[1]};
        presum = addArrays(presum, repeatSum);
        return ((long) presum[0]) * ((long) presum[1]);
    }

    static int[] addArrays(int[] arr1, int[] arr2) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Lengths not equal");
        }
        int[] out = new int[arr2.length];
        for (int i = 0; i < arr1.length; i++) {
            out[i] = arr1[i] + arr2[i];
        }
        return out;
    }

    private static int[] getRoundResult(Map<String, Module> modMap) {
        int[] roundResult = new int[2];
        Queue<Signal> signals = new ArrayDeque<>();
        signals.add(new Signal("button", L, "broadcaster"));
        while (!signals.isEmpty()) {
            Signal sig = signals.poll();
            if (sig.pulse() == null) {
                continue;
            }
            roundResult[sig.pulse() == L ? 0 : 1]++;
            if (!modMap.containsKey(sig.to())) {
                continue;
            }
            Module mod = modMap.get(sig.to());
            if (mod == null) {
                continue;
            }
            signals.addAll(mod.processSignal(sig));
        }
        return roundResult;
    }

    private static boolean getRoundResult2(Map<String, Module> modMap) {
        Queue<Signal> signals = new ArrayDeque<>();
        signals.add(new Signal("button", L, "broadcaster"));
        while (!signals.isEmpty()) {
            Signal sig = signals.poll();
            if (sig.pulse() == null) {
                continue;
            }
            if (Objects.equals(sig.to(), "rx") && sig.pulse() == L) {
                return true;
            }
            if (!modMap.containsKey(sig.to())) {
                continue;
            }
            Module mod = modMap.get(sig.to());
            if (mod == null) {
                continue;
            }
            signals.addAll(mod.processSignal(sig));
        }
        return false;
    }

    private static String stateStrings(Map<String, Module> modMap) {
        return modMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + entry.getValue().stateString())
                .collect(Collectors.joining());
    }

    private static Module lineToModule(String line) {
        String[] spline = line.split(" -> ");
        List<String> outputs = Arrays.asList(spline[1].split(", "));
        if (spline[0].startsWith("broadcaster")) {
            return new Broadcaster(outputs);
        } else if (spline[0].startsWith("%")) {
            String name = spline[0].substring(1);
            return new FlipFlop(name, outputs);
        } else if (spline[0].startsWith("&")) {
            String name = spline[0].substring(1);
            return new Conjunction(name, outputs);
        } else {
            return null;
        }
    }
}
