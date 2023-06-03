package puzzles.hoppers.solver;

import puzzles.clock.ClockConfig;
import puzzles.common.solver.Solver;
import puzzles.hoppers.model.HoppersConfig;

import java.io.IOException;

public class Hoppers {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Hoppers filename");
        }
        else {
            try {
                HoppersConfig hopperConfig = new HoppersConfig(args[0]);
                Solver.PrintBFSSolution(hopperConfig);
            }
            catch (IOException e) {
                System.out.println("ERROR WITH FILE NAME");
            }
        }
    }
}
