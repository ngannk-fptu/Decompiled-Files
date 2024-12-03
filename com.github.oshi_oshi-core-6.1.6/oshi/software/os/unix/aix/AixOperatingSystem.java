/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_partition_config_t
 *  com.sun.jna.platform.unix.aix.Perfstat$perfstat_process_t
 */
package oshi.software.os.unix.aix;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.aix.Uptime;
import oshi.driver.unix.aix.Who;
import oshi.driver.unix.aix.perfstat.PerfstatConfig;
import oshi.driver.unix.aix.perfstat.PerfstatProcess;
import oshi.jna.platform.unix.AixLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OperatingSystem;
import oshi.software.os.unix.aix.AixFileSystem;
import oshi.software.os.unix.aix.AixInternetProtocolStats;
import oshi.software.os.unix.aix.AixNetworkParams;
import oshi.software.os.unix.aix.AixOSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.tuples.Pair;

@ThreadSafe
public class AixOperatingSystem
extends AbstractOperatingSystem {
    private final Supplier<Perfstat.perfstat_partition_config_t> config = Memoizer.memoize(PerfstatConfig::queryConfig);
    private final Supplier<Perfstat.perfstat_process_t[]> procCpu = Memoizer.memoize(PerfstatProcess::queryProcesses, Memoizer.defaultExpiration());
    private static final long BOOTTIME = AixOperatingSystem.querySystemBootTimeMillis() / 1000L;

    @Override
    public String queryManufacturer() {
        return "IBM";
    }

    @Override
    public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
        String releaseNumber;
        Perfstat.perfstat_partition_config_t cfg = this.config.get();
        String systemName = System.getProperty("os.name");
        String archName = System.getProperty("os.arch");
        String versionNumber = System.getProperty("os.version");
        if (Util.isBlank(versionNumber)) {
            versionNumber = ExecutingCommand.getFirstAnswer("oslevel");
        }
        if (Util.isBlank(releaseNumber = Native.toString((byte[])cfg.OSBuild))) {
            releaseNumber = ExecutingCommand.getFirstAnswer("oslevel -s");
        } else {
            int idx = releaseNumber.lastIndexOf(32);
            if (idx > 0 && idx < releaseNumber.length()) {
                releaseNumber = releaseNumber.substring(idx + 1);
            }
        }
        return new Pair<String, OperatingSystem.OSVersionInfo>(systemName, new OperatingSystem.OSVersionInfo(versionNumber, archName, releaseNumber));
    }

    @Override
    protected int queryBitness(int jvmBitness) {
        if (jvmBitness == 64) {
            return 64;
        }
        return (this.config.get().conf & 0x800000) > 0 ? 64 : 32;
    }

    @Override
    public FileSystem getFileSystem() {
        return new AixFileSystem();
    }

    @Override
    public InternetProtocolStats getInternetProtocolStats() {
        return new AixInternetProtocolStats();
    }

    @Override
    public List<OSProcess> queryAllProcesses() {
        return this.getProcessListFromProcfs(-1);
    }

    @Override
    public List<OSProcess> queryChildProcesses(int parentPid) {
        List<OSProcess> allProcs = this.queryAllProcesses();
        Set<Integer> descendantPids = AixOperatingSystem.getChildrenOrDescendants(allProcs, parentPid, false);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID())).collect(Collectors.toList());
    }

    @Override
    public List<OSProcess> queryDescendantProcesses(int parentPid) {
        List<OSProcess> allProcs = this.queryAllProcesses();
        Set<Integer> descendantPids = AixOperatingSystem.getChildrenOrDescendants(allProcs, parentPid, true);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID())).collect(Collectors.toList());
    }

    @Override
    public OSProcess getProcess(int pid) {
        List<OSProcess> procs = this.getProcessListFromProcfs(pid);
        if (procs.isEmpty()) {
            return null;
        }
        return procs.get(0);
    }

    private List<OSProcess> getProcessListFromProcfs(int pid) {
        ArrayList<OSProcess> procs = new ArrayList<OSProcess>();
        Perfstat.perfstat_process_t[] perfstat = this.procCpu.get();
        HashMap<Integer, Pair<Long, Long>> cpuMap = new HashMap<Integer, Pair<Long, Long>>();
        for (Perfstat.perfstat_process_t stat : perfstat) {
            int statpid = (int)stat.pid;
            if (pid >= 0 && statpid != pid) continue;
            cpuMap.put(statpid, new Pair<Long, Long>((long)stat.ucpu_time, (long)stat.scpu_time));
        }
        for (Map.Entry entry : cpuMap.entrySet()) {
            AixOSProcess proc = new AixOSProcess((Integer)entry.getKey(), (Pair)entry.getValue(), this.procCpu);
            if (proc.getState() == OSProcess.State.INVALID) continue;
            procs.add(proc);
        }
        return procs;
    }

    @Override
    public int getProcessId() {
        return AixLibc.INSTANCE.getpid();
    }

    @Override
    public int getProcessCount() {
        return this.procCpu.get().length;
    }

    @Override
    public int getThreadCount() {
        long tc = 0L;
        for (Perfstat.perfstat_process_t proc : this.procCpu.get()) {
            tc += proc.num_threads;
        }
        return (int)tc;
    }

    @Override
    public long getSystemUptime() {
        return System.currentTimeMillis() / 1000L - BOOTTIME;
    }

    @Override
    public long getSystemBootTime() {
        return BOOTTIME;
    }

    private static long querySystemBootTimeMillis() {
        long bootTime = Who.queryBootTime();
        if (bootTime >= 1000L) {
            return bootTime;
        }
        return System.currentTimeMillis() - Uptime.queryUpTime();
    }

    @Override
    public NetworkParams getNetworkParams() {
        return new AixNetworkParams();
    }

    @Override
    public List<OSService> getServices() {
        File[] listFiles;
        File dir;
        ArrayList<OSService> services = new ArrayList<OSService>();
        List<String> systemServicesInfoList = ExecutingCommand.runNative("lssrc -a");
        if (systemServicesInfoList.size() > 1) {
            systemServicesInfoList.remove(0);
            for (String systemService : systemServicesInfoList) {
                String[] serviceSplit = ParseUtil.whitespaces.split(systemService.trim());
                if (systemService.contains("active")) {
                    if (serviceSplit.length == 4) {
                        services.add(new OSService(serviceSplit[0], ParseUtil.parseIntOrDefault(serviceSplit[2], 0), OSService.State.RUNNING));
                        continue;
                    }
                    if (serviceSplit.length != 3) continue;
                    services.add(new OSService(serviceSplit[0], ParseUtil.parseIntOrDefault(serviceSplit[1], 0), OSService.State.RUNNING));
                    continue;
                }
                if (!systemService.contains("inoperative")) continue;
                services.add(new OSService(serviceSplit[0], 0, OSService.State.STOPPED));
            }
        }
        if ((dir = new File("/etc/rc.d/init.d")).exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
            for (File file : listFiles) {
                String installedService = ExecutingCommand.getFirstAnswer(file.getAbsolutePath() + " status");
                if (installedService.contains("running")) {
                    services.add(new OSService(file.getName(), ParseUtil.parseLastInt(installedService, 0), OSService.State.RUNNING));
                    continue;
                }
                services.add(new OSService(file.getName(), 0, OSService.State.STOPPED));
            }
        }
        return services;
    }
}

