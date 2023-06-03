package puzzles.hoppers.model;

import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class HoppersModel {
    /** the collection of observers of this model */
    private final List<Observer<HoppersModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private HoppersConfig currentConfig;

    // For resetting, stores the initial filename
    private String initialFile;

    // When selecting coordinates
    private int[] selectedCoord;


    // Gets the board, used for updating buttons
    public char[][] getBoard() {
        return currentConfig.getBoard();
    }

    public int getWidth() {
        return currentConfig.getWIDTH();
    }

    public int getHeight() {
        return currentConfig.getHEIGHT();
    }

    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<HoppersModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String msg) {
        for (var observer : observers) {
            observer.update(this, msg);
        }
    }

    public HoppersModel(String filename) throws IOException {
        initialFile = filename;
        selectedCoord = null;
        currentConfig = new HoppersConfig(filename);
    }

    // Hint method
    public void hint() {
        // Try to solve puzzle
        Solver solver = new Solver();
        LinkedList<Configuration> solution = solver.ModelBFSSolver(currentConfig);
        selectedCoord = null;

        // If solution exits
        if (solution != null) {
            // Advance to next step
            if (solution.size() > 1) {
                currentConfig = (HoppersConfig) solution.get(1);
                // Alert
                alertObservers("Next step!");
            }
            else {
                alertObservers("Solution reached.");
            }
        }
        else {
            // Puzzle impossible
            alertObservers("There is no solution from here!");
        }
    }

    // Select method
    public void select(int row, int col) {
        // If no place currently selected
        if (selectedCoord == null) {
            // Valid selection
            if (!currentConfig.OutOfBounds(col, row) && currentConfig.isFrog(col, row)) {
                selectedCoord = new int[] {col, row};
                alertObservers("Selected (" + row + ", " + col + ")");
            }
            else {
                // Invalid
                alertObservers("No frog at (" + row + ", " + col + ")");
            }
        }
        // If place is already selected, attempt to move
        else {
            // Convert destination to DIRECTION
            HoppersConfig.DIRECTION myDirect = toDirection(col, row);
            if (myDirect == null) {
                // Fail
                alertObservers("Can't jump from (" + selectedCoord[1] + ", " + selectedCoord[0] + ")  to (" + row + ", " + col + ")");
            }
            else {
                // Try to make new config
                HoppersConfig newCon = currentConfig.tryNewConfig(selectedCoord[0], selectedCoord[1], myDirect);

                // If null, invalid
                if (newCon == null) {
                    alertObservers("Can't jump from (" + selectedCoord[1] + ", " + selectedCoord[0] + ")  to (" + row + ", " + col + ")");
                }
                else {
                    // update da config
                    currentConfig = newCon;
                    alertObservers("Jumped from (" + selectedCoord[1] + ", " + selectedCoord[0] + ")  to (" + row + ", " + col + ")");
                }
            }

            // Reset selection
            selectedCoord = null;
        }
    }

    // converts a destination index to a DIRECTION, assumes that selectedCoord is assigned
    // Because of the way I implemented the config, I needed this method for the select function
    private HoppersConfig.DIRECTION toDirection(int x, int y) {
        int sX = selectedCoord[0];
        int sY = selectedCoord[1];

        // NE
        if (sX == x - 2 && sY == y + 2) {
            return HoppersConfig.DIRECTION.NE;
        }
        // SE
        if (sX == x - 2 && sY == y - 2) {
            return HoppersConfig.DIRECTION.SE;
        }
        // SW
        if (sX == x + 2 && sY == y - 2) {
            return HoppersConfig.DIRECTION.SW;
        }
        // NW
        if (sX == x + 2 && sY == y + 2) {
            return HoppersConfig.DIRECTION.NW;
        }

        if (sY % 2 == 0) {
            // Only even indexes can move in all 8 directions
            // N
            if (sX == x && sY == y + 4) {
                return HoppersConfig.DIRECTION.N;
            }
            // S
            if (sX == x && sY == y - 4) {
                return HoppersConfig.DIRECTION.S;
            }
            // E
            if (sX == x - 4 && sY == y) {
                return HoppersConfig.DIRECTION.E;
            }
            // W
            if (sX == x + 4 && sY == y) {
                return HoppersConfig.DIRECTION.W;
            }
        }

        // Coord is INVALID
        return null;
    }

    // Load
    public void load(String filename) {
        try {
            currentConfig = new HoppersConfig(filename);
            initialFile = filename;
            alertObservers("Loaded: " + filename.substring(filename.lastIndexOf("\\") + 1));
        }
        catch (IOException e) {
            alertObservers("Failed to load: " + filename);
        }
    }

    // Reset
    public void reset() {
        try {
            currentConfig = new HoppersConfig(initialFile);
            alertObservers("Puzzle reset!");
        }
        catch (IOException e) {
            alertObservers("Failed to load: " + initialFile);
        }
    }

    @Override
    public String toString() {
        return this.currentConfig.toString();
    }
}
