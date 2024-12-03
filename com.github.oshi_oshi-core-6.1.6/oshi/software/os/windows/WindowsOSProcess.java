/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Memory
 *  com.sun.jna.Pointer
 *  com.sun.jna.platform.win32.Advapi32
 *  com.sun.jna.platform.win32.Advapi32Util
 *  com.sun.jna.platform.win32.Advapi32Util$Account
 *  com.sun.jna.platform.win32.BaseTSD$ULONG_PTRByReference
 *  com.sun.jna.platform.win32.COM.WbemcliUtil$WmiResult
 *  com.sun.jna.platform.win32.Kernel32
 *  com.sun.jna.platform.win32.Kernel32Util
 *  com.sun.jna.platform.win32.Shell32Util
 *  com.sun.jna.platform.win32.VersionHelpers
 *  com.sun.jna.platform.win32.Win32Exception
 *  com.sun.jna.platform.win32.WinNT$HANDLE
 *  com.sun.jna.platform.win32.WinNT$HANDLEByReference
 *  com.sun.jna.ptr.IntByReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package oshi.software.os.windows;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.COM.WbemcliUtil;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.Shell32Util;
import com.sun.jna.platform.win32.VersionHelpers;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.driver.windows.registry.ProcessPerformanceData;
import oshi.driver.windows.registry.ProcessWtsData;
import oshi.driver.windows.registry.ThreadPerformanceData;
import oshi.driver.windows.wmi.Win32Process;
import oshi.driver.windows.wmi.Win32ProcessCached;
import oshi.jna.platform.windows.NtDll;
import oshi.software.common.AbstractOSProcess;
import oshi.software.os.OSProcess;
import oshi.software.os.OSThread;
import oshi.software.os.windows.WindowsOSThread;
import oshi.software.os.windows.WindowsOperatingSystem;
import oshi.util.GlobalConfig;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.platform.windows.WmiUtil;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

