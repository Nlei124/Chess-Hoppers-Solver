package puzzles.clock;

import puzzles.common.solver.Configuration;

import java.util.Collection;
import java.util.LinkedList;

public class ClockConfig implements Configuration {
    //The minimum hour (1)
    private final static int MIN_HOUR = 1;
    //The max hour specified by input
    private static int MAX_HOUR;
    //The solution hour
    private static int END_HOUR;
    //The hour held by this config
    private int hour;

    //Constructor used once to create the starting clock config and sets the max and end hours
    public ClockConfig(int numHours, int current, int end)
    {
        MAX_HOUR = numHours;
        this.hour = current;
        END_HOUR = end;
    }
    //Overloaded constructor used for creating neighbors, just setting its current hour
    public ClockConfig(int current)
    {
        hour = current;
    }
    @Override
    public boolean isSolution() {
        return hour == END_HOUR;
    }

    @Override
    public Collection<Configuration> getNeighbors() {
        //Generates two neighbors in this order(+1, -1)
        Collection<Configuration> neighbors = new LinkedList<>();
        int neighHour = hour + 1;
        //Generating the neighbor an hour ahead but wrapping around the clock if it is past the max
        if(neighHour > MAX_HOUR) {
            neighHour = MIN_HOUR;
        }
        neighbors.add(new ClockConfig(neighHour));
        //Generating the neighbor an hour behind and doing the same wrap around
        neighHour = hour - 1;
        if(neighHour < MIN_HOUR) {
            neighHour = MAX_HOUR;
        }
        neighbors.add(new ClockConfig(neighHour));
        return neighbors;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof ClockConfig oth)
            return this.hour == oth.hour;
        return false;
    }

    @Override
    public int hashCode() {
        return hour;
    }

    @Override
    public String toString() {
        return Integer.toString(hour);
    }
}

