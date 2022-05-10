import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

public class Program {

    final static int NUM_PROCS = 6; // How many concurrent processes
    final static int TOTAL_RESOURCES = 30; // Total resources in the system
    final static int MAX_PROC_RESOURCES = 13; // Highest amount of resources any process could need
    final static int ITERATIONS = 30; // How long to run the program
    static int totalHeldResources = 0;
    static Random rand = new Random();

    public static void main(String[] args) {
        // The list of processes:
        ArrayList<Proc> processes = new ArrayList<>();
        for (int i = 0; i < NUM_PROCS; i++)
            processes.add(new Proc(MAX_PROC_RESOURCES - rand.nextInt(3))); // Initialize to a new Proc, with some small range for its max
        // Run the simulation:
        for (int i = 0; i < ITERATIONS; i++) {
            // loop through the processes and for each one get its request
            for (int j = 0; j < processes.size(); j++) {
                // Get the request
                int currRequest = processes.get(j).resourceRequest(TOTAL_RESOURCES - totalHeldResources);
                System.out.println("Process " + j + " requested " + currRequest + ", checking...");

                // just ignore processes that don't ask for resources
                if (currRequest == 0)
                    continue;

                if (currRequest < 0) {
                    totalHeldResources += currRequest;
                } else {
                    if (banker(processes, currRequest, j)) {
                        System.out.println("Process " + j + " requested " + currRequest + ", granted.");
                        processes.get(j).addResources(currRequest);
                        totalHeldResources += currRequest;
                    } else {
                        System.out.println("Process " + j + " requested " + currRequest + ", denied.");
                    }
                }

                // At the end of each iteration, give a summary of the current status:
                System.out.println("\n***** STATUS *****");
                System.out.println("Total Available: " + (TOTAL_RESOURCES - totalHeldResources));
                for (int k = 0; k < processes.size(); k++)
                    System.out.println("Process " + k + " holds: " + processes.get(k).getHeldResources() + ", max: " +
                            processes.get(k).getMaxResources() + ", claim: " +
                            (processes.get(k).getMaxResources() - processes.get(k).getHeldResources()));
                System.out.println("***** STATUS *****\n");

            }
        }

    }

    private static boolean banker(ArrayList<Proc> processes, int currRequest, int currentProcessIndex) {
        if (TOTAL_RESOURCES - totalHeldResources == 0) {
            return false;
        }
        boolean found;
        int tempAvailableResources = TOTAL_RESOURCES - totalHeldResources - currRequest;
        ArrayList<Proc> tempList = new ArrayList<>();
        for (Proc process : processes) {
            tempList.add(new Proc(process));
        }
        if ((TOTAL_RESOURCES - totalHeldResources) >= currRequest) {
            tempList.get(currentProcessIndex).addResources(currRequest); //simulates the current request being fulfilled
        }
        int size = tempList.size();
        while (size != 0) {
            found = false;
            for (int i = 0; i < tempList.size(); i++) {
                if (tempList.get(i).getMaxResources() - tempList.get(i).getHeldResources() <= (tempAvailableResources)) {
                    tempAvailableResources += tempList.get(i).getHeldResources();
                    tempList.remove(i);
                    size--;
                    i--;
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

}
