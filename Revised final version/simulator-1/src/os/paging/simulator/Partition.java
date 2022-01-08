/*
 * Copyright (C) 2022
 */
package os.paging.simulator;

/**
 *
 * @author Hiram K. <https://github.com/IdelsTak>
 */
public class Partition implements Comparable<Partition> {

    private int startLoc;
    private int size;
    private Partition next;

    public Partition() {
        startLoc = 0;
        size = 0;
        next = null;
    }

    public Partition(int start, int size) {
        startLoc = start;
        size = size;
        next = null;
    }

    @Override
    public int compareTo(Partition otherPartition) {
        return this.startLoc - otherPartition.startLoc;
    }

    @Override
    public String toString() {
        return String.format("\t\tPartition: Starts@: %-4d, with size: %-4d", startLoc, size);
    }

    /**
     * @return the startLoc
     */
    public int getStartLoc() {
        return startLoc;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * @param next the next to set
     */
    public void setNext(Partition next) {
        this.next = next;
    }

}