@ThreadSafe
public class WindowsOSProcess
extends AbstractOSProcess {
    private static final Logger LOG = LoggerFactory.getLogger(WindowsOSProcess.class);
    private static final boolean USE_BATCH_COMMANDLINE = GlobalConfig.get("oshi.os.windows.commandline.batch", false);
    private static final boolean USE_PROCSTATE_SUSPENDED = GlobalConfig.get("oshi.os.windows.procstate.suspended", false);
    private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();
    private static final boolean IS_WINDOWS7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();
    private final WindowsOperatingSystem os;
    private Supplier<Pair<String, String>> userInfo = Memoizer.memoize(this::queryUserInfo);
    private Supplier<Pair<String, String>> groupInfo = Memoizer.memoize(this::queryGroupInfo);
    private Supplier<String> currentWorkingDirectory = Memoizer.memoize(this::queryCwd);
    private Supplier<String> commandLine = Memoizer.memoize(this::queryCommandLine);
    private Supplier<List<String>> args = Memoizer.memoize(this::queryArguments);
    private Supplier<Triplet<String, String, Map<String, String>>> cwdCmdEnv = Memoizer.memoize(this::queryCwdCommandlineEnvironment);
    private String name;
    private String path;
    private OSProcess.State state = OSProcess.State.INVALID;
    private int parentProcessID;
    private int threadCount;
    private int priority;
    private long virtualSize;
    private long residentSetSize;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private long bytesRead;
    private long bytesWritten;
    private long openFiles;
    private int bitness;
    private long pageFaults;

    public WindowsOSProcess(int pid, WindowsOperatingSystem os, Map<Integer, ProcessPerformanceData.PerfCounterBlock> processMap, Map<Integer, ProcessWtsData.WtsInfo> processWtsMap, Map<Integer, ThreadPerformanceData.PerfCounterBlock> threadMap) {
        super(pid);
        this.os = os;
        this.bitness = os.getBitness();
        this.updateAttributes(processMap.get(pid), processWtsMap.get(pid), threadMap);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public String getCommandLine() {
        return this.commandLine.get();
    }

    @Override
    public List<String> getArguments() {
        return this.args.get();
    }

    @Override
    public Map<String, String> getEnvironmentVariables() {
        return this.cwdCmdEnv.get().getC();
    }

    @Override
    public String getCurrentWorkingDirectory() {
        return this.currentWorkingDirectory.get();
    }

    @Override
    public String getUser() {
        return this.userInfo.get().getA();
    }

    @Override
    public String getUserID() {
        return this.userInfo.get().getB();
    }

    @Override
    public String getGroup() {
        return this.groupInfo.get().getA();
    }

    @Override
    public String getGroupID() {
        return this.groupInfo.get().getB();
    }

    @Override
    public OSProcess.State getState() {
        return this.state;
    }

    @Override
    public int getParentProcessID() {
        return this.parentProcessID;
    }

    @Override
    public int getThreadCount() {
        return this.threadCount;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public long getVirtualSize() {
        return this.virtualSize;
    }

    @Override
    public long getResidentSetSize() {
        return this.residentSetSize;
    }

    @Override
    public long getKernelTime() {
        return this.kernelTime;
    }

    @Override
    public long getUserTime() {
        return this.userTime;
    }

    @Override
    public long getUpTime() {
        return this.upTime;
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getBytesRead() {
        return this.bytesRead;
    }

    @Override
    public long getBytesWritten() {
        return this.bytesWritten;
    }

    @Override
    public long getOpenFiles() {
        return this.openFiles;
    }

    @Override
    public int getBitness() {
        return this.bitness;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getAffinityMask() {
        WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, this.getProcessID());
        if (pHandle != null) {
            try {
                BaseTSD.ULONG_PTRByReference processAffinity = new BaseTSD.ULONG_PTRByReference();
                BaseTSD.ULONG_PTRByReference systemAffinity = new BaseTSD.ULONG_PTRByReference();
                if (Kernel32.INSTANCE.GetProcessAffinityMask(pHandle, processAffinity, systemAffinity)) {
                    long l = Pointer.nativeValue((Pointer)processAffinity.getValue().toPointer());
                    return l;
                }
            }
            finally {
                Kernel32.INSTANCE.CloseHandle(pHandle);
            }
            Kernel32.INSTANCE.CloseHandle(pHandle);
        }
        return 0L;
    }

    @Override
    public long getMinorFaults() {
        return this.pageFaults;
    }

    @Override
    public List<OSThread> getThreadDetails() {
        Map<Integer, ThreadPerformanceData.PerfCounterBlock> threads = ThreadPerformanceData.buildThreadMapFromRegistry(Collections.singleton(this.getProcessID()));
        if (threads != null) {
            threads = ThreadPerformanceData.buildThreadMapFromPerfCounters(Collections.singleton(this.getProcessID()));
        }
        if (threads == null) {
            return Collections.emptyList();
        }
        return threads.entrySet().stream().map(entry -> new WindowsOSThread(this.getProcessID(), (Integer)entry.getKey(), this.name, (ThreadPerformanceData.PerfCounterBlock)entry.getValue())).collect(Collectors.toList());
    }

    @Override
    public boolean updateAttributes() {
        Set<Integer> pids = Collections.singleton(this.getProcessID());
        Map<Integer, ProcessPerformanceData.PerfCounterBlock> pcb = ProcessPerformanceData.buildProcessMapFromRegistry(null);
        if (pcb == null) {
            pcb = ProcessPerformanceData.buildProcessMapFromPerfCounters(pids);
        }
        Map<Integer, ThreadPerformanceData.PerfCounterBlock> tcb = null;
        if (USE_PROCSTATE_SUSPENDED && (tcb = ThreadPerformanceData.buildThreadMapFromRegistry(null)) == null) {
            tcb = ThreadPerformanceData.buildThreadMapFromPerfCounters(null);
        }
        Map<Integer, ProcessWtsData.WtsInfo> wts = ProcessWtsData.queryProcessWtsMap(pids);
        return this.updateAttributes(pcb.get(this.getProcessID()), wts.get(this.getProcessID()), tcb);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean updateAttributes(ProcessPerformanceData.PerfCounterBlock pcb, ProcessWtsData.WtsInfo wts, Map<Integer, ThreadPerformanceData.PerfCounterBlock> threadMap) {
        WinNT.HANDLE pHandle;
        this.name = pcb.getName();
        this.path = wts.getPath();
        this.parentProcessID = pcb.getParentProcessID();
        this.threadCount = wts.getThreadCount();
        this.priority = pcb.getPriority();
        this.virtualSize = wts.getVirtualSize();
        this.residentSetSize = pcb.getResidentSetSize();
        this.kernelTime = wts.getKernelTime();
        this.userTime = wts.getUserTime();
        this.startTime = pcb.getStartTime();
        this.upTime = pcb.getUpTime();
        this.bytesRead = pcb.getBytesRead();
        this.bytesWritten = pcb.getBytesWritten();
        this.openFiles = wts.getOpenFiles();
        this.pageFaults = pcb.getPageFaults();
        this.state = OSProcess.State.RUNNING;
        if (threadMap != null) {
            int pid = this.getProcessID();
            for (ThreadPerformanceData.PerfCounterBlock tcb : threadMap.values()) {
                if (tcb.getOwningProcessID() != pid) continue;
                if (tcb.getThreadWaitReason() == 5) {
                    this.state = OSProcess.State.SUSPENDED;
                    continue;
                }
                this.state = OSProcess.State.RUNNING;
                break;
            }
        }
        if ((pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, this.getProcessID())) != null) {
            try {
                IntByReference wow64;
                if (IS_VISTA_OR_GREATER && this.bitness == 64 && Kernel32.INSTANCE.IsWow64Process(pHandle, wow64 = new IntByReference(0)) && wow64.getValue() > 0) {
                    this.bitness = 32;
                }
                WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
                try {
                    if (IS_WINDOWS7_OR_GREATER) {
                        this.path = Kernel32Util.QueryFullProcessImageName((WinNT.HANDLE)pHandle, (int)0);
                    }
                }
                catch (Win32Exception e) {
                    this.state = OSProcess.State.INVALID;
                }
                finally {
                    WinNT.HANDLE token = phToken.getValue();
                    if (token != null) {
                        Kernel32.INSTANCE.CloseHandle(token);
                    }
                }
            }
            finally {
                Kernel32.INSTANCE.CloseHandle(pHandle);
            }
        }
        return !this.state.equals((Object)OSProcess.State.INVALID);
    }

    private String queryCommandLine() {
        if (!this.cwdCmdEnv.get().getB().isEmpty()) {
            return this.cwdCmdEnv.get().getB();
        }
        if (USE_BATCH_COMMANDLINE) {
            return Win32ProcessCached.getInstance().getCommandLine(this.getProcessID(), this.getStartTime());
        }
        WbemcliUtil.WmiResult<Win32Process.CommandLineProperty> commandLineProcs = Win32Process.queryCommandLines(Collections.singleton(this.getProcessID()));
        if (commandLineProcs.getResultCount() > 0) {
            return WmiUtil.getString(commandLineProcs, Win32Process.CommandLineProperty.COMMANDLINE, 0);
        }
        return "";
    }

    private List<String> queryArguments() {
        String cl = this.getCommandLine();
        if (!cl.isEmpty()) {
            return Arrays.asList(Shell32Util.CommandLineToArgv((String)cl));
        }
        return Collections.emptyList();
    }

    private String queryCwd() {
        String cwd;
        if (!this.cwdCmdEnv.get().getA().isEmpty()) {
            return this.cwdCmdEnv.get().getA();
        }
        if (this.getProcessID() == this.os.getProcessId() && !(cwd = new File(".").getAbsolutePath()).isEmpty()) {
            return cwd.substring(0, cwd.length() - 1);
        }
        return "";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Pair<String, String> queryUserInfo() {
        Pair<String, String> pair = null;
        WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, this.getProcessID());
        if (pHandle != null) {
            WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
            try {
                if (Advapi32.INSTANCE.OpenProcessToken(pHandle, 10, phToken)) {
                    Advapi32Util.Account account = Advapi32Util.getTokenAccount((WinNT.HANDLE)phToken.getValue());
                    pair = new Pair<String, String>(account.name, account.sidString);
                } else {
                    int error = Kernel32.INSTANCE.GetLastError();
                    if (error != 5) {
                        LOG.error("Failed to get process token for process {}: {}", (Object)this.getProcessID(), (Object)Kernel32.INSTANCE.GetLastError());
                    }
                }
            }
            catch (Win32Exception e) {
                LOG.warn("Failed to query user info for process {} ({}): {}", new Object[]{this.getProcessID(), this.getName(), e.getMessage()});
            }
            finally {
                WinNT.HANDLE token = phToken.getValue();
                if (token != null) {
                    Kernel32.INSTANCE.CloseHandle(token);
                }
                Kernel32.INSTANCE.CloseHandle(pHandle);
            }
        }
        if (pair == null) {
            return new Pair<String, String>("unknown", "unknown");
        }
        return pair;
    }

    private Pair<String, String> queryGroupInfo() {
        Pair<String, String> pair = null;
        WinNT.HANDLE pHandle = Kernel32.INSTANCE.OpenProcess(1024, false, this.getProcessID());
        if (pHandle != null) {
            WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
            if (Advapi32.INSTANCE.OpenProcessToken(pHandle, 10, phToken)) {
                Advapi32Util.Account account = Advapi32Util.getTokenPrimaryGroup((WinNT.HANDLE)phToken.getValue());
                pair = new Pair<String, String>(account.name, account.sidString);
            } else {
                int error = Kernel32.INSTANCE.GetLastError();
                if (error != 5) {
                    LOG.error("Failed to get process token for process {}: {}", (Object)this.getProcessID(), (Object)Kernel32.INSTANCE.GetLastError());
                }
            }
            WinNT.HANDLE token = phToken.getValue();
            if (token != null) {
                Kernel32.INSTANCE.CloseHandle(token);
            }
            Kernel32.INSTANCE.CloseHandle(pHandle);
        }
        if (pair == null) {
            return new Pair<String, String>("unknown", "unknown");
        }
        return pair;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Triplet<String, String, Map<String, String>> queryCwdCommandlineEnvironment() {
        WinNT.HANDLE h = Kernel32.INSTANCE.OpenProcess(1040, false, this.getProcessID());
        if (h != null) {
            try {
                if (WindowsOperatingSystem.isX86() == WindowsOperatingSystem.isWow(h)) {
                    IntByReference nRead = new IntByReference();
                    NtDll.PROCESS_BASIC_INFORMATION pbi = new NtDll.PROCESS_BASIC_INFORMATION();
                    int ret = NtDll.INSTANCE.NtQueryInformationProcess(h, 0, pbi.getPointer(), pbi.size(), nRead);
                    if (ret != 0) {
                        Triplet<String, String, Map<String, String>> triplet = WindowsOSProcess.defaultCwdCommandlineEnvironment();
                        return triplet;
                    }
                    pbi.read();
                    NtDll.PEB peb = new NtDll.PEB();
                    Kernel32.INSTANCE.ReadProcessMemory(h, pbi.PebBaseAddress, peb.getPointer(), peb.size(), nRead);
                    if (nRead.getValue() == 0) {
                        Triplet<String, String, Map<String, String>> triplet = WindowsOSProcess.defaultCwdCommandlineEnvironment();
                        return triplet;
                    }
                    peb.read();
                    NtDll.RTL_USER_PROCESS_PARAMETERS upp = new NtDll.RTL_USER_PROCESS_PARAMETERS();
                    Kernel32.INSTANCE.ReadProcessMemory(h, peb.ProcessParameters, upp.getPointer(), upp.size(), nRead);
                    if (nRead.getValue() == 0) {
                        Triplet<String, String, Map<String, String>> triplet = WindowsOSProcess.defaultCwdCommandlineEnvironment();
                        return triplet;
                    }
                    upp.read();
                    String cwd = WindowsOSProcess.readUnicodeString(h, upp.CurrentDirectory.DosPath);
                    String cl = WindowsOSProcess.readUnicodeString(h, upp.CommandLine);
                    int envSize = upp.EnvironmentSize.intValue();
                    if (envSize > 0) {
                        Memory buffer = new Memory((long)envSize);
                        Kernel32.INSTANCE.ReadProcessMemory(h, upp.Environment, (Pointer)buffer, envSize, nRead);
                        if (nRead.getValue() > 0) {
                            char[] env = buffer.getCharArray(0L, envSize / 2);
                            Map<String, String> envMap = ParseUtil.parseCharArrayToStringMap(env);
                            envMap.remove("");
                            Triplet<String, String, Map<String, String>> triplet = new Triplet<String, String, Map<String, String>>(cwd, cl, Collections.unmodifiableMap(envMap));
                            return triplet;
                        }
                    }
                    Triplet<String, String, Map<String, String>> triplet = new Triplet<String, String, Map<String, String>>(cwd, cl, Collections.emptyMap());
                    return triplet;
                }
            }
            finally {
                Kernel32.INSTANCE.CloseHandle(h);
            }
        }
        return WindowsOSProcess.defaultCwdCommandlineEnvironment();
    }

    private static Triplet<String, String, Map<String, String>> defaultCwdCommandlineEnvironment() {
        return new Triplet<String, String, Map<String, String>>("", "", Collections.emptyMap());
    }

    private static String readUnicodeString(WinNT.HANDLE h, NtDll.UNICODE_STRING s) {
        IntByReference nRead = new IntByReference();
        if (s.Length > 0) {
            Memory m = new Memory((long)s.Length + 2L);
            m.clear();
            Kernel32.INSTANCE.ReadProcessMemory(h, s.Buffer, (Pointer)m, (int)s.Length, nRead);
            if (nRead.getValue() > 0) {
                return m.getWideString(0L);
            }
        }
        return "";
    }
}

