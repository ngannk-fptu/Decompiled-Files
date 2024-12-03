/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.software.os.unix.freebsd;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.unix.freebsd.Who;
import oshi.jna.platform.unix.FreeBsdLibc;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;
import oshi.software.os.unix.freebsd.FreeBsdFileSystem;
import oshi.software.os.unix.freebsd.FreeBsdInternetProtocolStats;
import oshi.software.os.unix.freebsd.FreeBsdNetworkParams;
import oshi.software.os.unix.freebsd.FreeBsdOSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;
import oshi.util.platform.unix.freebsd.BsdSysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class FreeBsdOperatingSystem
extends AbstractOperatingSystem {
    private static final Logger LOG = LoggerFactory.getLogger(FreeBsdOperatingSystem.class);
    private static final long BOOTTIME = FreeBsdOperatingSystem.querySystemBootTime();
    static final String PS_COMMAND_ARGS = Arrays.stream(PsKeywords.values()).map(Enum::name).map(String::toLowerCase).collect(Collectors.joining(","));

    @Override
    public String queryManufacturer() {
        return "Unix/BSD";
    }

    @Override
    public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
        String family = BsdSysctlUtil.sysctl("kern.ostype", "FreeBSD");
        String version = BsdSysctlUtil.sysctl("kern.osrelease", "");
        String versionInfo = BsdSysctlUtil.sysctl("kern.version", "");
        String buildNumber = versionInfo.split(":")[0].replace(family, "").replace(version, "").trim();
        return new Pair<String, OperatingSystem.OSVersionInfo>(family, new OperatingSystem.OSVersionInfo(version, null, buildNumber));
    }

    @Override
    protected int queryBitness(int jvmBitness) {
        if (jvmBitness < 64 && ExecutingCommand.getFirstAnswer("uname -m").indexOf("64") == -1) {
            return jvmBitness;
        }
        return 64;
    }

    @Override
    public FileSystem getFileSystem() {
        return new FreeBsdFileSystem();
    }

    @Override
    public InternetProtocolStats getInternetProtocolStats() {
        return new FreeBsdInternetProtocolStats();
    }

    @Override
    public List<OSSession> getSessions() {
        return USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent();
    }

    @Override
    public List<OSProcess> queryAllProcesses() {
        return FreeBsdOperatingSystem.getProcessListFromPS(-1);
    }

    @Override
    public List<OSProcess> queryChildProcesses(int parentPid) {
        List<OSProcess> allProcs = this.queryAllProcesses();
        Set<Integer> descendantPids = FreeBsdOperatingSystem.getChildrenOrDescendants(allProcs, parentPid, false);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID())).collect(Collectors.toList());
    }

    @Override
    public List<OSProcess> queryDescendantProcesses(int parentPid) {
        List<OSProcess> allProcs = this.queryAllProcesses();
        Set<Integer> descendantPids = FreeBsdOperatingSystem.getChildrenOrDescendants(allProcs, parentPid, true);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID())).collect(Collectors.toList());
    }

    @Override
    public OSProcess getProcess(int pid) {
        List<OSProcess> procs = FreeBsdOperatingSystem.getProcessListFromPS(pid);
        if (procs.isEmpty()) {
            return null;
        }
        return procs.get(0);
    }

    private static List<OSProcess> getProcessListFromPS(int pid) {
        List<String> procList;
        ArrayList<OSProcess> procs = new ArrayList<OSProcess>();
        String psCommand = "ps -awwxo " + PS_COMMAND_ARGS;
        if (pid >= 0) {
            psCommand = psCommand + " -p " + pid;
        }
        if ((procList = ExecutingCommand.runNative(psCommand)).isEmpty() || procList.size() < 2) {
            return procs;
        }
        procList.remove(0);
        for (String proc : procList) {
            Map<PsKeywords, String> psMap = ParseUtil.stringToEnumMap(PsKeywords.class, proc.trim(), ' ');
            if (!psMap.containsKey((Object)PsKeywords.ARGS)) continue;
            procs.add(new FreeBsdOSProcess(pid < 0 ? ParseUtil.parseIntOrDefault(psMap.get((Object)PsKeywords.PID), 0) : pid, psMap));
        }
        return procs;
    }

    @Override
    public int getProcessId() {
        return FreeBsdLibc.INSTANCE.getpid();
    }

    @Override
    public int getProcessCount() {
        List<String> procList = ExecutingCommand.runNative("ps -axo pid");
        if (!procList.isEmpty()) {
            return procList.size() - 1;
        }
        return 0;
    }

    @Override
    public int getThreadCount() {
        int threads = 0;
        for (String proc : ExecutingCommand.runNative("ps -axo nlwp")) {
            threads += ParseUtil.parseIntOrDefault(proc.trim(), 0);
        }
        return threads;
    }

    @Override
    public long getSystemUptime() {
        return System.currentTimeMillis() / 1000L - BOOTTIME;
    }

    @Override
    public long getSystemBootTime() {
        return BOOTTIME;
    }

    private static long querySystemBootTime() {
        FreeBsdLibc.Timeval tv = new FreeBsdLibc.Timeval();
        if (!BsdSysctlUtil.sysctl("kern.boottime", tv) || tv.tv_sec == 0L) {
            return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L);
        }
        return tv.tv_sec;
    }

    @Override
    public NetworkParams getNetworkParams() {
        return new FreeBsdNetworkParams();
    }

    @Override
    public List<OSService> getServices() {
        File[] listFiles;
        ArrayList<OSService> services = new ArrayList<OSService>();
        HashSet<String> running = new HashSet<String>();
        for (OSProcess p : this.getChildProcesses(1, OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.PID_ASC, 0)) {
            OSService s = new OSService(p.getName(), p.getProcessID(), OSService.State.RUNNING);
            services.add(s);
            running.add(p.getName());
        }
        File dir = new File("/etc/rc.d");
        if (dir.exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
            for (File f : listFiles) {
                String name = f.getName();
                if (running.contains(name)) continue;
                OSService s = new OSService(name, 0, OSService.State.STOPPED);
                services.add(s);
            }
        } else {
            LOG.error("Directory: /etc/init does not exist");
        }
        return services;
    }

    static enum PsKeywords {
        STATE,
        PID,
        PPID,
        USER,
        UID,
        GROUP,
        GID,
        NLWP,
        PRI,
        VSZ,
        RSS,
        ETIMES,
        SYSTIME,
        TIME,
        COMM,
        MAJFLT,
        MINFLT,
        NVCSW,
        NIVCSW,
        ARGS;

    }
}

