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
public class Process implements Comparable<Process> {

    private int id;
    private String name;
    private int[] props;
    private char state;
    private Process nextPCB;
    private RegisterSet cpu;

    public Process() {
        name = "";
        state = "".charAt(0);
        id = 0;
        cpu = new RegisterSet();
        props = new int[5];
        nextPCB = null;
    }

    public Process(int value, String process) {
        id = value;
        props = new int[5];
        nextPCB = null;
        List<String> coreValues = new ArrayList<>();
        List<String> registerValues = new ArrayList<>();

        try {
            String[] x = process.split(" ");
            name = x[0];
            state = 'n';

            for (int i = 1; i <= 4; i++) {
                props[i - 1] = Integer.parseInt(x[i]);
            }
            for (int i = 5; i <= 13; i++) {
                coreValues.add(x[i]);
            }
            for (int i = 14; i < x.length; i++) {
                registerValues.add(x[i]);
            }

            cpu = new RegisterSet(coreValues, registerValues);

            // System.out.println(this);
        } catch (Exception e) {
            System.out.println("Error creating the process...");
            e.printStackTrace();
        }
    }

    public Process(String process) {
        id = -1;
        props = new int[5];
        nextPCB = null;
        List<String> coreValues = new ArrayList<>();
        List<String> registerValues = new ArrayList<>();

        try {
            String[] x = process.split(" ");
            name = x[0];
            state = 'n';

            for (int i = 1; i <= 4; i++) {
                props[i - 1] = Integer.parseInt(x[i]);
            }
            for (int i = 5; i <= 13; i++) {
                coreValues.add(x[i]);
            }
            for (int i = 14; i < x.length; i++) {
                registerValues.add(x[i]);
            }

            cpu = new RegisterSet(coreValues, registerValues);

            // System.out.println(this);
        } catch (Exception e) {
            System.out.println("Error creating the process...");
            e.printStackTrace();
        }
    }

    public boolean randomizeCPU() {
        return cpu.randomize();
    }

    public int getArrivalTime() {
        return props[1];
    }

    public boolean incrArrival(int n) {
        props[1] = n + 1;
        return true;
    }

    public int getBurstTime() {
        return props[2];
    }

    public int getStartMemLoc() {
        return props[4];
    }

    public int getProcSize() {
        return props[3];
    }

    public void setID(int value) {
        id = value;
    }

    public void setStartMemLoc(int n) {
        props[4] = n;
    }

    @Override
    public int compareTo(Process otherProcess) {
        int a = this.props[1];
        int b = otherProcess.props[1];

        int c = this.props[2];
        int d = otherProcess.props[2];

        return (a - b) + (c - d);
    }

    @Override
    public String toString() {
        String temp = String.format("\t\tID: %d Name: %-10s State: %-3c Priority: %-3d Arrival Time: %-3d CPU Burst Time: %-4d Size: %-6d Mem_Start_Location: %-6d", id, name, state, props[0], props[1], props[2], props[3], props[4]);
        temp += "\n\t" + cpu.toString();
        return temp;
    }

    /**
     * @return the state
     */
    public char getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(char state) {
        this.state = state;
    }

    /**
     * @return the nextPCB
     */
    public Process getNextPCB() {
        return nextPCB;
    }

    /**
     * @param nextPCB the nextPCB to set
     */
    public void setNextPCB(Process nextPCB) {
        this.nextPCB = nextPCB;
    }

}
