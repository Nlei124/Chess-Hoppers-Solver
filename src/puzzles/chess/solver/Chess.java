package puzzles.chess.solver;

import puzzles.chess.model.ChessConfig;
import puzzles.common.solver.Solver;

import java.io.IOException;

public class Chess {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Chess filename");
        }
        else {
            try {
                System.out.printf("File: %s, %n", args[0]);
                ChessConfig chessConfig = new ChessConfig(args[0]);
                Solver.PrintBFSSolution(chessConfig);
            }
            catch (IOException e)
            {
                System.out.println("ERROR WITH FILE NAME");
            }

        }
    }
}
