/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.win32.Advapi32Util
 *  com.sun.jna.platform.win32.COM.WbemcliUtil$WmiResult
 *  com.sun.jna.platform.win32.Kernel32
 *  com.sun.jna.platform.win32.VersionHelpers
 *  com.sun.jna.platform.win32.Win32Exception
 *  com.sun.jna.platform.win32.WinBase$SYSTEM_INFO
 *  com.sun.jna.platform.win32.WinReg
 *  com.sun.jna.platform.win32.WinReg$HKEY
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.hardware.platform.windows;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinReg;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.LogicalProcessorInformation;
import oshi.driver.windows.perfmon.ProcessorInformation;
import oshi.driver.windows.perfmon.SystemInformation;
import oshi.driver.windows.wmi.Win32Processor;
import oshi.hardware.CentralProcessor;
import oshi.hardware.common.AbstractCentralProcessor;
import oshi.jna.platform.windows.PowrProf;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
final class WindowsCentralProcessor
extends AbstractCentralProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(WindowsCentralProcessor.class);
    private Map<String, Integer> numaNodeProcToLogicalProcMap;
    private static final boolean USE_CPU_UTILITY = VersionHelpers.IsWindows8OrGreater() && GlobalConfig.get("oshi.os.windows.cpu.utility", false);
    private final Supplier<Pair<List<String>, Map<ProcessorInformation.ProcessorUtilityTickCountProperty, List<Long>>>> processorUtilityCounters = USE_CPU_UTILITY ? Memoizer.memoize(WindowsCentralProcessor::queryProcessorUtilityCounters, TimeUnit.MILLISECONDS.toNanos(300L)) : null;
    private Map<ProcessorInformation.ProcessorUtilityTickCountProperty, List<Long>> initialUtilityCounters = USE_CPU_UTILITY ? this.processorUtilityCounters.get().getB() : null;
    private Long utilityBaseMultiplier = null;

    WindowsCentralProcessor() {
    }

    @Override
    protected CentralProcessor.ProcessorIdentifier queryProcessorId() {
        String processorID;
        WbemcliUtil.WmiResult<Win32Processor.ProcessorIdProperty> processorId;
        String cpuVendor = "";
        String cpuName = "";
        String cpuIdentifier = "";
        String cpuFamily = "";
        String cpuModel = "";
        String cpuStepping = "";
        long cpuVendorFreq = 0L;
        boolean cpu64bit = false;
        String cpuRegistryRoot = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\";
        String[] processorIds = Advapi32Util.registryGetKeys((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)"HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\");
        if (processorIds.length > 0) {
            String cpuRegistryPath = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\" + processorIds[0];
            cpuVendor = Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)cpuRegistryPath, (String)"VendorIdentifier");
            cpuName = Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)cpuRegistryPath, (String)"ProcessorNameString");
            cpuIdentifier = Advapi32Util.registryGetStringValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)cpuRegistryPath, (String)"Identifier");
            try {
                cpuVendorFreq = (long)Advapi32Util.registryGetIntValue((WinReg.HKEY)WinReg.HKEY_LOCAL_MACHINE, (String)cpuRegistryPath, (String)"~MHz") * 1000000L;
            }
            catch (Win32Exception win32Exception) {
                // empty catch block
            }
        }
        if (!cpuIdentifier.isEmpty()) {
            cpuFamily = WindowsCentralProcessor.parseIdentifier(cpuIdentifier, "Family");
            cpuModel = WindowsCentralProcessor.parseIdentifier(cpuIdentifier, "Model");
            cpuStepping = WindowsCentralProcessor.parseIdentifier(cpuIdentifier, "Stepping");
        }
        WinBase.SYSTEM_INFO sysinfo = new WinBase.SYSTEM_INFO();
        Kernel32.INSTANCE.GetNativeSystemInfo(sysinfo);
        int processorArchitecture = sysinfo.processorArchitecture.pi.wProcessorArchitecture.intValue();
        if (processorArchitecture == 9 || processorArchitecture == 12 || processorArchitecture == 6) {
            cpu64bit = true;
        }
        if ((processorId = Win32Processor.queryProcessorId()).getResultCount() > 0) {
            processorID = WmiUtil.getString(processorId, Win32Processor.ProcessorIdProperty.PROCESSORID, 0);
        } else {
            String[] stringArray;
            if (cpu64bit) {
                String[] stringArray2 = new String[1];
                stringArray = stringArray2;
                stringArray2[0] = "ia64";
            } else {
                stringArray = new String[]{};
            }
            processorID = WindowsCentralProcessor.createProcessorID(cpuStepping, cpuModel, cpuFamily, stringArray);
        }
        return new CentralProcessor.ProcessorIdentifier(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit, cpuVendorFreq);
    }

    private static String parseIdentifier(String identifier, String key) {
        String[] idSplit = ParseUtil.whitespaces.split(identifier);
        boolean found = false;
        for (String s : idSplit) {
            if (found) {
                return s;
            }
            found = s.equals(key);
        }
        return "";
    }

    @Override
    protected Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> initProcessorCounts() {
        if (VersionHelpers.IsWindows7OrGreater()) {
            Pair<List<CentralProcessor.LogicalProcessor>, List<CentralProcessor.PhysicalProcessor>> procs = LogicalProcessorInformation.getLogicalProcessorInformationEx();
            int curNode = -1;
            int procNum = 0;
            int lp = 0;
            this.numaNodeProcToLogicalProcMap = new HashMap<String, Integer>();
            for (CentralProcessor.LogicalProcessor logProc : procs.getA()) {
                int node = logProc.getNumaNode();
                if (node != curNode) {
                    curNode = node;
                    procNum = 0;
                }
                this.numaNodeProcToLogicalProcMap.put(String.format("%d,%d", logProc.getNumaNode(), procNum++), lp++);
            }
            return procs;
        }
        return LogicalProcessorInformation.getLogicalProcessorInformation();
    }

    @Override
    public long[] querySystemCpuLoadTicks() {
        long[] ticks = new long[CentralProcessor.TickType.values().length];
        long[][] procTicks = this.getProcessorCpuLoadTicks();
        for (int i = 0; i < ticks.length; ++i) {
            for (long[] procTick : procTicks) {
                int n = i;
                ticks[n] = ticks[n] + procTick[i];
            }
        }
        return ticks;
    }

    @Override
    public long[] queryCurrentFreq() {
        if (VersionHelpers.IsWindows7OrGreater()) {
            Pair<List<String>, Map<ProcessorInformation.ProcessorFrequencyProperty, List<Long>>> instanceValuePair = ProcessorInformation.queryFrequencyCounters();
            List<String> instances = instanceValuePair.getA();
            Map<ProcessorInformation.ProcessorFrequencyProperty, List<Long>> valueMap = instanceValuePair.getB();
            List<Long> percentMaxList = valueMap.get(ProcessorInformation.ProcessorFrequencyProperty.PERCENTOFMAXIMUMFREQUENCY);
            if (!instances.isEmpty()) {
                long maxFreq = this.getMaxFreq();
                long[] freqs = new long[this.getLogicalProcessorCount()];
                for (int p = 0; p < instances.size(); ++p) {
                    int cpu;
                    int n = cpu = instances.get(p).contains(",") ? this.numaNodeProcToLogicalProcMap.getOrDefault(instances.get(p), 0) : ParseUtil.parseIntOrDefault(instances.get(p), 0);
                    if (cpu >= this.getLogicalProcessorCount()) continue;
                    freqs[cpu] = percentMaxList.get(cpu) * maxFreq / 100L;
                }
                return freqs;
            }
        }
        return this.queryNTPower(2);
    }

    @Override
    public long queryMaxFreq() {
        long[] freqs = this.queryNTPower(1);
        return Arrays.stream(freqs).max().orElse(-1L);
    }

    private long[] queryNTPower(int fieldIndex) {
        PowrProf.ProcessorPowerInformation ppi = new PowrProf.ProcessorPowerInformation();
        long[] freqs = new long[this.getLogicalProcessorCount()];
        int bufferSize = ppi.size() * freqs.length;
        Memory mem = new Memory((long)bufferSize);
        if (0 != PowrProf.INSTANCE.CallNtPowerInformation(11, null, 0, (Pointer)mem, bufferSize)) {
            LOG.error("Unable to get Processor Information");
            Arrays.fill(freqs, -1L);
            return freqs;
        }
        for (int i = 0; i < freqs.length; ++i) {
            ppi = new PowrProf.ProcessorPowerInformation(mem.share((long)i * (long)ppi.size()));
            freqs[i] = fieldIndex == 1 ? (long)ppi.maxMhz * 1000000L : (fieldIndex == 2 ? (long)ppi.currentMhz * 1000000L : -1L);
        }
        return freqs;
    }

    @Override
    public double[] getSystemLoadAverage(int nelem) {
        if (nelem < 1 || nelem > 3) {
            throw new IllegalArgumentException("Must include from one to three elements.");
        }
        double[] average = new double[nelem];
        for (int i = 0; i < average.length; ++i) {
            average[i] = -1.0;
        }
        return average;
    }

    @Override
    public long[][] queryProcessorCpuLoadTicks() {
        List<Long> idleList;
        List<Long> softIrqList;
        List<Long> irqList;
        List<Long> userList;
        List<Long> systemList;
        Map<Enum, List<Long>> valueMap;
        List<String> instances;
        Pair<List<String>, Map<Enum, List<Long>>> instanceValuePair;
        List<Long> baseList = null;
        List<Long> systemUtility = null;
        List<Long> processorUtility = null;
        List<Long> processorUtilityBase = null;
        List<Long> initSystemList = null;
        List<Long> initUserList = null;
        List<Long> initBase = null;
        List<Long> initSystemUtility = null;
        List<Long> initProcessorUtility = null;
        List<Long> initProcessorUtilityBase = null;
        if (USE_CPU_UTILITY) {
            instanceValuePair = this.processorUtilityCounters.get();
            instances = instanceValuePair.getA();
            valueMap = instanceValuePair.getB();
            systemList = valueMap.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPRIVILEGEDTIME);
            userList = valueMap.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTUSERTIME);
            irqList = valueMap.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTINTERRUPTTIME);
            softIrqList = valueMap.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTDPCTIME);
            idleList = valueMap.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORTIME);
            baseList = valueMap.get(ProcessorInformation.ProcessorUtilityTickCountProperty.TIMESTAMP_SYS100NS);
            systemUtility = valueMap.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPRIVILEGEDUTILITY);
            processorUtility = valueMap.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORUTILITY);
            processorUtilityBase = valueMap.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORUTILITY_BASE);
            initSystemList = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPRIVILEGEDTIME);
            initUserList = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTUSERTIME);
            initBase = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.TIMESTAMP_SYS100NS);
            initSystemUtility = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPRIVILEGEDUTILITY);
            initProcessorUtility = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORUTILITY);
            initProcessorUtilityBase = this.initialUtilityCounters.get(ProcessorInformation.ProcessorUtilityTickCountProperty.PERCENTPROCESSORUTILITY_BASE);
        } else {
            instanceValuePair = ProcessorInformation.queryProcessorCounters();
            instances = instanceValuePair.getA();
            valueMap = instanceValuePair.getB();
            systemList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPRIVILEGEDTIME);
            userList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTUSERTIME);
            irqList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTINTERRUPTTIME);
            softIrqList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTDPCTIME);
            idleList = valueMap.get(ProcessorInformation.ProcessorTickCountProperty.PERCENTPROCESSORTIME);
        }
        int ncpu = this.getLogicalProcessorCount();
        long[][] ticks = new long[ncpu][CentralProcessor.TickType.values().length];
        if (instances.isEmpty() || systemList == null || userList == null || irqList == null || softIrqList == null || idleList == null || USE_CPU_UTILITY && (baseList == null || systemUtility == null || processorUtility == null || processorUtilityBase == null || initSystemList == null || initUserList == null || initBase == null || initSystemUtility == null || initProcessorUtility == null || initProcessorUtilityBase == null)) {
            return ticks;
        }
        for (int p = 0; p < instances.size(); ++p) {
            long deltaBase;
            long multiplier;
            long deltaT;
            int cpu;
            int n = cpu = instances.get(p).contains(",") ? this.numaNodeProcToLogicalProcMap.getOrDefault(instances.get(p), 0) : ParseUtil.parseIntOrDefault(instances.get(p), 0);
            if (cpu >= ncpu) continue;
            ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = systemList.get(cpu);
            ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = userList.get(cpu);
            ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] = irqList.get(cpu);
            ticks[cpu][CentralProcessor.TickType.SOFTIRQ.getIndex()] = softIrqList.get(cpu);
            ticks[cpu][CentralProcessor.TickType.IDLE.getIndex()] = idleList.get(cpu);
            if (USE_CPU_UTILITY && (deltaT = baseList.get(cpu) - initBase.get(cpu)) > 0L && (multiplier = this.lazilyCalculateMultiplier(deltaBase = processorUtilityBase.get(cpu) - initProcessorUtilityBase.get(cpu), deltaT)) > 0L) {
                long deltaProc = processorUtility.get(cpu) - initProcessorUtility.get(cpu);
                long deltaSys = systemUtility.get(cpu) - initSystemUtility.get(cpu);
                long newUser = initUserList.get(cpu) + multiplier * (deltaProc - deltaSys) / 100L;
                long newSystem = initSystemList.get(cpu) + multiplier * deltaSys / 100L;
                long delta = newUser - ticks[cpu][CentralProcessor.TickType.USER.getIndex()];
                ticks[cpu][CentralProcessor.TickType.USER.getIndex()] = newUser;
                ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()] = newSystem;
                long[] lArray = ticks[cpu];
                int n2 = CentralProcessor.TickType.IDLE.getIndex();
                lArray[n2] = lArray[n2] - (delta += newSystem - ticks[cpu][CentralProcessor.TickType.SYSTEM.getIndex()]);
            }
            long[] lArray = ticks[cpu];
            int n3 = CentralProcessor.TickType.SYSTEM.getIndex();
            lArray[n3] = lArray[n3] - (ticks[cpu][CentralProcessor.TickType.IRQ.getIndex()] + ticks[cpu][CentralProcessor.TickType.SOFTIRQ.getIndex()]);
            long[] lArray2 = ticks[cpu];
            int n4 = CentralProcessor.TickType.SYSTEM.getIndex();
            lArray2[n4] = lArray2[n4] / 10000L;
            long[] lArray3 = ticks[cpu];
            int n5 = CentralProcessor.TickType.USER.getIndex();
            lArray3[n5] = lArray3[n5] / 10000L;
            long[] lArray4 = ticks[cpu];
            int n6 = CentralProcessor.TickType.IRQ.getIndex();
            lArray4[n6] = lArray4[n6] / 10000L;
            long[] lArray5 = ticks[cpu];
            int n7 = CentralProcessor.TickType.SOFTIRQ.getIndex();
            lArray5[n7] = lArray5[n7] / 10000L;
            long[] lArray6 = ticks[cpu];
            int n8 = CentralProcessor.TickType.IDLE.getIndex();
            lArray6[n8] = lArray6[n8] / 10000L;
        }
        return ticks;
    }

    private synchronized long lazilyCalculateMultiplier(long deltaBase, long deltaT) {
        if (this.utilityBaseMultiplier == null) {
            if (deltaT >> 32 > 0L) {
                this.initialUtilityCounters = this.processorUtilityCounters.get().getB();
                return 0L;
            }
            if (deltaBase <= 0L) {
                deltaBase += 0x100000000L;
            }
            long multiplier = Math.round((double)deltaT / (double)deltaBase);
            if (deltaT < 50000000L) {
                return multiplier;
            }
            this.utilityBaseMultiplier = multiplier;
        }
        return this.utilityBaseMultiplier;
    }

    private static Pair<List<String>, Map<ProcessorInformation.ProcessorUtilityTickCountProperty, List<Long>>> queryProcessorUtilityCounters() {
        return ProcessorInformation.queryProcessorCapacityCounters();
    }

    @Override
    public long queryContextSwitches() {
        return SystemInformation.queryContextSwitchCounters().getOrDefault(SystemInformation.ContextSwitchProperty.CONTEXTSWITCHESPERSEC, 0L);
    }

    @Override
    public long queryInterrupts() {
        return ProcessorInformation.queryInterruptCounters().getOrDefault(ProcessorInformation.InterruptsProperty.INTERRUPTSPERSEC, 0L);
    }
}

