/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.unix.LibCAPI$size_t
 *  com.sun.jna.platform.unix.LibCAPI$size_t$ByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.unix.FreeBsdLibc;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
final class FreeBsdCentralProcessor
extends AbstractCentralProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(FreeBsdCentralProcessor.class);
    private static final Pattern CPUMASK = Pattern.compile(".*<cpu\\s.*mask=\"(?:0x)?(\\p{XDigit}+)\".*>.*</cpu>.*");

    FreeBsdCentralProcessor() {
    }

    @Override
    protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
        Pattern identifierPattern = Pattern.compile("Origin=\"([^\"]*)\".*Id=(\\S+).*Family=(\\S+).*Model=(\\S+).*Stepping=(\\S+).*");
        Pattern featuresPattern = Pattern.compile("Features=(\\S+)<.*");
        String cpuVendor = "";
        String cpuName = BsdSysctlUtil.sysctl("hw.model", "");
        String cpuFamily = "";
        String cpuModel = "";
        String cpuStepping = "";
        long cpuFreq = BsdSysctlUtil.sysctl("hw.clockrate", 0L) * 1000000L;
        long processorIdBits = 0L;
        List<String> cpuInfo = FileUtil.readFile("/var/run/dmesg.boot");
        for (String line : cpuInfo) {
            Matcher m;
            if ((line = line.trim()).startsWith("CPU:") && cpuName.isEmpty()) {
                cpuName = line.replace("CPU:", "").trim();
                continue;
            }
            if (line.startsWith("Origin=")) {
                m = identifierPattern.matcher(line);
                if (!m.matches()) continue;
                cpuVendor = m.group(1);
                processorIdBits |= Long.decode(m.group(2)).longValue();
                cpuFamily = Integer.decode(m.group(3)).toString();
                cpuModel = Integer.decode(m.group(4)).toString();
                cpuStepping = Integer.decode(m.group(5)).toString();
                continue;
            }
            if (!line.startsWith("Features=")) continue;
            m = featuresPattern.matcher(line);
            if (!m.matches()) break;
            processorIdBits |= Long.decode(m.group(1)) << 32;
            break;
        }
        boolean cpu64bit = ExecutingCommand.getFirstAnswer("uname -m").trim().contains("64");
        String processorID = FreeBsdCentralProcessor.getProcessorIDfromDmiDecode(processorIdBits);
        return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit, cpuFreq);
    }

    @Override
    protected Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> initProcessorCounts() {
        List<CentralProcessor.LogicalProcessor> logProcs = FreeBsdCentralProcessor.parseTopology();
        if (logProcs.isEmpty()) {
            logProcs.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
        }
        HashMap<Integer, String> dmesg = new HashMap<Integer, String>();
        Pattern normal = Pattern.compile("cpu(\\\\d+): (.+) on .*");
        Pattern hybrid = Pattern.compile("CPU\\\\s*(\\\\d+): (.+) affinity:.*");
        for (String s : FileUtil.readFile("/var/run/dmesg.boot")) {
            Matcher h = hybrid.matcher(s);
            if (h.matches()) {
                int coreId = ParseUtil.parseIntOrDefault(h.group(1), 0);
                dmesg.put(coreId, h.group(2).trim());
                continue;
            }
            Matcher n = normal.matcher(s);
            if (!n.matches()) continue;
            int coreId = ParseUtil.parseIntOrDefault(n.group(1), 0);
            dmesg.putIfAbsent(coreId, n.group(2).trim());
        }
        if (dmesg.isEmpty()) {
            return new Pair<List<CentralProcessor.LogicalProcessor>, Object>(logProcs, null);
        }
        return new Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>>(logProcs, this.createProcListFromDmesg(logProcs, dmesg));
    }

    private static List<CentralProcessor.LogicalProcessor> parseTopology() {
        String[] topology = BsdSysctlUtil.sysctl("kern.sched.topology_spec", "").split("\\n|\\r");
        long group1 = 1L;
        ArrayList<Long> group2 = new ArrayList<Long>();
        ArrayList<Long> group3 = new ArrayList<Long>();
        int groupLevel = 0;
        block5: for (String topo : topology) {
            Matcher m;
            if (topo.contains("<group level=")) {
                ++groupLevel;
                continue;
            }
            if (topo.contains("</group>")) {
                --groupLevel;
                continue;
            }
            if (!topo.contains("<cpu") || !(m = CPUMASK.matcher(topo)).matches()) continue;
            switch (groupLevel) {
                case 1: {
                    group1 = Long.parseLong(m.group(1), 16);
                    continue block5;
                }
                case 2: {
                    group2.add(Long.parseLong(m.group(1), 16));
                    continue block5;
                }
                case 3: {
                    group3.add(Long.parseLong(m.group(1), 16));
                    continue block5;
                }
            }
        }
        return FreeBsdCentralProcessor.matchBitmasks(group1, group2, group3);
    }

    private static List<CentralProcessor.LogicalProcessor> matchBitmasks(long group1, List<Long> group2, List<Long> group3) {
        ArrayList<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<CentralProcessor.LogicalProcessor>();
        int lowBit = Long.numberOfTrailingZeros(group1);
        int hiBit = 63 - Long.numberOfLeadingZeros(group1);
        for (int i = lowBit; i <= hiBit; ++i) {
            if ((group1 & 1L << i) <= 0L) continue;
            int numaNode = 0;
            CentralProcessor.LogicalProcessor logProc = new CentralProcessor.LogicalProcessor(i, FreeBsdCentralProcessor.getMatchingBitmask(group3, i), FreeBsdCentralProcessor.getMatchingBitmask(group2, i), numaNode);
            logProcs.add(logProc);
        }
        return logProcs;
    }

    private static int getMatchingBitmask(List<Long> bitmasks, int lp) {
        for (int j = 0; j < bitmasks.size(); ++j) {
            if ((bitmasks.get(j) & 1L << lp) == 0L) continue;
            return j;
        }
        return 0;
    }

    @Override
    public long[] querySystemCpuLoadTicks() {
        long[] ticks = new long[CentralProcessor.TickType.values().length];
        FreeBsdLibc.CpTime cpTime = new FreeBsdLibc.CpTime();
        BsdSysctlUtil.sysctl("kern.cp_time", cpTime);
        ticks[CentralProcessor.TickType.USER.getIndex()] = cpTime.cpu_ticks[0];
        ticks[CentralProcessor.TickType.NICE.getIndex()] = cpTime.cpu_ticks[1];
        ticks[CentralProcessor.TickType.SYSTEM.getIndex()] = cpTime.cpu_ticks[2];
        ticks[CentralProcessor.TickType.IRQ.getIndex()] = cpTime.cpu_ticks[3];
        ticks[CentralProcessor.TickType.IDLE.getIndex()] = cpTime.cpu_ticks[4];
        return ticks;
    }

    @Override
    public long[] queryCurrentFreq() {
        long[] freq = new long[]{BsdSysctlUtil.sysctl("dev.cpu.0.freq", -1L)};
        freq[0] = freq[0] > 0L ? freq[0] * 1000000L : BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
        return freq;
    }

    @Override
    public long queryMaxFreq() {
        long max = -1L;
        String freqLevels = BsdSysctlUtil.sysctl("dev.cpu.0.freq_levels", "");
        for (String s : ParseUtil.whitespaces.split(freqLevels)) {
            long freq = ParseUtil.parseLongOrDefault(s.split("/")[0], -1L);
            if (max >= freq) continue;
            max = freq;
        }
        max = max > 0L ? (max *= 1000000L) : BsdSysctlUtil.sysctl("machdep.tsc_freq", -1L);
        return max;
    }

    @Override
    public double[] getSystemLoadAverage(int nelem) {
        if (nelem < 1 || nelem > 3) {
            throw new IllegalArgumentException("Must include from one to three elements.");
        }
        double[] average = new double[nelem];
        int retval = FreeBsdLibc.INSTANCE.getloadavg(average, nelem);
        if (retval < nelem) {
            for (int i = Math.max(retval, 0); i < average.length; ++i) {
                average[i] = -1.0;
            }
        }
        return average;
    }

    @Override
    public long[][] queryProcessorCpuLoadTicks() {
        long[][] ticks = new long[this.getLogicalProcessorCount()][CentralProcessor.TickType.values().length];
        String name = "kern.cp_times";
        long size = new FreeBsdLibc.CpTime().size();
        long arraySize = size * (long)this.getLogicalProcessorCount();
        Memory p = new Memory(arraySize);
        if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, (Pointer)p, new LibCAPI.size_t.ByReference(new LibCAPI.size_t(arraySize)), null, LibCAPI.size_t.ZERO)) {
            LOG.error("Failed sysctl call: {}, Error code: {}", (Object)name, (Object)Native.getLastError());
            return ticks;
        }
        for (int cpu = 0; cpu < this.getLogicalProcessorCount(); ++cpu) {
            ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = p.getLong(size * (long)cpu + (long)(0 * FreeBsdLibc.UINT64_SIZE));
            ticks[cpu][CentralProcessor.TickType.NICE.getIndex()] = p.getLong(size * (long)cpu + (long)(1 * FreeBsdLibc.UINT64_SIZE));
            ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = p.getLong(size * (long)cpu + (long)(2 * FreeBsdLibc.UINT64_SIZE));
            ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] = p.getLong(size * (long)cpu + (long)(3 * FreeBsdLibc.UINT64_SIZE));
            ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = p.getLong(size * (long)cpu + (long)(4 * FreeBsdLibc.UINT64_SIZE));
        }
        return ticks;
    }

    private static String getProcessorIDfromDmiDecode(long processorID) {
        boolean procInfo = false;
        String marker = "Processor Information";
        for (String checkLine : ExecutingCommand.runNative("dmidecode -t system")) {
            if (!procInfo && checkLine.contains(marker)) {
                marker = "ID:";
                procInfo = true;
                continue;
            }
            if (!procInfo || !checkLine.contains(marker)) continue;
            return checkLine.split(marker)[1].trim();
        }
        return String.format("%016X", processorID);
    }

    @Override
    public long queryContextSwitches() {
        String name = "vm.stats.sys.v_swtch";
        LibCAPI.size_t.ByReference size = new LibCAPI.size_t.ByReference(new LibCAPI.size_t((long)FreeBsdLibc.INT_SIZE));
        Memory p = new Memory(size.longValue());
        if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, (Pointer)p, size, null, LibCAPI.size_t.ZERO)) {
            return 0L;
        }
        return ParseUtil.unsignedIntToLong(p.getInt(0L));
    }

    @Override
    public long queryInterrupts() {
        String name = "vm.stats.sys.v_intr";
        LibCAPI.size_t.ByReference size = new LibCAPI.size_t.ByReference(new LibCAPI.size_t((long)FreeBsdLibc.INT_SIZE));
        Memory p = new Memory(size.longValue());
        if (0 != FreeBsdLibc.INSTANCE.sysctlbyname(name, (Pointer)p, size, null, LibCAPI.size_t.ZERO)) {
            return 0L;
        }
        return ParseUtil.unsignedIntToLong(p.getInt(0L));
    }
}

