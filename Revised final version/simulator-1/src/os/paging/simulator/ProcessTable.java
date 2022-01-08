/*
 * Copyright (C) 2022
 */
package os.paging.simulator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class ProcessTable {

    private final List<Process> processes = new ArrayList<>();
    private int pidCounter = 0;

    public ProcessTable() {
    }

    public boolean add(Process process) {
        if (processes.size() <= 100) {
            process.setID(pidCounter);
            int pos = binarySearch(processes, process);
            if (pos < 0) {
                processes.add(-pos - 1, process);
            } else {
                processes.add(pos, process);
            }
            // processes.add(newProc);
            pidCounter = (pidCounter + 1) % 100;
            // System.out.println("PID_COUNTER: " + pidCounter);
            // System.out.println(processes.size()-1);
        } else {
            // System.out.println("Process Table is Full...Incrementing Arrival Time.");
            return false;
        }
        return true;
    }

    public Process deQueueReadyProc() {
        Process temp = null;

        if (processes.size() > 0) {
            int i = 0;
            boolean found = false;
            while (!found && i < processes.size()) {
                if (processes.get(i).getState() == 'r') {
                    found = true;
                    temp = processes.remove(i);
                } else {
                    i++;
                }
            }
            return temp;
        } else {
            return null;
        }
    }

    public Process get(int index) {
        return processes.get(index);
    }

    public Process remove(int id) {
        Process x = processes.remove(id);
        return x;
    }

    public int size() {
        return processes.size();
    }

    @Override
    public String toString() {
        String temp = "";
        for (Process p : processes) {
            temp += p.toString() + "\n";
        }
        return temp;
    }

    /**
     * Binary Search for creating ordered list of processes in storage
     *
     * @param <T>
     * @param table
     * @param key
     * @return
     */
    public static <T extends Comparable<T>> int binarySearch(List<T> table, T key) {
        int low = 0, high = table.size() - 1;
        while (high >= low) {
            int mid = (low + high) / 2;
            T midElement = table.get(mid);
            int result = key.compareTo(midElement);
            if (result == 0) {
                return mid;
            }
            if (result < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -low - 1;
    }
}
