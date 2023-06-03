package puzzles.chess.model;

import puzzles.chess.solver.Chess;
import puzzles.common.solver.Configuration;
import puzzles.common.Coordinates;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// TODO: implement your ChessConfig for the common solver

public class ChessConfig implements Configuration {
    /** a cell without a piece*/
    private final static char EMPTY = '.';
    /** Pawn piece*/
    private final static char PAWN = 'P';
    /** Bishop piece*/
    private final static char BISHOP = 'B';
    /** Knight piece*/
    private final static char KNIGHT = 'N';
    /** Rook piece*/
    private final static char ROOK = 'R';
    /** Queen piece*/
    private final static char QUEEN = 'Q';
    /** King piece*/
    private final static char KING = 'K';


    private static int BOARD_ROWS;
    private static int BOARD_COLS;
    private char[][] board;
    private int piecesRemaining;
    private ArrayList<Coordinates> pieceLocations;
    public ChessConfig(String filename) throws IOException {
        try (BufferedReader in = new BufferedReader(new FileReader(filename))) {
            // read first line: rows cols
            String[] fields = in.readLine().split("\\s+");
            BOARD_ROWS = Integer.parseInt(fields[0]);
            BOARD_COLS = Integer.parseInt(fields[1]);
            board = new char[BOARD_ROWS][BOARD_COLS];
            piecesRemaining = 0;
            pieceLocations = new ArrayList<>();

            //Build the board based on what is read in
            for(int i = 0; i < BOARD_ROWS; i++)
            {
                fields = in.readLine().split("\\s+");
                for (int j = 0; j < BOARD_COLS; j++) {
                    board[i][j] = fields[j].charAt(0);
                    //Counting the number of pieces
                    if(board[i][j] != EMPTY) {
                        piecesRemaining++;
                        pieceLocations.add(new Coordinates(i, j));
                    }
                }
            }
        }
    }
    public ChessConfig(ChessConfig other, Coordinates src, Coordinates dest)
    {
        //Copying the piece locations list
        pieceLocations = new ArrayList<>(other.pieceLocations);

        //Copying board
        board = new char[BOARD_ROWS][BOARD_COLS];
        for(int i = 0; i < BOARD_ROWS; i++)
        {
            if (BOARD_COLS >= 0) {
                System.arraycopy(other.board[i], 0, board[i], 0, BOARD_COLS);
            }
        }
        //Moving the piece
        board[dest.row()][dest.col()] = board[src.row()][src.col()];
        board[src.row()][src.col()] = EMPTY;

        //Removing a piece
        pieceLocations.remove(src);
        piecesRemaining = other.piecesRemaining-1;
    }

