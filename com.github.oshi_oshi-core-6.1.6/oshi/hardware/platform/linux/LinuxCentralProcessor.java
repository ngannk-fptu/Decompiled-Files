/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.platform.linux.Udev
 *  com.sun.jna.platform.linux.Udev$UdevContext
 *  com.sun.jna.platform.linux.Udev$UdevDevice
 *  com.sun.jna.platform.linux.Udev$UdevEnumerate
 *  com.sun.jna.platform.linux.Udev$UdevListEntry
 */
package oshi.hardware.platform.linux;

import com.sun.jna.platform.linux.Udev;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.Lshw;
import oshi.driver.linux.proc.CpuStat;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.linux.LinuxLibc;
import oshi.software.os.linux.LinuxOperatingSystem;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.platform.linux.ProcPath;
import oshi.util.tuples.Pair;

@ThreadSafe
final class LinuxCentralProcessor
extends AbstractCentralProcessor {
    LinuxCentralProcessor() {
    }

    @Override
    protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
        String cpuVendor = "";
        String cpuName = "";
        String cpuFamily = "";
        String cpuModel = "";
        String cpuStepping = "";
        long cpuFreq = 0L;
        boolean cpu64bit = false;
        StringBuilder armStepping = new StringBuilder();
        String[] flags = new String[]{};
        List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
        block25: for (String line : cpuInfo) {
            String[] splitLine = ParseUtil.whitespacesColonWhitespace.split(line);
            if (splitLine.length < 2) {
                if (!line.startsWith("CPU architecture: ")) continue;
                cpuFamily = line.replace("CPU architecture: ", "").trim();
                continue;
            }
            block14 : switch (splitLine[0]) {
                case "vendor_id": 
                case "CPU implementer": {
                    cpuVendor = splitLine[1];
                    break;
                }
                case "model name": 
                case "Processor": {
                    cpuName = splitLine[1];
                    break;
                }
                case "flags": {
                    for (String flag : flags = splitLine[1].toLowerCase().split(" ")) {
                        if (!"lm".equals(flag)) continue;
                        cpu64bit = true;
                        break block14;
                    }
                    continue block25;
                }
                case "stepping": {
                    cpuStepping = splitLine[1];
                    break;
                }
                case "CPU variant": {
                    if (armStepping.toString().startsWith("r")) break;
                    armStepping.insert(0, "r" + splitLine[1]);
                    break;
                }
                case "CPU revision": {
                    if (armStepping.toString().contains("p")) break;
                    armStepping.append('p').append(splitLine[1]);
                    break;
                }
                case "model": 
                case "CPU part": {
                    cpuModel = splitLine[1];
                    break;
                }
                case "cpu family": {
                    cpuFamily = splitLine[1];
                    break;
                }
                case "cpu MHz": {
                    cpuFreq = ParseUtil.parseHertz(splitLine[1]);
                    break;
                }
            }
        }
        if (cpuName.contains("Hz")) {
            cpuFreq = -1L;
        } else {
            long cpuCapacity = Lshw.queryCpuCapacity();
            if (cpuCapacity > cpuFreq) {
                cpuFreq = cpuCapacity;
            }
        }
        if (cpuStepping.isEmpty()) {
            cpuStepping = armStepping.toString();
        }
        String processorID = LinuxCentralProcessor.getProcessorID(cpuVendor, cpuStepping, cpuModel, cpuFamily, flags);
        if (cpuVendor.startsWith("0x")) {
            List<String> lscpu = ExecutingCommand.runNative("lscpu");
            for (String line : lscpu) {
                if (!line.startsWith("Architecture:")) continue;
                cpuVendor = line.replace("Architecture:", "").trim();
            }
        }
        return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit, cpuFreq);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> initProcessorCounts() {
        ArrayList<CentralProcessor.LogicalProcessor> logProcs = new ArrayList<CentralProcessor.LogicalProcessor>();
        HashMap<Integer, Integer> coreEfficiencyMap = new HashMap<Integer, Integer>();
        HashMap<Integer, String> modAliasMap = new HashMap<Integer, String>();
        Udev.UdevContext udev = Udev.INSTANCE.udev_new();
        try {
            Udev.UdevEnumerate enumerate = udev.enumerateNew();
            try {
                enumerate.addMatchSubsystem("cpu");
                enumerate.scanDevices();
                for (Udev.UdevListEntry entry = enumerate.getListEntry(); entry != null; entry = entry.getNext()) {
                    String syspath = entry.getName();
                    int processor = ParseUtil.getFirstIntValue(syspath);
                    int coreId = FileUtil.getIntFromFile(syspath + "/topology/core_id");
                    int pkgId = FileUtil.getIntFromFile(syspath + "/topology/physical_package_id");
                    int pkgCoreKey = (pkgId << 16) + coreId;
                    coreEfficiencyMap.put(pkgCoreKey, FileUtil.getIntFromFile(syspath + "/cpu_capacity"));
                    Udev.UdevDevice device = udev.deviceNewFromSyspath(syspath);
                    if (device != null) {
                        try {
                            modAliasMap.put(pkgCoreKey, device.getPropertyValue("MODALIAS"));
                        }
                        finally {
                            device.unref();
                        }
                    }
                    int nodeId = 0;
                    String prefix = syspath + "/node";
                    try (Stream<Path> path = Files.list(Paths.get(syspath, new String[0]));){
                        Optional<Path> first = path.filter(p -> p.toString().startsWith(prefix)).findFirst();
                        if (first.isPresent()) {
                            nodeId = ParseUtil.getFirstIntValue(first.get().getFileName().toString());
                        }
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    logProcs.add(new CentralProcessor.LogicalProcessor(processor, coreId, pkgId, nodeId));
                }
            }
            finally {
                enumerate.unref();
            }
        }
        finally {
            udev.unref();
        }
        if (logProcs.isEmpty()) {
            logProcs.add(new CentralProcessor.LogicalProcessor(0, 0, 0));
            coreEfficiencyMap.put(0, 0);
        }
        List physProcs = coreEfficiencyMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(e -> {
            int pkgId = (Integer)e.getKey() >> 16;
            int coreId = (Integer)e.getKey() & 0xFFFF;
            return new CentralProcessor.PhysicalProcessor(pkgId, coreId, (Integer)e.getValue(), modAliasMap.getOrDefault(e.getKey(), ""));
        }).collect(Collectors.toList());
        return new Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>>(logProcs, physProcs);
    }

    @Override
    public long[] querySystemCpuLoadTicks() {
        long[] ticks = CpuStat.getSystemCpuLoadTicks();
        if (LongStream.of(ticks).sum() == 0L) {
            ticks = CpuStat.getSystemCpuLoadTicks();
        }
        long hz = LinuxOperatingSystem.getHz();
        for (int i = 0; i < ticks.length; ++i) {
            ticks[i] = ticks[i] * 1000L / hz;
        }
        return ticks;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long[] queryCurrentFreq() {
        long[] freqs = new long[this.getLogicalProcessorCount()];
        long max = 0L;
        Udev.UdevContext udev = Udev.INSTANCE.udev_new();
        try {
            Udev.UdevEnumerate enumerate = udev.enumerateNew();
            try {
                enumerate.addMatchSubsystem("cpu");
                enumerate.scanDevices();
                for (Udev.UdevListEntry entry = enumerate.getListEntry(); entry != null; entry = entry.getNext()) {
                    String syspath = entry.getName();
                    int cpu = ParseUtil.getFirstIntValue(syspath);
                    if (cpu >= 0 && cpu < freqs.length) {
                        freqs[cpu] = FileUtil.getLongFromFile(syspath + "/cpufreq/scaling_cur_freq");
                        if (freqs[cpu] == 0L) {
                            freqs[cpu] = FileUtil.getLongFromFile(syspath + "/cpufreq/cpuinfo_cur_freq");
                        }
                    }
                    if (max >= freqs[cpu]) continue;
                    max = freqs[cpu];
                }
                if (max > 0L) {
                    int i22 = 0;
                    while (i22 < freqs.length) {
                        int n = i22++;
                        freqs[n] = freqs[n] * 1000L;
                    }
                    long[] i22 = freqs;
                    return i22;
                }
            }
            finally {
                enumerate.unref();
            }
        }
        finally {
            udev.unref();
        }
        Arrays.fill(freqs, -1L);
        List<String> cpuInfo = FileUtil.readFile(ProcPath.CPUINFO);
        int proc = 0;
        for (String s : cpuInfo) {
            if (!s.toLowerCase().contains("cpu mhz")) continue;
            freqs[proc] = Math.round(ParseUtil.parseLastDouble(s, 0.0) * 1000000.0);
            if (++proc < freqs.length) continue;
            break;
        }
        return freqs;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long queryMaxFreq() {
        long max;
        block17: {
            max = Arrays.stream(this.getCurrentFreq()).max().orElse(-1L);
            if (max > 0L) {
                max /= 1000L;
            }
            Udev.UdevContext udev = Udev.INSTANCE.udev_new();
            try {
                Udev.UdevEnumerate enumerate = udev.enumerateNew();
                try {
                    enumerate.addMatchSubsystem("cpu");
                    enumerate.scanDevices();
                    Udev.UdevListEntry entry = enumerate.getListEntry();
                    if (entry == null) break block17;
                    String syspath = entry.getName();
                    String cpuFreqPath = syspath.substring(0, syspath.lastIndexOf(File.separatorChar)) + "/cpuFreq";
                    String policyPrefix = cpuFreqPath + "/policy";
                    try (Stream<Path> path = Files.list(Paths.get(cpuFreqPath, new String[0]));){
                        Optional<Long> maxPolicy = path.filter(p -> p.toString().startsWith(policyPrefix)).map(p -> {
                            long freq = FileUtil.getLongFromFile(p.toString() + "/scaling_max_freq");
                            if (freq == 0L) {
                                freq = FileUtil.getLongFromFile(p.toString() + "/cpuinfo_max_freq");
                            }
                            return freq;
                        }).max(Long::compare);
                        if (maxPolicy.isPresent() && max < maxPolicy.get()) {
                            max = maxPolicy.get();
                        }
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                }
                finally {
                    enumerate.unref();
                }
            }
            finally {
                udev.unref();
            }
        }
        if (max == 0L) {
            return -1L;
        }
        long lshwMax = Lshw.queryCpuCapacity();
        return lshwMax > (max *= 1000L) ? lshwMax : max;
    }

    @Override
    public double[] getSystemLoadAverage(int nelem) {
        if (nelem < 1 || nelem > 3) {
            throw new IllegalArgumentException("Must include from one to three elements.");
        }
        double[] average = new double[nelem];
        int retval = LinuxLibc.INSTANCE.getloadavg(average, nelem);
        if (retval < nelem) {
            for (int i = Math.max(retval, 0); i < average.length; ++i) {
                average[i] = -1.0;
            }
        }
        return average;
    }

    @Override
    public long[][] queryProcessorCpuLoadTicks() {
        long[][] ticks = CpuStat.getProcessorCpuLoadTicks(this.getLogicalProcessorCount());
        if (LongStream.of(ticks[0]).sum() == 0L) {
            ticks = CpuStat.getProcessorCpuLoadTicks(this.getLogicalProcessorCount());
        }
        long hz = LinuxOperatingSystem.getHz();
        for (int i = 0; i < ticks.length; ++i) {
            for (int j = 0; j < ticks[i].length; ++j) {
                ticks[i][j] = ticks[i][j] * 1000L / hz;
            }
        }
        return ticks;
    }

    private static String getProcessorID(String vendor, String stepping, String model, String family, String[] flags) {
        boolean procInfo = false;
        String marker = "Processor Information";
        for (String checkLine : ExecutingCommand.runNative("dmidecode -t 4")) {
            if (!procInfo && checkLine.contains(marker)) {
                marker = "ID:";
                procInfo = true;
                continue;
            }
            if (!procInfo || !checkLine.contains(marker)) continue;
            return checkLine.split(marker)[1].trim();
        }
        marker = "eax=";
        for (String checkLine : ExecutingCommand.runNative("cpuid -1r")) {
            if (!checkLine.contains(marker) || !checkLine.trim().startsWith("0x00000001")) continue;
            String eax = "";
            String edx = "";
            for (String register : ParseUtil.whitespaces.split(checkLine)) {
                if (register.startsWith("eax=")) {
                    eax = ParseUtil.removeMatchingString(register, "eax=0x");
                    continue;
                }
                if (!register.startsWith("edx=")) continue;
                edx = ParseUtil.removeMatchingString(register, "edx=0x");
            }
            return edx + eax;
        }
        if (vendor.startsWith("0x")) {
            return LinuxCentralProcessor.createMIDR(vendor, stepping, model, family) + "00000000";
        }
        return LinuxCentralProcessor.createProcessorID(stepping, model, family, flags);
    }

    private static String createMIDR(String vendor, String stepping, String model, String family) {
        int midrBytes = 0;
        if (stepping.startsWith("r") && stepping.contains("p")) {
            String[] rev = stepping.substring(1).split("p");
            midrBytes |= ParseUtil.parseLastInt(rev[1], 0);
            midrBytes |= ParseUtil.parseLastInt(rev[0], 0) << 20;
        }
        midrBytes |= ParseUtil.parseLastInt(model, 0) << 4;
        midrBytes |= ParseUtil.parseLastInt(family, 0) << 16;
        return String.format("%08X", midrBytes |= ParseUtil.parseLastInt(vendor, 0) << 24);
    }

    @Override
    public long queryContextSwitches() {
        return CpuStat.getContextSwitches();
    }

    @Override
    public long queryInterrupts() {
        return CpuStat.getInterrupts();
    }
}

