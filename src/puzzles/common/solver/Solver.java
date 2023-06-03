package puzzles.common.solver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Solver {
    /**
     * Solves using a Breadth first search
     * Prints the number of configurations created and the number of unique configurations
     * @param start the starting configuration
     * @return List of the path from the start to the end(specified in the configuration)
     */
    public static LinkedList<Configuration> BFSSolver(Configuration start)
    {
        //counter for whenever a config is made during this
        int configCount = 0;
        //counter for the unique configs
        int unique = 0;
        //Hash for predecessor map
        HashMap<Configuration, Configuration> predmap = new HashMap<>();
        //Setting up the predecessor map and queue
        predmap.put(start, null);
        Queue<Configuration> vistingQueue = new LinkedList<>();
        vistingQueue.offer(start);

        //Loops through for the queue and checks if the solution is reached
        while(!vistingQueue.isEmpty() && !vistingQueue.peek().isSolution())
        {
            //Process the front of the queue
            Configuration curr = vistingQueue.remove();
            //Process the neighbors of the front
            for(Configuration neighbor : curr.getNeighbors())
            {
                //Add a count to for a generated config
                configCount++;
                //Checks for visitation
                if(!predmap.containsKey(neighbor))
                {
                    //adds a count for an unique generated config
                    unique++;
                    //Then adds the neighbors to predecessor map and queue
                    predmap.put(neighbor, curr);
                    vistingQueue.offer(neighbor);
                }
            }
        }
        //Printing the configuration counts
        System.out.println("Total configs: " + configCount);
        System.out.println("Unique configs: " + unique);
        //No solution
        if(vistingQueue.isEmpty()) {
            return null;
        }
        //Creating the list of the path
        LinkedList<Configuration> path = new LinkedList<>();
        path.add(0, vistingQueue.peek());
        Configuration prev = predmap.get( vistingQueue.peek());
        //Adding the previous until the start is found
        while( prev != null)
        {
            path.add(0, prev);
            prev = predmap.get(prev);
        }
        return path;
    }
    //Helper function that calls the solver and prints the solution using the linkedlist generated
    public static void PrintBFSSolution(Configuration start)
    {
        LinkedList<Configuration> path = BFSSolver(start);
        if(path == null)
        {
            System.out.println("No solution");
            return;
        }
        for(int i = 0; i < path.size(); i ++)
        {
            System.out.printf("Step %d: %s%n", i, path.get(i).toString());
        }
    }

    /**The same solver but without the connfig messages*/
    public static LinkedList<Configuration> ModelBFSSolver(Configuration start)
    {
        //Hash for predecessor map
        HashMap<Configuration, Configuration> predmap = new HashMap<>();
        //Setting up the predecessor map and queue
        predmap.put(start, null);
        Queue<Configuration> vistingQueue = new LinkedList<>();
        vistingQueue.offer(start);

        //Loops through for the queue and checks if the solution is reached
        while(!vistingQueue.isEmpty() && !vistingQueue.peek().isSolution())
        {
            //Process the front of the queue
            Configuration curr = vistingQueue.remove();
            //Process the neighbors of the front
            for(Configuration neighbor : curr.getNeighbors())
            { 
                //Checks for visitation
                if(!predmap.containsKey(neighbor))
                {
                    //Then adds the neighbors to predecessor map and queue
                    predmap.put(neighbor, curr);
                    vistingQueue.offer(neighbor);
                }
            }
        }
        //No solution
        if(vistingQueue.isEmpty()) {
            return null;
        }
        //Creating the list of the path
        LinkedList<Configuration> path = new LinkedList<>();
        path.add(0, vistingQueue.peek());
        Configuration prev = predmap.get( vistingQueue.peek());
        //Adding the previous until the start is found
        while( prev != null)
        {
            path.add(0, prev);
            prev = predmap.get(prev);
        }
        return path;
    }

}
