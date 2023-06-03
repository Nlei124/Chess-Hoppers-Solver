package puzzles.strings;

import puzzles.common.solver.Solver;

public class Strings {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println(("Usage: java Strings start finish"));
        } else {
            System.out.printf("Start: %s, End: %s, %n", args[0], args[1]);
            StringsConfig stringsConfig = new StringsConfig(args[0], args[1]);
            Solver.PrintBFSSolution(stringsConfig);
        }
    }
}
