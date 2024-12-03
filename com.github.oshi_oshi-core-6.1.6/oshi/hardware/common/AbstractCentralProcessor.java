/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Platform
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.hardware.common;

import com.sun.jna.Platform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.linux.proc.Auxv;
import oshi.hardware.CentralProcessor;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public abstract class AbstractCentralProcessor
implements CentralProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCentralProcessor.class);
    private final Supplier<CentralProcessor.ProcessorIdentifier> cpuid = Memoizer.memoize(this::queryProcessorId);
    private final Supplier<Long> maxFreq = Memoizer.memoize(this::queryMaxFreq, Memoizer.defaultExpiration());
    private final Supplier<long[]> currentFreq = Memoizer.memoize(this::queryCurrentFreq, Memoizer.defaultExpiration());
    private final Supplier<Long> contextSwitches = Memoizer.memoize(this::queryContextSwitches, Memoizer.defaultExpiration());
    private final Supplier<Long> interrupts = Memoizer.memoize(this::queryInterrupts, Memoizer.defaultExpiration());
    private final Supplier<long[]> systemCpuLoadTicks = Memoizer.memoize(this::querySystemCpuLoadTicks, Memoizer.defaultExpiration());
    private final Supplier<long[][]> processorCpuLoadTicks = Memoizer.memoize(this::queryProcessorCpuLoadTicks, Memoizer.defaultExpiration());
    private final int physicalPackageCount;
    private final int physicalProcessorCount;
    private final int logicalProcessorCount;
    private final List<CentralProcessor.LogicalProcessor> logicalProcessors;
    private final List<CentralProcessor.PhysicalProcessor> physicalProcessors;

    protected AbstractCentralProcessor() {
        Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> processorLists = this.initProcessorCounts();
        this.logicalProcessors = Collections.unmodifiableList(processorLists.getA());
        if (processorLists.getB() == null) {
            Set pkgCoreKeys = this.logicalProcessors.stream().map(p -> (p.getPhysicalPackageNumber() << 16) + p.getPhysicalProcessorNumber()).collect(Collectors.toSet());
            List physProcs = pkgCoreKeys.stream().sorted().map(k -> new CentralProcessor.PhysicalProcessor(k >> 16, (int)(k & 0xFFFF))).collect(Collectors.toList());
            this.physicalProcessors = Collections.unmodifiableList(physProcs);
        } else {
            this.physicalProcessors = Collections.unmodifiableList(processorLists.getB());
        }
        HashSet<Integer> physPkgs = new HashSet<Integer>();
        for (CentralProcessor.LogicalProcessor logProc : this.logicalProcessors) {
            int pkg = logProc.getPhysicalPackageNumber();
            physPkgs.add(pkg);
        }
        this.logicalProcessorCount = this.logicalProcessors.size();
        this.physicalProcessorCount = this.physicalProcessors.size();
        this.physicalPackageCount = physPkgs.size();
    }

    protected abstract Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> initProcessorCounts();

    protected abstract CentralProcessor.ProcessorIdentifier queryProcessorId();

    @Override
    public CentralProcessor.ProcessorIdentifier getProcessorIdentifier() {
        return this.cpuid.get();
    }

    @Override
    public long getMaxFreq() {
        return this.maxFreq.get();
    }

    protected abstract long queryMaxFreq();

    @Override
    public long[] getCurrentFreq() {
        long[] freq = this.currentFreq.get();
        if (freq.length == this.getLogicalProcessorCount()) {
            return freq;
        }
        long[] freqs = new long[this.getLogicalProcessorCount()];
        Arrays.fill(freqs, freq[0]);
        return freqs;
    }

    protected abstract long[] queryCurrentFreq();

    @Override
    public long getContextSwitches() {
        return this.contextSwitches.get();
    }

    protected abstract long queryContextSwitches();

    @Override
    public long getInterrupts() {
        return this.interrupts.get();
    }

    protected abstract long queryInterrupts();

    @Override
    public List<CentralProcessor.LogicalProcessor> getLogicalProcessors() {
        return this.logicalProcessors;
    }

    @Override
    public List<CentralProcessor.PhysicalProcessor> getPhysicalProcessors() {
        return this.physicalProcessors;
    }

    @Override
    public long[] getSystemCpuLoadTicks() {
        return this.systemCpuLoadTicks.get();
    }

    protected abstract long[] querySystemCpuLoadTicks();

    @Override
    public long[][] getProcessorCpuLoadTicks() {
        return this.processorCpuLoadTicks.get();
    }

    protected abstract long[][] queryProcessorCpuLoadTicks();

    @Override
    public double getSystemCpuLoadBetweenTicks(long[] oldTicks) {
        if (oldTicks.length != CentralProcessor.TickType.values().length) {
            throw new IllegalArgumentException("Tick array " + oldTicks.length + " should have " + CentralProcessor.TickType.values().length + " elements");
        }
        long[] ticks = this.getSystemCpuLoadTicks();
        long total = 0L;
        for (int i = 0; i < ticks.length; ++i) {
            total += ticks[i] - oldTicks[i];
        }
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] + ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        LOG.trace("Total ticks: {}  Idle ticks: {}", (Object)total, (Object)idle);
        return total > 0L ? (double)(total - idle) / (double)total : 0.0;
    }

    @Override
    public double[] getProcessorCpuLoadBetweenTicks(long[][] oldTicks) {
        if (oldTicks.length != this.logicalProcessorCount || oldTicks[0].length != CentralProcessor.TickType.values().length) {
            throw new IllegalArgumentException("Tick array " + oldTicks.length + " should have " + this.logicalProcessorCount + " arrays, each of which has " + CentralProcessor.TickType.values().length + " elements");
        }
        long[][] ticks = this.getProcessorCpuLoadTicks();
        double[] load = new double[this.logicalProcessorCount];
        for (int cpu = 0; cpu < this.logicalProcessorCount; ++cpu) {
            long total = 0L;
            for (int i = 0; i < ticks[cpu].length; ++i) {
                total += ticks[cpu][i] - oldTicks[cpu][i];
            }
            long idle = ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] + ticks[cpu][CentralProcessor.TickType.IOWAIT.getIndex()] - oldTicks[cpu][CentralProcessor.TickType.IDLE.getIndex()] - oldTicks[cpu][CentralProcessor.TickType.IOWAIT.getIndex()];
            LOG.trace("CPU: {}  Total ticks: {}  Idle ticks: {}", new Object[]{cpu, total, idle});
            load[cpu] = total > 0L && idle >= 0L ? (double)(total - idle) / (double)total : 0.0;
        }
        return load;
    }

    @Override
    public int getLogicalProcessorCount() {
        return this.logicalProcessorCount;
    }

    @Override
    public int getPhysicalProcessorCount() {
        return this.physicalProcessorCount;
    }

    @Override
    public int getPhysicalPackageCount() {
        return this.physicalPackageCount;
    }

    protected static String createProcessorID(String stepping, String model, String family, String[] flags) {
        long processorIdBytes = 0L;
        long steppingL = ParseUtil.parseLongOrDefault(stepping, 0L);
        long modelL = ParseUtil.parseLongOrDefault(model, 0L);
        long familyL = ParseUtil.parseLongOrDefault(family, 0L);
        processorIdBytes |= steppingL & 0xFL;
        processorIdBytes |= (modelL & 0xFL) << 4;
        processorIdBytes |= (modelL & 0xF0L) << 16;
        processorIdBytes |= (familyL & 0xFL) << 8;
        processorIdBytes |= (familyL & 0xF0L) << 20;
        long hwcap = 0L;
        if (Platform.isLinux()) {
            hwcap = Auxv.queryAuxv().getOrDefault(16, 0L);
        }
        if (hwcap > 0L) {
            processorIdBytes |= hwcap << 32;
        } else {
            String[] stringArray = flags;
            int n = stringArray.length;
            block64: for (int i = 0; i < n; ++i) {
                String flag;
                switch (flag = stringArray[i]) {
                    case "fpu": {
                        processorIdBytes |= 0x100000000L;
                        continue block64;
                    }
                    case "vme": {
                        processorIdBytes |= 0x200000000L;
                        continue block64;
                    }
                    case "de": {
                        processorIdBytes |= 0x400000000L;
                        continue block64;
                    }
                    case "pse": {
                        processorIdBytes |= 0x800000000L;
                        continue block64;
                    }
                    case "tsc": {
                        processorIdBytes |= 0x1000000000L;
                        continue block64;
                    }
                    case "msr": {
                        processorIdBytes |= 0x2000000000L;
                        continue block64;
                    }
                    case "pae": {
                        processorIdBytes |= 0x4000000000L;
                        continue block64;
                    }
                    case "mce": {
                        processorIdBytes |= 0x8000000000L;
                        continue block64;
                    }
                    case "cx8": {
                        processorIdBytes |= 0x10000000000L;
                        continue block64;
                    }
                    case "apic": {
                        processorIdBytes |= 0x20000000000L;
                        continue block64;
                    }
                    case "sep": {
                        processorIdBytes |= 0x80000000000L;
                        continue block64;
                    }
                    case "mtrr": {
                        processorIdBytes |= 0x100000000000L;
                        continue block64;
                    }
                    case "pge": {
                        processorIdBytes |= 0x200000000000L;
                        continue block64;
                    }
                    case "mca": {
                        processorIdBytes |= 0x400000000000L;
                        continue block64;
                    }
                    case "cmov": {
                        processorIdBytes |= 0x800000000000L;
                        continue block64;
                    }
                    case "pat": {
                        processorIdBytes |= 0x1000000000000L;
                        continue block64;
                    }
                    case "pse-36": {
                        processorIdBytes |= 0x2000000000000L;
                        continue block64;
                    }
                    case "psn": {
                        processorIdBytes |= 0x4000000000000L;
                        continue block64;
                    }
                    case "clfsh": {
                        processorIdBytes |= 0x8000000000000L;
                        continue block64;
                    }
                    case "ds": {
                        processorIdBytes |= 0x20000000000000L;
                        continue block64;
                    }
                    case "acpi": {
                        processorIdBytes |= 0x40000000000000L;
                        continue block64;
                    }
                    case "mmx": {
                        processorIdBytes |= 0x80000000000000L;
                        continue block64;
                    }
                    case "fxsr": {
                        processorIdBytes |= 0x100000000000000L;
                        continue block64;
                    }
                    case "sse": {
                        processorIdBytes |= 0x200000000000000L;
                        continue block64;
                    }
                    case "sse2": {
                        processorIdBytes |= 0x400000000000000L;
                        continue block64;
                    }
                    case "ss": {
                        processorIdBytes |= 0x800000000000000L;
                        continue block64;
                    }
                    case "htt": {
                        processorIdBytes |= 0x1000000000000000L;
                        continue block64;
                    }
                    case "tm": {
                        processorIdBytes |= 0x2000000000000000L;
                        continue block64;
                    }
                    case "ia64": {
                        processorIdBytes |= 0x4000000000000000L;
                        continue block64;
                    }
                    case "pbe": {
                        processorIdBytes |= Long.MIN_VALUE;
                        continue block64;
                    }
                }
            }
        }
        return String.format("%016X", processorIdBytes);
    }

    protected List<CentralProcessor.PhysicalProcessor> createProcListFromDmesg(List<CentralProcessor.LogicalProcessor> logProcs, Map<Integer, String> dmesg) {
        boolean isHybrid = dmesg.values().stream().distinct().count() > 1L;
        ArrayList<CentralProcessor.PhysicalProcessor> physProcs = new ArrayList<CentralProcessor.PhysicalProcessor>();
        HashSet<Integer> pkgCoreKeys = new HashSet<Integer>();
        for (CentralProcessor.LogicalProcessor logProc : logProcs) {
            int coreId;
            int pkgId = logProc.getPhysicalPackageNumber();
            int pkgCoreKey = (pkgId << 16) + (coreId = logProc.getPhysicalProcessorNumber());
            if (pkgCoreKeys.contains(pkgCoreKey)) continue;
            pkgCoreKeys.add(pkgCoreKey);
            String idStr = dmesg.getOrDefault(logProc.getProcessorNumber(), "");
            int efficiency = 0;
            if (isHybrid && idStr.startsWith("ARM Cortex")) {
                efficiency = ParseUtil.getFirstIntValue(idStr) >= 70 ? 1 : 0;
            }
            physProcs.add(new CentralProcessor.PhysicalProcessor(pkgId, coreId, efficiency, idStr));
        }
        physProcs.sort(Comparator.comparingInt(CentralProcessor.PhysicalProcessor::getPhysicalPackageNumber).thenComparingInt(CentralProcessor.PhysicalProcessor::getPhysicalProcessorNumber));
        return physProcs;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.getProcessorIdentifier().getName());
        sb.append("\n ").append(this.getPhysicalPackageCount()).append(" physical CPU package(s)");
        sb.append("\n ").append(this.getPhysicalProcessorCount()).append(" physical CPU core(s)");
        HashMap<Integer, Integer> efficiencyCount = new HashMap<Integer, Integer>();
        int maxEfficiency = 0;
        for (CentralProcessor.PhysicalProcessor cpu : this.getPhysicalProcessors()) {
            int eff = cpu.getEfficiency();
            efficiencyCount.merge(eff, 1, Integer::sum);
            if (eff <= maxEfficiency) continue;
            maxEfficiency = eff;
        }
        int pCores = efficiencyCount.getOrDefault(maxEfficiency, 0);
        int eCores = this.getPhysicalProcessorCount() - pCores;
        if (eCores > 0) {
            sb.append(" (").append(pCores).append(" performance + ").append(eCores).append(" efficiency)");
        }
        sb.append("\n ").append(this.getLogicalProcessorCount()).append(" logical CPU(s)");
        sb.append('\n').append("Identifier: ").append(this.getProcessorIdentifier().getIdentifier());
        sb.append('\n').append("ProcessorID: ").append(this.getProcessorIdentifier().getProcessorID());
        sb.append('\n').append("Microarchitecture: ").append(this.getProcessorIdentifier().getMicroarchitecture());
        return sb.toString();
    }
}

