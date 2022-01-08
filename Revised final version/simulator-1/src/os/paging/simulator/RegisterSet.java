package os.paging.simulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Core Register Positions: ----------------------------------------------- | XAR | XDI | XDO | PC |
 * IR | EMIT | RR | PSW | -----------------------------------------------
 */
public class RegisterSet {

    private final List<String> cores = new ArrayList<>();
    private final List<String> registers = new ArrayList<>();

    private String XAR;
    private String XDI;
    private String XDO;
    private String PC;
    private String IR;
    private String EMIT;
    private String RR;
    private String PSW;

    public RegisterSet() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public RegisterSet(Collection<String> cores, Collection<String> registers) {
        this.cores.addAll(cores);
        this.registers.addAll(registers);

        this.initRegisters();
    }

    public boolean randomize() {
        //Randomize the cpu core
        //if(CPU == null) return false;

        for (int i = 0; i < cores.size(); i++) {
            Random r = new Random();
            StringBuilder sb = new StringBuilder();
            while (sb.length() < 6) {
                sb.append(Integer.toHexString(r.nextInt()));
            }
            cores.set(i, sb.toString().substring(0, 6));
        }

        //Randomize the registers
        for (int i = 0; i < registers.size(); i++) {
            Random r = new Random();
            StringBuilder sb = new StringBuilder();
            while (sb.length() < 6) {
                sb.append(Integer.toHexString(r.nextInt()));
            }
            registers.set(i, sb.toString().substring(0, 6));
        }

        initRegisters();

        return true;
    }

    private void initRegisters() {
        XAR = cores.get(0);
        XDI = cores.get(1);
        XDO = cores.get(2);
        PC = cores.get(3);
        IR = cores.get(4);
        EMIT = cores.get(5);
        RR = cores.get(6);
        PSW = cores.get(7);
    }

    @Override
    public String toString() {
        String temp = String.format("\tXAR: %-8.6s XDI: %-8.6s XDO: %-8.6s PC: %-8.6s IR: %-8.6s EMIT: %-8.6s RR: %-8.6s PSW: %-4.6s \n\t\tRegister Values: ",
                XAR, XDI, XDO, PC, IR, EMIT, RR, PSW);
        for (String register : registers) {
            temp += register + " ";
        }
        return temp;
    }
}
