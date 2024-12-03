/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 */
package oshi.hardware.platform.unix.openbsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.unix.OpenBsdLibc;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.openbsd.OpenBsdSysctlUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public class OpenBsdCentralProcessor
extends AbstractCentralProcessor {
    private final Supplier<Pair<Long, Long>> vmStats = Memoizer.memoize(OpenBsdCentralProcessor::queryVmStats, Memoizer.defaultExpiration());
    private static final Pattern DMESG_CPU = Pattern.compile("cpu(\\d+): smt (\\d+), core (\\d+), package (\\d+)");

    @Override
    protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
        String cpuVendor = OpenBsdSysctlUtil.sysctl("machdep.cpuvendor", "");
        int[] mib = new int[]{6, 2};
        String cpuName = OpenBsdSysctlUtil.sysctl(mib, "");
        int cpuid = ParseUtil.hexStringToInt(OpenBsdSysctlUtil.sysctl("machdep.cpuid", ""), 0);
        int cpufeature = ParseUtil.hexStringToInt(OpenBsdSysctlUtil.sysctl("machdep.cpufeature", ""), 0);
        Triplet<Integer, Integer, Integer> cpu = OpenBsdCentralProcessor.cpuidToFamilyModelStepping(cpuid);
        String cpuFamily = cpu.getA().toString();
        String cpuModel = cpu.getB().toString();
        String cpuStepping = cpu.getC().toString();
        long cpuFreq = ParseUtil.parseHertz(cpuName);
        if (cpuFreq < 0L) {
            cpuFreq = this.queryMaxFreq();
        }
        mib[1] = 1;
        String machine = OpenBsdSysctlUtil.sysctl(mib, "");
        boolean cpu64bit = machine != null && machine.contains("64") || ExecutingCommand.getFirstAnswer("uname -m").trim().contains("64");
        String processorID = String.format("%08x%08x", cpufeature, cpuid);
        return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit, cpuFreq);
    }

    private static Triplet<Integer, Integer, Integer> cpuidToFamilyModelStepping(int cpuid) {
        int family = cpuid >> 16 & 0xFF0 | cpuid >> 8 & 0xF;
        int model = cpuid >> 12 & 0xF0 | cpuid >> 4 & 0xF;
        int stepping = cpuid & 0xF;
        return new Triplet<Integer, Integer, Integer>(family, model, stepping);
    }

    @Override
    protected long queryMaxFreq() {
        return this.queryCurrentFreq()[0];
    }

    @Override
    protected long[] queryCurrentFreq() {
        long[] freq = new long[1];
        int[] mib = new int[]{6, 12};
        freq[0] = OpenBsdSysctlUtil.sysctl(mib, 0L) * 1000000L;
        return freq;
    }

    @Override
    protected Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> initProcessorCounts() {
        HashMap<Integer, Integer> coreMap = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> packageMap = new HashMap<Integer, Integer>();
        for (String line : ExecutingCommand.runNative("dmesg")) {
            Matcher m = DMESG_CPU.matcher(line);
            if (!m.matches()) continue;
            int cpu = ParseUtil.parseIntOrDefault(m.group(1), 0);
            coreMap.put(cpu, ParseUtil.parseIntOrDefault(m.group(3), 0));
            packageMap.put(cpu, ParseUtil.parseIntOrDefault(m.group(4), 0));
        }
        int logicalProcessorCount = OpenBsdSysctlUtil.sysctl("hw.ncpuonline", 1);
        if (logicalProcessorCount < coreMap.keySet().size()) {
            logicalProcessorCount = coreMap.keySet().size();
        }
        ArrayList<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<CentralProcessor.LogicalProcessor>(logicalProcessorCount);
        for (int i = 0; i < logicalProcessorCount; ++i) {
            logProcs.add(new CentralProcessor.LogicalProcessor(i, coreMap.getOrDefault(i, 0), packageMap.getOrDefault(i, 0)));
        }
        HashMap<Integer, String> dmesg = new HashMap<Integer, String>();
        Pattern p = Pattern.compile("cpu(\\\\d+).*: ((ARM|AMD|Intel).+)");
        for (String s : ExecutingCommand.runNative("dmesg")) {
            Matcher m = p.matcher(s);
            if (!m.matches()) continue;
            int coreId = ParseUtil.parseIntOrDefault(m.group(1), 0);
            dmesg.put(coreId, m.group(2).trim());
        }
        if (dmesg.isEmpty()) {
            return new Pair(logProcs, null);
        }
        return new Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>>(logProcs, this.createProcListFromDmesg(logProcs, dmesg));
    }

    @Override
    protected long queryContextSwitches() {
        return this.vmStats.get().getA();
    }

    @Override
    protected long queryInterrupts() {
        return this.vmStats.get().getB();
    }

    private static Pair<Long, Long> queryVmStats() {
        long contextSwitches = 0L;
        long interrupts = 0L;
        List<String> vmstat = ExecutingCommand.runNative("vmstat -s");
        for (String line : vmstat) {
            if (line.endsWith("cpu context switches")) {
                contextSwitches = ParseUtil.getFirstIntValue(line);
                continue;
            }
            if (!line.endsWith("interrupts")) continue;
            interrupts = ParseUtil.getFirstIntValue(line);
        }
        return new Pair<Long, Long>(contextSwitches, interrupts);
    }

    @Override
    protected long[] querySystemCpuLoadTicks() {
        long[] ticks = new long[CentralProcessor.TickType.values().length];
        int[] mib = new int[]{1, 40};
        Memory m = OpenBsdSysctlUtil.sysctl(mib);
        long[] cpuTicks = OpenBsdCentralProcessor.cpTimeToTicks(m, false);
        if (cpuTicks.length >= 5) {
            ticks[CentralProcessor.TickType.USER.getIndex()] = cpuTicks[0];
            ticks[CentralProcessor.TickType.NICE.getIndex()] = cpuTicks[1];
            ticks[CentralProcessor.TickType.SYSTEM.getIndex()] = cpuTicks[2];
            int offset = cpuTicks.length > 5 ? 1 : 0;
            ticks[CentralProcessor.TickType.IRQ.getIndex()] = cpuTicks[3 + offset];
            ticks[CentralProcessor.TickType.IDLE.getIndex()] = cpuTicks[4 + offset];
        }
        return ticks;
    }

    @Override
    protected long[][] queryProcessorCpuLoadTicks() {
        long[][] ticks = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
        int[] mib = new int[3];
        mib[0] = 1;
        mib[1] = 71;
        for (int cpu = 0; cpu < this.getLogicalProcessorCount(); ++cpu) {
            mib[2] = cpu;
            Memory m = OpenBsdSysctlUtil.sysctl(mib);
            long[] cpuTicks = OpenBsdCentralProcessor.cpTimeToTicks(m, true);
            if (cpuTicks.length < 5) continue;
            ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = cpuTicks[0];
            ticks[cpu][CentralProcessor.TickType.NICE.getIndex()] = cpuTicks[1];
            ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = cpuTicks[2];
            int offset = cpuTicks.length > 5 ? 1 : 0;
            ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] = cpuTicks[3 + offset];
            ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = cpuTicks[4 + offset];
        }
        return ticks;
    }

    private static long[] cpTimeToTicks(Memory m, boolean force64bit) {
        int arraySize;
        long longBytes = force64bit ? 8L : (long)Native.LONG_SIZE;
        int n = arraySize = m == null ? 0 : (int)(m.size() / longBytes);
        if (force64bit && m != null) {
            return m.getLongArray(0L, arraySize);
        }
        long[] ticks = new long[arraySize];
        for (int i = 0; i < arraySize; ++i) {
            ticks[i] = m.getNativeLong((long)i * longBytes).longValue();
        }
        return ticks;
    }

    @Override
    public double[] getSystemLoadAverage(int nelem) {
        if (nelem < 1 || nelem > 3) {
            throw new IllegalArgumentException("Must include from one to three elements.");
        }
        double[] average = new double[nelem];
        int retval = OpenBsdLibc.INSTANCE.getloadavg(average, nelem);
        if (retval < nelem) {
            Arrays.fill(average, -1.0);
        }
        return average;
    }
}