    /**
     * gets the value at a given coordinate
     * @param pos the position on the board
     * @return the piece at that location
     */
    public char getPiece(Coordinates pos)
    {
        return board[pos.row()][pos.col()];
    }
    /**
     * Gets the valid moves of the specified piece
     * Calls the respective get___Moves function
     * @param piece type of piece
     * @param pos position of the piece
     * @return a list of moves the specified piece can go to take a piece
     */
    public ArrayList<Coordinates> getPieceMoves(char piece, Coordinates pos)
    {
        switch (piece) {
            case PAWN -> {
                return getPawnMoves(pos);
            }
            case BISHOP -> {
                return getBishopMoves(pos);
            }
            case KNIGHT -> {
                return getKnightMoves(pos);
            }
            case ROOK -> {
                return getRookMoves(pos);
            }
            case QUEEN -> {
                return getQueenMoves(pos);
            }
            case KING -> {
                return getKingMoves(pos);
            }
            default -> {
            }
        }
        return null;
    }
    /**
     * Gets all the pawn moves that takes a piece
     * @param pos position of the pawn
     * @return a list of places the pawn will move to take a piece, list will be empty if there are none
     */
    private ArrayList<Coordinates> getPawnMoves(Coordinates pos)
    {
        ArrayList<Coordinates> moves = new ArrayList<>();
        if(pos.row() == 0)
            return moves;
        //If the pawn is not on far left
        if(pos.col() > 0){
            Coordinates temp = new Coordinates(pos.row()-1, pos.col()-1);
            if(getPiece(temp) != EMPTY)
                moves.add(temp);
        }
        //If the pawn is not on far right
        if(pos.col() < BOARD_COLS-1){
            Coordinates temp = new Coordinates(pos.row()-1, pos.col()+1);
            if(getPiece(temp) != EMPTY)
                moves.add(temp);
        }
        return moves;
    }
    /**
     * Gets all the knight moves that takes a piece
     * @param pos position of the knight
     * @return a list of places the knight will move to take a piece, list will be empty if there are none
     */
    private ArrayList<Coordinates> getKnightMoves(Coordinates pos)
    {
        ArrayList<Coordinates> moves = new ArrayList<>();
        //Generate all 8 moves of the knight
        moves.add(new Coordinates(pos.row()+1, pos.col()-2));
        moves.add(new Coordinates(pos.row()+1, pos.col()+2));
        moves.add(new Coordinates(pos.row()+2, pos.col()-1));
        moves.add(new Coordinates(pos.row()+2, pos.col()+1));
        moves.add(new Coordinates(pos.row()-2, pos.col()-1));
        moves.add(new Coordinates(pos.row()-2, pos.col()+1));
        moves.add(new Coordinates(pos.row()-1, pos.col()-2));
        moves.add(new Coordinates(pos.row()-1, pos.col()+2));

        //Filter the moves out
        ArrayList<Coordinates> finalMoves = new ArrayList<>();
        for (Coordinates move: moves) {
            //Check if the moves are inside the board and if it takes a piece
            if(move.row() >= 0 && move.row() < BOARD_ROWS &&
               move.col() >= 0 && move.col() < BOARD_COLS &&
               getPiece(move) != EMPTY) {
                finalMoves.add(move);
            }
        }
        return finalMoves;
    }
    /**
     * Gets all the bishop moves that takes a piece
     * @param pos position of the bishop
     * @return a list of places the bishop will move to take a piece, list will be empty if there are none
     */
    private ArrayList<Coordinates> getBishopMoves(Coordinates pos)
    {
        ArrayList<Coordinates> moves = new ArrayList<>();
        //booleans for whether to check a diagonal
        boolean upleft = true;
        boolean upright = true;
        boolean botleft = true;
        boolean botright = true;
        //Scanning the diagonals until they reach a border or piece
        for (int i = 1; upleft || upright || botleft || botright; i++) {
            Coordinates tempmove;
            if(upleft)
            {
                tempmove = new Coordinates(pos.row()-i, pos.col()-i);
                //Hitting out of the board, so it will stop checking upper left
                if(tempmove.row() < 0 || tempmove.col() < 0) {
                    upleft = false;
                }
                //If there is a piece hit then add that move to list of moves and stop checking
                else if (getPiece(tempmove) != EMPTY) {
                    moves.add(tempmove);
                    upleft = false;
                }
            }
            if(upright)
            {
                tempmove = new Coordinates(pos.row()-i, pos.col()+i);
                //Hitting out of the board, so it will stop checking upper right
                if(tempmove.row() < 0 || tempmove.col() > BOARD_COLS-1) {
                    upright = false;
                }
                //If there is a piece hit then add that move to list of moves and stop checking
                else if (getPiece(tempmove) != EMPTY) {
                    moves.add(tempmove);
                    upright = false;
                }
            }
            if(botleft)
            {
                tempmove = new Coordinates(pos.row()+i, pos.col()-i);
                //Hitting out of the board, so it will stop checking bottom left
                if(tempmove.row() > BOARD_ROWS-1 || tempmove.col() < 0) {
                    botleft = false;
                }
                //If there is a piece hit then add that move to list of moves and stop checking
                else if (getPiece(tempmove) != EMPTY) {
                    moves.add(tempmove);
                    botleft = false;
                }
            }
            if(botright)
            {
                tempmove = new Coordinates(pos.row()+i, pos.col()+i);
                //Hitting out of the board, so it will stop checking upper right
                if(tempmove.row() > BOARD_ROWS-1 || tempmove.col() > BOARD_COLS-1) {
                    botright = false;
                }
                //If there is a piece hit then add that move to list of moves and stop checking
                else if (getPiece(tempmove) != EMPTY) {
                    moves.add(tempmove);
                    botright = false;
                }
            }
        }
        return moves;
    }
    /**
     * Gets all the rook moves that takes a piece
     * @param pos position of the rook
     * @return a list of places the rook will move to take a piece, list will be empty if there are none
     */
    private ArrayList<Coordinates> getRookMoves(Coordinates pos)
    {
        ArrayList<Coordinates> moves = new ArrayList<>();
        //booleans for whether to check a direction similar to bishop
        boolean up = true;
        boolean right = true;
        boolean left = true;
        boolean down = true;
        //Scanning the directions until they reach a border or piece 
        for (int i = 1; up || right || left || down; i++) {
            Coordinates tempmove;
            if(up)
            {
                tempmove = new Coordinates(pos.row()-i, pos.col());
                //Hitting out of the board, so it will stop checking upward
                if(tempmove.row() < 0) {
                    up = false;
                }
                //If there is a piece hit then add that move to list of moves and stop checking
                else if (getPiece(tempmove) != EMPTY) {
                    moves.add(tempmove);
                    up = false;
                }
            }
            if(right)
            {
                tempmove = new Coordinates(pos.row(), pos.col()+i);
                //Hitting out of the board, so it will stop checking right
                if(tempmove.col() > BOARD_COLS-1) {
                    right = false;
                }
                //If there is a piece hit then add that move to list of moves and stop checking
                else if (getPiece(tempmove) != EMPTY) {
                    moves.add(tempmove);
                    right = false;
                }
            }
            if(left)
            {
                tempmove = new Coordinates(pos.row(), pos.col()-i);
                //Hitting out of the board, so it will stop checking left
                if(tempmove.col() < 0) {
                    left = false;
                }
                //If there is a piece hit then add that move to list of moves and stop checking
                else if (getPiece(tempmove) != EMPTY) {
                    moves.add(tempmove);
                    left = false;
                }
            }
            if(down)
            {
                tempmove = new Coordinates(pos.row()+i, pos.col());
                //Hitting out of the board, so it will stop checking downwards
                if(tempmove.row() > BOARD_ROWS-1) {
                    down = false;
                }
                //If there is a piece hit then add that move to list of moves and stop checking
                else if (getPiece(tempmove) != EMPTY) {
                    moves.add(tempmove);
                    down = false;
                }
            }
        }
        return moves;
    }
    /**
     * Gets all the queen moves that takes a piece
     * @param pos position of the queen
     * @return a list of places the queen will move to take a piece, list will be empty if there are none
     */
    private ArrayList<Coordinates> getQueenMoves(Coordinates pos)
    {
        //Just combines the moves of bishops and rooks
        ArrayList<Coordinates> moves = getBishopMoves(pos);
        moves.addAll(getRookMoves(pos));
        return moves;
    }
    /**
     * Gets all the king moves that takes a piece
     * @param pos position of the king
     * @return a list of places the king will move to take a piece, list will be empty if there are none
     */
    private ArrayList<Coordinates> getKingMoves(Coordinates pos)
    {
        ArrayList<Coordinates> moves = new ArrayList<>();
        //Generate the 8 moves next to the king
        moves.add(new Coordinates(pos.row()+1, pos.col()-1));
        moves.add(new Coordinates(pos.row()+1, pos.col()));
        moves.add(new Coordinates(pos.row()+1, pos.col()+1));
        moves.add(new Coordinates(pos.row(), pos.col()+1));
        moves.add(new Coordinates(pos.row(), pos.col()-1));
        moves.add(new Coordinates(pos.row()-1, pos.col()-1));
        moves.add(new Coordinates(pos.row()-1, pos.col()));
        moves.add(new Coordinates(pos.row()-1, pos.col()+1));

        //Filter the moves out
        ArrayList<Coordinates> finalMoves = new ArrayList<>();
        for (Coordinates move: moves) {
            //Check if the moves are inside the board and if it takes a piece
            if(move.row() >= 0 && move.row() < BOARD_ROWS &&
                    move.col() >= 0 && move.col() < BOARD_COLS &&
                    getPiece(move) != EMPTY) {
                finalMoves.add(move);
            }
        }
        return finalMoves;
    }
    @Override
    public boolean isSolution() {
        return piecesRemaining == 1;
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors = new LinkedList<>();
        //Loop through every piece
        for (Coordinates pieceLoc: pieceLocations) {
            //For each piece get its valid moves
            ArrayList<Coordinates> validMoves = getPieceMoves(getPiece(pieceLoc), pieceLoc);
            //Then create the configurations
            for (Coordinates validMove: validMoves) {
                neighbors.add(new ChessConfig(this, pieceLoc, validMove));
            }
        }
        return neighbors;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof ChessConfig oth)
            return Arrays.deepEquals(this.board, oth.board);
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for(int i = 0; i < BOARD_ROWS; i++)
        {
            output.append("\n");
            for (int j = 0; j < BOARD_COLS; j++) {
                output.append(board[i][j]).append(" ");
            }
        }
        return output.toString();
    }
    /** Makes a string representation of the board*/
    public String boardString()
    {
        StringBuilder output = new StringBuilder();
        output.append("   ");
        for (int i = 0; i < BOARD_COLS; i++) {
            output.append(i).append(" ");
        }
        output.append("\n  ");
        for (int i = 0; i < BOARD_COLS; i++) {
            output.append("--");
        }
        output.append("\n");

        for(int i = 0; i < BOARD_ROWS; i++)
        {
            output.append(i).append("|").append(" ");
            for (int j = 0; j < BOARD_COLS; j++) {
                output.append(board[i][j]).append(" ");
            }
            output.append("\n");
        }
        return output.toString();
    }
    /**Checks if the given position is within the board and is a piece*/
    public boolean isValidSelection(int row, int col)
    {
        return row >= 0 && row < BOARD_ROWS &&
               col >= 0 && col < BOARD_COLS &&
               board[row][col] != EMPTY;
    }
    /**Checks if there are any captures available*/
    public boolean anyValidCaptures()
    {
        if(isSolution())
            return false;
        for (Coordinates pieceLoc: pieceLocations) {
            //For each piece get its valid moves
            ArrayList<Coordinates> validMoves = getPieceMoves(getPiece(pieceLoc), pieceLoc);
            //Then, if there is any move, return true
            if(validMoves.size() > 0)
                return true;
        }
        return false;
    }
    /**Getter for rows*/
    public int getBoardRows()
    {
        return BOARD_ROWS;
    }
    /**Getter for columns*/
    public int getBoardCols()
    {
        return BOARD_COLS;
    }
}
