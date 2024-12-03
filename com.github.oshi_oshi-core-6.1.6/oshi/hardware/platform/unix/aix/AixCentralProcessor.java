/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_cpu_t
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_cpu_total_t
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_partition_config_t
 */
package oshi.hardware.platform.unix.aix;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.Lssrad;
import oshi.driver.unix.aix.perfstat.PerfstatConfig;
import oshi.driver.unix.aix.perfstat.PerfstatCpu;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
final class AixCentralProcessor
extends AbstractCentralProcessor {
    private final Supplier<Perfstat.perfstat_cpu_total_t> cpuTotal = Memoizer.memoize(PerfstatCpu::queryCpuTotal, Memoizer.defaultExpiration());
    private final Supplier<Perfstat.perfstat_cpu_t[]> cpuProc = Memoizer.memoize(PerfstatCpu::queryCpu, Memoizer.defaultExpiration());
    private static final int SBITS = AixCentralProcessor.querySbits();
    private Perfstat.perfstat_partition_config_t config;
    private static final long USER_HZ = ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("getconf CLK_TCK"), 100L);

    AixCentralProcessor() {
    }

    @Override
    protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
        String cpuVendor = "unknown";
        String cpuName = "";
        String cpuFamily = "";
        boolean cpu64bit = false;
        String nameMarker = "Processor Type:";
        String familyMarker = "Processor Version:";
        String bitnessMarker = "CPU Type:";
        for (String checkLine : ExecutingCommand.runNative("prtconf")) {
            if (checkLine.startsWith("Processor Type:")) {
                cpuName = checkLine.split("Processor Type:")[1].trim();
                if (cpuName.startsWith("P")) {
                    cpuVendor = "IBM";
                    continue;
                }
                if (!cpuName.startsWith("I")) continue;
                cpuVendor = "Intel";
                continue;
            }
            if (checkLine.startsWith("Processor Version:")) {
                cpuFamily = checkLine.split("Processor Version:")[1].trim();
                continue;
            }
            if (!checkLine.startsWith("CPU Type:")) continue;
            cpu64bit = checkLine.split("CPU Type:")[1].contains("64");
        }
        String cpuModel = "";
        String cpuStepping = "";
        String machineId = Native.toString((byte[])this.config.machineID);
        if (machineId.isEmpty()) {
            machineId = ExecutingCommand.getFirstAnswer("uname -m");
        }
        if (machineId.length() > 10) {
            int m = machineId.length() - 4;
            int s = machineId.length() - 2;
            cpuModel = machineId.substring(m, s);
            cpuStepping = machineId.substring(s);
        }
        return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, machineId, cpu64bit, (long)(this.config.processorMHz * 1000000.0));
    }

    @Override
    protected Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> initProcessorCounts() {
        int lcpus;
        this.config = PerfstatConfig.queryConfig();
        int physProcs = (int)this.config.numProcessors.max;
        if (physProcs < 1) {
            physProcs = 1;
        }
        if ((lcpus = this.config.lcpus) < 1) {
            lcpus = 1;
        }
        Map<Integer, Pair<Integer, Integer>> nodePkgMap = Lssrad.queryNodesPackages();
        ArrayList<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<CentralProcessor.LogicalProcessor>();
        for (int proc = 0; proc < lcpus; ++proc) {
            Pair<Integer, Integer> nodePkg = nodePkgMap.get(proc);
            logProcs.add(new CentralProcessor.LogicalProcessor(proc, proc / physProcs, nodePkg == null ? 0 : nodePkg.getB(), nodePkg == null ? 0 : nodePkg.getA()));
        }
        return new Pair(logProcs, null);
    }

    @Override
    public long[] querySystemCpuLoadTicks() {
        Perfstat.perfstat_cpu_total_t perfstat = this.cpuTotal.get();
        long[] ticks = new long[CentralProcessor.TickType.values().length];
        ticks[CentralProcessor.TickType.USER.ordinal()] = perfstat.user * 1000L / USER_HZ;
        ticks[CentralProcessor.TickType.SYSTEM.ordinal()] = perfstat.sys * 1000L / USER_HZ;
        ticks[CentralProcessor.TickType.IDLE.ordinal()] = perfstat.idle * 1000L / USER_HZ;
        ticks[CentralProcessor.TickType.IOWAIT.ordinal()] = perfstat.wait * 1000L / USER_HZ;
        ticks[CentralProcessor.TickType.IRQ.ordinal()] = perfstat.devintrs * 1000L / USER_HZ;
        ticks[CentralProcessor.TickType.SOFTIRQ.ordinal()] = perfstat.softintrs * 1000L / USER_HZ;
        ticks[CentralProcessor.TickType.STEAL.ordinal()] = (perfstat.idle_stolen_purr + perfstat.busy_stolen_purr) * 1000L / USER_HZ;
        return ticks;
    }

    @Override
    public long[] queryCurrentFreq() {
        long[] freqs = new long[this.getLogicalProcessorCount()];
        Arrays.fill(freqs, -1L);
        String freqMarker = "runs at";
        int idx = 0;
        for (String checkLine : ExecutingCommand.runNative("pmcycles -m")) {
            if (!checkLine.contains(freqMarker)) continue;
            freqs[idx++] = ParseUtil.parseHertz(checkLine.split(freqMarker)[1].trim());
            if (idx < freqs.length) continue;
            break;
        }
        return freqs;
    }

    @Override
    protected long queryMaxFreq() {
        Perfstat.perfstat_cpu_total_t perfstat = this.cpuTotal.get();
        return perfstat.processorHZ;
    }

    @Override
    public double[] getSystemLoadAverage(int nelem) {
        if (nelem < 1 || nelem > 3) {
            throw new IllegalArgumentException("Must include from one to three elements.");
        }
        double[] average = new double[nelem];
        long[] loadavg = this.cpuTotal.get().loadavg;
        for (int i = 0; i < nelem; ++i) {
            average[i] = (double)loadavg[i] / (double)(1L << SBITS);
        }
        return average;
    }

    @Override
    public long[][] queryProcessorCpuLoadTicks() {
        Perfstat.perfstat_cpu_t[] cpu = this.cpuProc.get();
        long[][] ticks = new long[cpu.length][CentralProcessor.TickType.values().length];
        for (int i = 0; i < cpu.length; ++i) {
            ticks[i] = new long[CentralProcessor.TickType.values().length];
            ticks[i][CentralProcessor.TickType.USER.ordinal()] = cpu[i].user * 1000L / USER_HZ;
            ticks[i][CentralProcessor.TickType.SYSTEM.ordinal()] = cpu[i].sys * 1000L / USER_HZ;
            ticks[i][CentralProcessor.TickType.IDLE.ordinal()] = cpu[i].idle * 1000L / USER_HZ;
            ticks[i][CentralProcessor.TickType.IOWAIT.ordinal()] = cpu[i].wait * 1000L / USER_HZ;
            ticks[i][CentralProcessor.TickType.IRQ.ordinal()] = cpu[i].devintrs * 1000L / USER_HZ;
            ticks[i][CentralProcessor.TickType.SOFTIRQ.ordinal()] = cpu[i].softintrs * 1000L / USER_HZ;
            ticks[i][CentralProcessor.TickType.STEAL.ordinal()] = (cpu[i].idle_stolen_purr + cpu[i].busy_stolen_purr) * 1000L / USER_HZ;
        }
        return ticks;
    }

    @Override
    public long queryContextSwitches() {
        return this.cpuTotal.get().pswitch;
    }

    @Override
    public long queryInterrupts() {
        Perfstat.perfstat_cpu_total_t cpu = this.cpuTotal.get();
        return cpu.devintrs + cpu.softintrs;
    }

    private static int querySbits() {
        for (String s : FileUtil.readFile("/usr/include/sys/proc.h")) {
            if (!s.contains("SBITS") || !s.contains("#define")) continue;
            return ParseUtil.parseLastInt(s, 16);
        }
        return 16;
    }
}

