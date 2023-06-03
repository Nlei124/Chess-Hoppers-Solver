package puzzles.chess.model;

import puzzles.common.Coordinates;
import puzzles.common.Observer;
import puzzles.common.solver.Configuration;
import puzzles.common.solver.Solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChessModel {
    /** the collection of observers of this model */
    private final List<Observer<ChessModel, String>> observers = new LinkedList<>();

    /** the current configuration */
    private ChessConfig currentConfig;

    /** the selected coordinate */
    private Coordinates select;

    /** loaded filename*/
    private String loadedfile;


    /**
     * The view calls this to add itself as an observer.
     *
     * @param observer the view
     */
    public void addObserver(Observer<ChessModel, String> observer) {
        this.observers.add(observer);
    }

    /**
     * The model's state has changed (the counter), so inform the view via
     * the update method
     */
    private void alertObservers(String data) {
        for (var observer : observers) {
            observer.update(this, data);
        }
    }

    public ChessConfig getCurrentConfig(){
        return currentConfig;
    }

    public ChessModel(String filename) throws IOException {
        currentConfig = new ChessConfig(filename);
        select = null;
        loadedfile = filename;
    }

    /** Makes the next move towards the solution
     * If the solution has been reached no move will be made*/
    public void hint()
    {
        LinkedList<Configuration> configs = Solver.ModelBFSSolver(currentConfig);
        //When there are no moves toward a solution
        if(configs == null) {
            alertObservers("Unsolvable Puzzle; Load or Reset.");
        }
        //When the solution is reached, no move is made
        else if(currentConfig.isSolution()) {
            alertObservers("Puzzle Already Solved; Load or Reset.");
        }
        //Otherwise it advances the config by one
        else
        {
            currentConfig = (ChessConfig) configs.get(1);
            alertObservers("Next Step.");
        }
    }

    /**
     * loads the chess config from the given file
     * @param filename the file's name
     */
    public void load(String filename)
    {
        try
        {
            currentConfig = new ChessConfig(filename);
            loadedfile = filename;
            alertObservers("Loaded: " + filename.substring(filename.lastIndexOf("\\") + 1));
        }
        catch(IOException e)
        {
            alertObservers("Failed to Load: " + filename);
        }
    }

    /**
     * First select call tries to select a piece at the given position
     * Then the second call will try to move that piece to the given position
     * The piece must capture another piece
     * @param row row of the square
     * @param col col of the square
     */
    public void select(int row, int col)
    {
        //Checking if there are any captures left
        if(!currentConfig.anyValidCaptures())
        {
            //if not then send message that it is either solved or unsolvable
            if(currentConfig.isSolution()) {
                alertObservers("Solution Reached; Load or Reset.");
            }
            else {
                alertObservers("No Captures Remaining; Load or Reset.");
            }
            return;
        }
        //the first select call
        if(select == null) {
            //check if the selected position is valid or not
            if(currentConfig.isValidSelection(row, col))
            {
                select = new Coordinates(row, col);
                alertObservers(String.format("Selected (%d,%d)", row, col));
            }
            //if it's not valid no selection is made
            else {
                alertObservers(String.format("Invalid Selection (%d,%d)", row, col));
            }
            return;
        }

        //The capturing call
        boolean validCap = false;
        ArrayList<Coordinates> moves = currentConfig.getPieceMoves(currentConfig.getPiece(select), select);
        for (Coordinates move: moves) {
            if(move.row() == row && move.col() == col) {
                validCap = true;
                break;
            }
        }
        if(validCap)
        {
            currentConfig = new ChessConfig(currentConfig, select, new Coordinates(row, col));
            alertObservers(String.format("Captured from (%d,%d) to (%d,%d)", select.row(), select.col(), row, col));
        }
        else
        {
            alertObservers(String.format("Invalid Capture from (%d,%d) to (%d,%d)", select.row(), select.col(), row, col));
        }
        //reset the selection
        select = null;
    }

    /**
     * Resets the puzzle by loading the last loaded file
     */
    public void reset()
    {
        try
        {
            currentConfig = new ChessConfig(loadedfile);
            alertObservers("Reset puzzle.");
        }
        catch (IOException e)
        {
            alertObservers("Failed to Load: " + loadedfile);
        }
    }
    /** Gets the amount of rows in the config*/
    public int getRows()
    {
        return currentConfig.getBoardRows();
    }
    /** Gets the amount of columns in the config*/
    public int getCols()
    {
        return currentConfig.getBoardCols();
    }
    /** Gets the character at the row and column*/
    public char getVal(int row, int col)
    {
        return currentConfig.getPiece(new Coordinates(row, col));
    }


    @Override
    public String toString() {
        return currentConfig.boardString();
    }
}
