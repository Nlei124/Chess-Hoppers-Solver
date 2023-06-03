package puzzles.strings;

import puzzles.common.solver.Configuration;

import java.util.Collection;
import java.util.LinkedList;

public class StringsConfig implements Configuration {
    //Variables for the range we want the string in (65-90 is uppercase A-Z)
    private final static int ASCII_START = 65;
    private final static int ASCII_END = 90;

    //The ending word
    private static String END_WORD;
    //The current word
    private String word;
    //Constructor used once to create the starting clock config and sets the max and end hours
    public StringsConfig(String current, String end)
    {
        word = current;
        END_WORD = end;
    }
    //Constructor used for creating neighbors
    public StringsConfig(String current) {
        word = current;
    }
    /**
     * Creates a neighboring string by shifting a specified letter a specified amount of time
     * @param letterPos pos of which letter to change
     * @param posChange how many positions to change the letter
     * @return the new changed string
     */
    public String createNeighbor(int letterPos, int posChange)
    {
        //65 = A
        //90 = Z
        //I made it to work for any given ascii range
        //mods the change for times it wraps around more than once
        posChange %= ASCII_END-ASCII_START + 1;
        //changes the ascii value
        int changedAscii = (word.charAt(letterPos)) + posChange;

        //If the change passes the end(Z) then it will wrap around
        if (changedAscii > ASCII_END)
        {
            //the -1 is to account for the step it takes to change from the end to the start
            changedAscii = ASCII_START + (posChange - 1);
        }
        //This else if is never reached due to the % I do at the beginning, but I'm keeping it here anyway
        //if it is the negative wrap around
        else if(changedAscii < ASCII_START)
        {
            //the +1 is to account for the step it takes to change from the start to the end
            changedAscii = ASCII_END - (posChange + 1);
        }
        return word.substring(0, letterPos) + (char)changedAscii + word.substring(letterPos + 1);
    }

    @Override
    public boolean isSolution() {
        return word.equals(END_WORD);
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        Collection<Configuration> neighbors = new LinkedList<>();
        //Finds the first letter difference and then uses the overloaded constructor to make the neighbors
        int diffLetter = 0;
        for(int i = 0; i < END_WORD.length(); i++)
        {
            if(word.charAt(i) != END_WORD.charAt(i))
            {
                diffLetter = i;
                break;
            }
        }
        //generating neighbor ahead
        neighbors.add(new StringsConfig(createNeighbor(diffLetter,1)));
        //generating neighbor behind
        neighbors.add(new StringsConfig(createNeighbor(diffLetter,-1)));
        return neighbors;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof StringsConfig oth)
        {
            return this.word.equals((oth).word);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public String toString() {
        return word;
    }
}
