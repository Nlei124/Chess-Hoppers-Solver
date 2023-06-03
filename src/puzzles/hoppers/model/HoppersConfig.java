package puzzles.hoppers.model;

import puzzles.common.solver.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// Author: Colby Heaton

public class HoppersConfig implements Configuration{
    /** a cell that has not been assigned a value yet */
    private final static char EMPTY = '.';
    /** a cell that cannot be entered */
    private final static char INVAL = '*';
    /** a green frog cell */
    private final static char GREEN = 'G';
    /** a red frog cell */
    private final static char RED = 'R';

    private static int WIDTH;
    private static int HEIGHT;

    // The current board / config
    private char[][] board;

    // These getters are for creating the GUI's board
    public char[][] getBoard() {
        return board;
    }
    public static int getHEIGHT() {
        return HEIGHT;
    }
    public static int getWIDTH() {
        return WIDTH;
    }

    // Represents the directions that a frog can hop
    public enum DIRECTION {
        N,
        NE,
        E,
        SE,
        S,
        SW,
        W,
        NW
    }

    /**
     * Read in the hoppers puzzle from the filename.
     *
     * @param filename the name of the file
     * @throws IOException thrown if there is a problem opening or reading the file
     */
    public HoppersConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            // read first line: rows cols
            String[] fields = in.readLine().split("\\s+");
            WIDTH = Integer.parseInt(fields[1]);
            HEIGHT = Integer.parseInt(fields[0]);

            // build the board
            // initialize config
            this.board = new char[HEIGHT][WIDTH];
            for (int i = 0; i < HEIGHT; i++) {
                // read next line
                fields = in.readLine().split("\\s+");
                for (int j = 0; j < WIDTH; j++) {
                    board[i][j] = fields[j].charAt(0);
                }
            }
        }
    }

    // Checks if the given coord holds a green frog
    private boolean isGreen(int xCoord, int yCoord) {
        return (board[yCoord][xCoord] == GREEN);
    }

    // Checks if the given coord holds any frog
    public boolean isFrog(int xCoord, int yCoord) {
        return (board[yCoord][xCoord] == GREEN || board[yCoord][xCoord] == RED);
    }

    // Ensures there are no green frogs
    @Override
    public boolean isSolution() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (isGreen(j, i)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> successors = new ArrayList<>();

        // loop thru all spots
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {

                // if this spot is a frog
                if (isFrog(j, i)) {

                    // determine which directions to check
                    DIRECTION[] directions;
                    if (i % 2 == 1) {
                        // can only go to four corners
                        directions = new DIRECTION[]{
                            DIRECTION.NE,
                            DIRECTION.SE,
                            DIRECTION.SW,
                            DIRECTION.NW
                        };
                    }
                    else {
                        // can go in any direction
                        directions = new DIRECTION[]{
                            DIRECTION.N,
                            DIRECTION.NE,
                            DIRECTION.E,
                            DIRECTION.SE,
                            DIRECTION.S,
                            DIRECTION.SW,
                            DIRECTION.W,
                            DIRECTION.NW
                        };
                    }

                    // try to create configs for each direction
                    for (int k = 0; k < directions.length; k++) {
                        HoppersConfig newConfig = tryNewConfig(j, i, directions[k]);
                        if (newConfig != null) {
                            successors.add(newConfig);
                        }
                    }
                }
            }
        }

        return successors;
    }

    public HoppersConfig tryNewConfig(int xCoord, int yCoord, DIRECTION direction) {
        // VALIDITY CHECK!
        int[] destination = new int[2];
        int[] halfway = new int[2];

        // generate steps based on directions
        switch (direction) {
            case N:
                halfway[0] = xCoord;
                halfway[1] = yCoord - 2;
                destination[0] = xCoord;
                destination[1] = yCoord - 4;
                break;
            case NE:
                halfway[0] = xCoord + 1;
                halfway[1] = yCoord - 1;
                destination[0] = xCoord + 2;
                destination[1] = yCoord - 2;
                break;
            case E:
                halfway[0] = xCoord + 2;
                halfway[1] = yCoord;
                destination[0] = xCoord + 4;
                destination[1] = yCoord;
                break;
            case SE:
                halfway[0] = xCoord + 1;
                halfway[1] = yCoord + 1;
                destination[0] = xCoord + 2;
                destination[1] = yCoord + 2;
                break;
            case S:
                halfway[0] = xCoord;
                halfway[1] = yCoord + 2;
                destination[0] = xCoord;
                destination[1] = yCoord + 4;
                break;
            case SW:
                halfway[0] = xCoord - 1;
                halfway[1] = yCoord + 1;
                destination[0] = xCoord - 2;
                destination[1] = yCoord + 2;
                break;
            case W:
                halfway[0] = xCoord - 2;
                halfway[1] = yCoord;
                destination[0] = xCoord - 4;
                destination[1] = yCoord;
                break;
            case NW:
                halfway[0] = xCoord - 1;
                halfway[1] = yCoord - 1;
                destination[0] = xCoord - 2;
                destination[1] = yCoord - 2;
                break;
        }

        // CHECK 1 = HALFWAY VALID
        if (OutOfBounds(halfway[0], halfway[1])) {
            return null;
        }

        // CHECK 2 = HALFWAY HAS FROG, NOT RED
        if (!isGreen(halfway[0], halfway[1])) {
            return null;
        }

        // CHECK 3 = DESTINATION VALID, AND NO FROG
        if (OutOfBounds(destination[0], destination[1]) || isFrog(destination[0], destination[1])) {
            return null;
        }

        return new HoppersConfig(this, xCoord, yCoord, halfway, destination);
    }

    // Checks if given coord is OOB
    public boolean OutOfBounds(int xCoord, int yCoord) {
        return (xCoord < 0 || xCoord >= WIDTH || yCoord < 0 || yCoord >= HEIGHT);
    }

    // Copy constructor
    private HoppersConfig(HoppersConfig other, int xCoord, int yCoord, int[] halfway, int[] destination){
        // copy board
        this.board = new char[HEIGHT][WIDTH];
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                this.board[i][j] = other.board[i][j];
            }
        }

        // remove frog from start
        char origFrog = this.board[yCoord][xCoord];
        this.board[yCoord][xCoord] = EMPTY;

        // remove inbetween frog
        this.board[halfway[1]][halfway[0]] = EMPTY;

        // place frog at destination
        this.board[destination[1]][destination[0]] = origFrog;

    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String output = "";
        for (int i = 0; i < HEIGHT + 2; i++) {

            // Kind of a mess
            for (int j = 0; j < WIDTH + 1; j++) {
                if (i == 0 && j >= 1) {
                    // print hori numbers
                    output += " " + (j-1);
                }
                else if (i == 1 && j >= 1) {
                    // print hori dashes
                    output += "--";
                }
                else if (j == 0 && i > 1) {
                    // print vert numbers
                    output += (i-2) + "|";
                }
                else if (j < 1 && i < 2) {
                    output += "  ";
                }
                else {
                    // Print the actual board vals
                    output += " " + this.board[i - 2][j - 1];
                }
            }
            output += "\n";

        }
        return output;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof HoppersConfig oHop) {
            return Arrays.deepEquals(board, oHop.board);
        }
        return false;
    }
}
