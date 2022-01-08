/*
 * Copyright (C) 2022
 */
package os.paging.simulator;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.io.File.separator;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class OSTest {

    public static void main(String[] args) {
        OS windows = new OS();
        List<Process> storage = new ArrayList<>();

        //Read in all the processes and stores them in a storage table and ordered by arrival time (Smallest -> Largest order)
        try {
            FileReader reader = new FileReader(System.getProperty("user.dir") + separator + "processes4.txt");
            Scanner in = new Scanner(reader);

            while (in.hasNextLine()) {
                Process newProc = new Process(in.nextLine());
                storage.add(newProc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //Print all results to another file
            FileWriter write = new FileWriter(System.getProperty("user.dir") + separator + "results.txt");
            PrintWriter out = new PrintWriter(write);

            int global_Time = 0;

            //Boolean switches to determine if change occured
            boolean ready, terminated;
            ready = terminated = false;

            //Loop as long as there are more processes
            while (storage.size() > 0 || windows.getProcessTable().size() > 0 || !windows.isCPUAvailable()) {

                // System.out.println(windows.printWaitingQ());
                // if(global_Time == 2000) break;
                //Is it this processes time to arrive?
                for (int i = 0; i < storage.size(); i++) {

                    if ((storage.get(i).getArrivalTime() == global_Time) && (windows.getProcessTable().size() < 99)) {

                        boolean added = windows.add(storage.get(i));

                        if (!added) {
                            //Grab the process and store it in the Memory Waiting Queue
                            Process temp = storage.get(i);
                            temp.incrArrival(global_Time);
                            temp.setState('w');
                            storage.set(i, temp);
                            // out.println("Process not added. Waiting for Memory...");
                        } else {
                            ready = true;
                            storage.remove(i);
                            if (windows.getProcessTable().size() > 1) {
                                int idx = windows.getProcessTable().size() - 2;
                                Process process = windows.getProcessTable().get(idx);
                                
                                idx = windows.getProcessTable().size() - 1;
                                
                                Process next = windows.getProcessTable().get(idx);
                                process.setNextPCB(next);
                            }

                        }
                    } else if (storage.get(i).getArrivalTime() <= global_Time) {
                        Process temp = storage.get(i);
                        //Set the process to arrive at the next cycle if its original arrival was passed
                        temp.incrArrival(global_Time);
                        storage.set(i, temp);

                    }
                }

                //Check if the CPU is free
                if (windows.isCPUAvailable()) {
                    //Timestamp the start of a new process
                    // out.println("GLOBAL TIME: " + global_Time);
                    // out.println("CPU TIME: " + windows.clock + "\n");
                    //If so, start new execution
                    windows.setClock(0);
                    //Grab next process from Proc Table
                    if (windows.getProcessTable().size() > 0) {
                        //DEQUEUE will grab the next ready process in the Process Table
                        windows.setCPU(windows.getProcessTable().deQueueReadyProc());
                        if (windows.getCPU() == null) {
                            windows.allocateAvailableResources();
                            continue;
                        }
                        windows.getCPU().setState('e');
                        windows.setCPUAvailable(false);
                        out.println("STARTING WORK ON: \n\n" + windows.getCPU());
                        out.println();
                    }
                } else {
                    /*
                     * If the CPU has a process already, continue processing. Increments the clock
                     * and randomizes the values and prints out the contents of the CPU by returning
                     * a string
                     */

                    //Compute the contents every cycle
                    if (windows.getCPU() != null) {
                        String temp = windows.compute();
                        //out.println(temp);					
                    }
                }

                //Check if the process has finished. If so, terminate and free the CPU
                if (windows.getCPU() != null && windows.getClock() >= windows.getCPU().getBurstTime()) {

                    out.println("Global Time: " + global_Time);
                    out.println("CPU Clock: " + windows.getClock());

                    out.println("BURST REACHED...now terminating...\n");

                    //Change its state to terminated
                    windows.getCPU().setState('t');
                    windows.getCPU().setID(-1);
                    out.println(windows.getCPU() + "\n");

                    //Add it to the Terminated Queue
                    Process endProc = windows.deallocate(windows.getCPU());
                    out.println(windows.printPartitionsList());
                    windows.getTerminatedQ().add(endProc);
                    windows.setCPUAvailable(true);
                    windows.setCPU(null);

                    //Terminated queue has changed checker
                    terminated = true;
                }

                //If there was a change in the readyQ, print its contents
                // if(ready) {
                // 	out.println(windows.printReadyQ());
                // 	ready = false;
                // }
                //If there was a change in the terminatedQ, print its contents
                // if(terminated) {
                // 	out.println("\nCONTENTS OF TERMINATEDQ :");
                // 	out.println(windows.terminated_Q);
                // 	terminated = false;
                // 	out.println();
                // }
                global_Time++;
            }
            out.println(windows.printTerminatedQ());
            out.println("\nStorage: " + storage.size());
            out.println("\nProcTable: " + windows.getProcessTable().size());
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
