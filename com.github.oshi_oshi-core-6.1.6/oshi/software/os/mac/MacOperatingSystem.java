/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Structure
 *  com.sun.jna.platform.mac.SystemB
 *  com.sun.jna.platform.mac.SystemB$ProcTaskInfo
 *  com.sun.jna.platform.mac.SystemB$Timeval
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.software.os.mac;

import com.sun.jna.Structure;
import com.sun.jna.platform.mac.SystemB;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.mac.Who;
import oshi.driver.mac.WindowInfo;
import oshi.software.common.AbstractOperatingSystem;
import oshi.software.os.FileSystem;
import oshi.software.os.InternetProtocolStats;
import oshi.software.os.NetworkParams;
import oshi.software.os.OSDesktopWindow;
import oshi.software.os.OSProcess;
import oshi.software.os.OSService;
import oshi.software.os.OSSession;
import oshi.software.os.OperatingSystem;
import oshi.software.os.mac.MacFileSystem;
import oshi.software.os.mac.MacInternetProtocolStats;
import oshi.software.os.mac.MacNetworkParams;
import oshi.software.os.mac.MacOSProcess;
import oshi.util.ExecutingCommand;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;
import oshi.util.Util;
import oshi.util.platform.mac.SysctlUtil;
import oshi.util.tuples.Pair;

