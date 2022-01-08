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
public class OS {

    private final List<Process> terminatedQ = new ArrayList<>();
    private final List<Partition> freePartitions = new ArrayList<>();
    private final ProcessTable processTable = new ProcessTable();
    private int clock;
    private Process CPU;
    private boolean CPUAvailable = true;

    public OS() {
        //init free_partitions list with 1 partition of size N
        //Size of memory is in bytes.
        freePartitions.add(new Partition(0, 1000000));
    }

    private void adjustPartitionPointers() {
        for (int i = 0; i < freePartitions.size(); i++) {
            if (i == freePartitions.size() - 1) {
                return;
            }
            freePartitions.get(i).setNext(freePartitions.get(i + 1));
        }
    }

    private void createFreePartition(int start_loc, int size) {
        Partition new_fp = new Partition(start_loc, size);

        //Binary search input on start loc
        int pos = binarySearch(freePartitions, new_fp);
        if (pos < 0) {
            freePartitions.add(-pos - 1, new_fp);
        } else {
            freePartitions.add(pos, new_fp);
        }

        if (freePartitions.size() > 1) {
            fpListMerge();
        }
        adjustPartitionPointers();
    }

    private void fpListMerge() {
        for (int i = 0; i < freePartitions.size(); i++) {
            //look ahead to next partition for if it exists
            if (freePartitions.size() >= 2) {
                if (i + 1 == freePartitions.size()) {
                    return;
                }
                if (freePartitions.get(i + 1) == null) {
                    return;
                }
                //Check if they are next to each other
                Partition left = freePartitions.get(i);
                Partition right = freePartitions.get(i + 1);

                if ((left.getStartLoc() + left.getSize()) == right.getStartLoc()) {
                    left.setSize(left.getSize() + right.getSize());
                    freePartitions.remove(right);
                    freePartitions.set(i, left);
                    // System.out.println("MERGE MADE");
                    fpListMerge();
                }

                adjustPartitionPointers();
            }
        }
    }

    public Process deallocate(Process process) {
        createFreePartition(process.getStartMemLoc(), process.getProcSize());
        process.setStartMemLoc(-1);

        allocateAvailableResources();
        adjustPartitionPointers();
        return process;
    }

    public void allocateAvailableResources() {
        for (int i = 0; i < processTable.size(); i++) {
            if (processTable.get(i).getState() == 'w') {
                allocate(processTable.get(i));
            }
        }
    }

    private boolean allocate(Process process) {
        int fpPos = -1;

        // System.out.println("Proc Mem + Size: " + process.getStartMemLoc() + " " + process.getProcSize());
        for (int i = 0; i < freePartitions.size(); i++) {
            if (freePartitions.get(i).getSize() >= process.getProcSize()) {
                fpPos = i;
            }
        }

        if (fpPos < 0) {
            process.setState('w');
            // processTable.add(process);
            return false;
        } else {
            process.setState('r');
            process.setStartMemLoc(freePartitions.get(fpPos).getStartLoc());
            processTable.add(process);

            int new_start = freePartitions.get(fpPos).getStartLoc() + process.getProcSize();
            int new_size = freePartitions.get(fpPos).getSize() - process.getProcSize();

            freePartitions.remove(fpPos);

            if (new_size != 0) {
                createFreePartition(new_start, new_size);
            }
            adjustPartitionPointers();

            return true;
        }
    }

    public boolean add(Process process) {
        // System.out.println("No available memory to allocate...Process pushed to WaitQ");
        return allocate(process);
    }

    public String compute() {
        clock++;
        CPUAvailable = false;

        CPU.randomizeCPU();

        return "\t\tCONTENTS OF CPU: " + "\n" + CPU + "\n";
    }

    public String printReadyQ() {
        String temp = "CONTENTS OF THE READYQ:\n";
        int i = 0;
        boolean found = false;
        while (!found && i < processTable.size()) {
            if (processTable.get(i).getState() == 'r') {
                found = true;
                Process ready = processTable.get(i);
                temp += ready + "\n";
                while (ready.getNextPCB() != null) {
                    ready = ready.getNextPCB();
                    temp += ready + "\n";
                }
            } else {
                i++;
            }
        }
        return temp;
    }

    public String printWaitingQ() {
        String temp = "CONTENTS OF THE WAITINGQ:\n";
        for (int i = 0; i < processTable.size(); i++) {
            if (processTable.get(i).getState() == 'w') {
                temp += processTable.get(i) + "\n\n";
            }
        }
        return temp;
    }

    public String printTerminatedQ() {
        String temp = "CONTENTS OF THE TERMINATEDQ (size: " + terminatedQ.size() + " ):\n";
        for (Process process : terminatedQ) {
            temp += process + "\n\n";
        }
        return temp;
    }

    public String printPartitionsList() {
        String temp = "CONTENTS OF FREE PARTITIONS LIST Size(" + freePartitions.size() + "):\n";
        for (Partition partition : freePartitions) {
            temp += partition + "\n";
        }
        return temp;
    }

    //Binary Search for creating ordered list of processes in storage
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

    /**
     * @return the clock
     */
    public int getClock() {
        return clock;
    }

    /**
     * @param clock the clock to set
     */
    public void setClock(int clock) {
        this.clock = clock;
    }

    /**
     * @return the CPU
     */
    public Process getCPU() {
        return CPU;
    }

    /**
     * @param CPU the CPU to set
     */
    public void setCPU(Process CPU) {
        this.CPU = CPU;
    }

    /**
     * @return the CPUAvailable
     */
    public boolean isCPUAvailable() {
        return CPUAvailable;
    }

    /**
     * @param CPUAvailable the CPUAvailable to set
     */
    public void setCPUAvailable(boolean CPUAvailable) {
        this.CPUAvailable = CPUAvailable;
    }

    /**
     * @return the processTable
     */
    public ProcessTable getProcessTable() {
        return processTable;
    }

    /**
     * @return the terminatedQ
     */
    public List<Process> getTerminatedQ() {
        return terminatedQ;
    }

}
