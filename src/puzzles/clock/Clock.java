package puzzles.clock;

import puzzles.common.solver.Solver;

public class Clock {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Clock hours stop end"));
        } else {
            System.out.printf("Hours: %s, Start: %s, End: %s %n", args[0], args[1], args[2]);
            ClockConfig clockConfig = new ClockConfig(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
            Solver.PrintBFSSolution(clockConfig);
        }
    }
}