@ThreadSafe
public class MacOperatingSystem
extends AbstractOperatingSystem {
    private static final Logger LOG = LoggerFactory.getLogger(MacOperatingSystem.class);
    public static final String MACOS_VERSIONS_PROPERTIES = "oshi.macos.versions.properties";
    private static final String SYSTEM_LIBRARY_LAUNCH_AGENTS = "/System/Library/LaunchAgents";
    private static final String SYSTEM_LIBRARY_LAUNCH_DAEMONS = "/System/Library/LaunchDaemons";
    private int maxProc = 1024;
    private final String osXVersion;
    private final int major;
    private final int minor;
    private static final long BOOTTIME;

    public MacOperatingSystem() {
        String version = System.getProperty("os.version");
        int verMajor = ParseUtil.getFirstIntValue(version);
        int verMinor = ParseUtil.getNthIntValue(version, 2);
        if (verMajor == 10 && verMinor > 15) {
            String swVers = ExecutingCommand.getFirstAnswer("sw_vers -productVersion");
            if (!swVers.isEmpty()) {
                version = swVers;
            }
            verMajor = ParseUtil.getFirstIntValue(version);
            verMinor = ParseUtil.getNthIntValue(version, 2);
        }
        this.osXVersion = version;
        this.major = verMajor;
        this.minor = verMinor;
        this.maxProc = SysctlUtil.sysctl("kern.maxproc", 4096);
    }

    @Override
    public String queryManufacturer() {
        return "Apple";
    }

    @Override
    public Pair<String, OperatingSystem.OSVersionInfo> queryFamilyVersionInfo() {
        String family = this.major > 10 || this.major == 10 && this.minor >= 12 ? "macOS" : System.getProperty("os.name");
        String codeName = this.parseCodeName();
        String buildNumber = SysctlUtil.sysctl("kern.osversion", "");
        return new Pair<String, OperatingSystem.OSVersionInfo>(family, new OperatingSystem.OSVersionInfo(this.osXVersion, codeName, buildNumber));
    }

    private String parseCodeName() {
        Properties verProps = FileUtil.readPropertiesFromFilename(MACOS_VERSIONS_PROPERTIES);
        String codeName = null;
        if (this.major > 10) {
            codeName = verProps.getProperty(Integer.toString(this.major));
        } else if (this.major == 10) {
            codeName = verProps.getProperty(this.major + "." + this.minor);
        }
        if (Util.isBlank(codeName)) {
            LOG.warn("Unable to parse version {}.{} to a codename.", (Object)this.major, (Object)this.minor);
        }
        return codeName;
    }

    @Override
    protected int queryBitness(int jvmBitness) {
        if (jvmBitness == 64 || this.major == 10 && this.minor > 6) {
            return 64;
        }
        return ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("getconf LONG_BIT"), 32);
    }

    @Override
    public FileSystem getFileSystem() {
        return new MacFileSystem();
    }

    @Override
    public InternetProtocolStats getInternetProtocolStats() {
        return new MacInternetProtocolStats(this.isElevated());
    }

    @Override
    public List<OSSession> getSessions() {
        return USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent();
    }

    @Override
    public List<OSProcess> queryAllProcesses() {
        ArrayList<OSProcess> procs = new ArrayList<OSProcess>();
        int[] pids = new int[this.maxProc];
        int numberOfProcesses = SystemB.INSTANCE.proc_listpids(1, 0, pids, pids.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;
        for (int i = 0; i < numberOfProcesses; ++i) {
            OSProcess proc;
            if (pids[i] == 0 || (proc = this.getProcess(pids[i])) == null) continue;
            procs.add(proc);
        }
        return procs;
    }

    @Override
    public OSProcess getProcess(int pid) {
        MacOSProcess proc = new MacOSProcess(pid, this.minor);
        return proc.getState().equals((Object)OSProcess.State.INVALID) ? null : proc;
    }

    @Override
    public List<OSProcess> queryChildProcesses(int parentPid) {
        List<OSProcess> allProcs = this.queryAllProcesses();
        Set<Integer> descendantPids = MacOperatingSystem.getChildrenOrDescendants(allProcs, parentPid, false);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID())).collect(Collectors.toList());
    }

    @Override
    public List<OSProcess> queryDescendantProcesses(int parentPid) {
        List<OSProcess> allProcs = this.queryAllProcesses();
        Set<Integer> descendantPids = MacOperatingSystem.getChildrenOrDescendants(allProcs, parentPid, true);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID())).collect(Collectors.toList());
    }

    @Override
    public int getProcessId() {
        return SystemB.INSTANCE.getpid();
    }

    @Override
    public int getProcessCount() {
        return SystemB.INSTANCE.proc_listpids(1, 0, null, 0) / SystemB.INT_SIZE;
    }

    @Override
    public int getThreadCount() {
        int[] pids = new int[this.getProcessCount() + 10];
        int numberOfProcesses = SystemB.INSTANCE.proc_listpids(1, 0, pids, pids.length) / SystemB.INT_SIZE;
        int numberOfThreads = 0;
        SystemB.ProcTaskInfo taskInfo = new SystemB.ProcTaskInfo();
        for (int i = 0; i < numberOfProcesses; ++i) {
            int exit = SystemB.INSTANCE.proc_pidinfo(pids[i], 4, 0L, (Structure)taskInfo, taskInfo.size());
            if (exit == -1) continue;
            numberOfThreads += taskInfo.pti_threadnum;
        }
        return numberOfThreads;
    }

    @Override
    public long getSystemUptime() {
        return System.currentTimeMillis() / 1000L - BOOTTIME;
    }

    @Override
    public long getSystemBootTime() {
        return BOOTTIME;
    }

    @Override
    public NetworkParams getNetworkParams() {
        return new MacNetworkParams();
    }

    @Override
    public List<OSService> getServices() {
        ArrayList<OSService> services = new ArrayList<OSService>();
        HashSet<String> running = new HashSet<String>();
        for (OSProcess p : this.getChildProcesses(1, OperatingSystem.ProcessFiltering.ALL_PROCESSES, OperatingSystem.ProcessSorting.PID_ASC, 0)) {
            OSService s = new OSService(p.getName(), p.getProcessID(), OSService.State.RUNNING);
            services.add(s);
            running.add(p.getName());
        }
        ArrayList<File> files = new ArrayList<File>();
        File dir = new File(SYSTEM_LIBRARY_LAUNCH_AGENTS);
        if (dir.exists() && dir.isDirectory()) {
            files.addAll(Arrays.asList(dir.listFiles((f, name) -> name.toLowerCase().endsWith(".plist"))));
        } else {
            LOG.error("Directory: /System/Library/LaunchAgents does not exist");
        }
        dir = new File(SYSTEM_LIBRARY_LAUNCH_DAEMONS);
        if (dir.exists() && dir.isDirectory()) {
            files.addAll(Arrays.asList(dir.listFiles((f, name) -> name.toLowerCase().endsWith(".plist"))));
        } else {
            LOG.error("Directory: /System/Library/LaunchDaemons does not exist");
        }
        for (File f2 : files) {
            String shortName;
            String name2 = f2.getName().substring(0, f2.getName().length() - 6);
            int index = name2.lastIndexOf(46);
            String string = shortName = index < 0 || index > name2.length() - 2 ? name2 : name2.substring(index + 1);
            if (running.contains(name2) || running.contains(shortName)) continue;
            OSService s = new OSService(name2, 0, OSService.State.STOPPED);
            services.add(s);
        }
        return services;
    }

    @Override
    public List<OSDesktopWindow> getDesktopWindows(boolean visibleOnly) {
        return WindowInfo.queryDesktopWindows(visibleOnly);
    }

    static {
        SystemB.Timeval tv = new SystemB.Timeval();
        BOOTTIME = !SysctlUtil.sysctl("kern.boottime", (Structure)tv) || tv.tv_sec.longValue() == 0L ? ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll("\\D", ""), System.currentTimeMillis() / 1000L) : tv.tv_sec.longValue();
    }
}

