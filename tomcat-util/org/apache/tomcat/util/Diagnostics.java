/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.util;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformLoggingMXBean;
import java.lang.management.PlatformManagedObject;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class Diagnostics {
    private static final String PACKAGE = "org.apache.tomcat.util";
    private static final StringManager sm = StringManager.getManager("org.apache.tomcat.util");
    private static final String INDENT1 = "  ";
    private static final String INDENT2 = "\t";
    private static final String INDENT3 = "   ";
    private static final String CRLF = "\r\n";
    private static final String vminfoSystemProperty = "java.vm.info";
    private static final Log log = LogFactory.getLog(Diagnostics.class);
    private static final SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
    private static final CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
    private static final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
    private static final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static final PlatformLoggingMXBean loggingMXBean = ManagementFactory.getPlatformMXBean(PlatformLoggingMXBean.class);
    private static final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private static final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    private static final List<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
    private static final List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();

    public static boolean isThreadContentionMonitoringEnabled() {
        return threadMXBean.isThreadContentionMonitoringEnabled();
    }

    public static void setThreadContentionMonitoringEnabled(boolean enable) {
        threadMXBean.setThreadContentionMonitoringEnabled(enable);
        boolean checkValue = threadMXBean.isThreadContentionMonitoringEnabled();
        if (enable != checkValue) {
            log.error((Object)("Could not set threadContentionMonitoringEnabled to " + enable + ", got " + checkValue + " instead"));
        }
    }

    public static boolean isThreadCpuTimeEnabled() {
        return threadMXBean.isThreadCpuTimeEnabled();
    }

    public static void setThreadCpuTimeEnabled(boolean enable) {
        threadMXBean.setThreadCpuTimeEnabled(enable);
        boolean checkValue = threadMXBean.isThreadCpuTimeEnabled();
        if (enable != checkValue) {
            log.error((Object)("Could not set threadCpuTimeEnabled to " + enable + ", got " + checkValue + " instead"));
        }
    }

    public static void resetPeakThreadCount() {
        threadMXBean.resetPeakThreadCount();
    }

    public static void setVerboseClassLoading(boolean verbose) {
        classLoadingMXBean.setVerbose(verbose);
        boolean checkValue = classLoadingMXBean.isVerbose();
        if (verbose != checkValue) {
            log.error((Object)("Could not set verbose class loading to " + verbose + ", got " + checkValue + " instead"));
        }
    }

    public static void setLoggerLevel(String loggerName, String levelName) {
        loggingMXBean.setLoggerLevel(loggerName, levelName);
        String checkValue = loggingMXBean.getLoggerLevel(loggerName);
        if (!checkValue.equals(levelName)) {
            log.error((Object)("Could not set logger level for logger '" + loggerName + "' to '" + levelName + "', got '" + checkValue + "' instead"));
        }
    }

    public static void setVerboseGarbageCollection(boolean verbose) {
        memoryMXBean.setVerbose(verbose);
        boolean checkValue = memoryMXBean.isVerbose();
        if (verbose != checkValue) {
            log.error((Object)("Could not set verbose garbage collection logging to " + verbose + ", got " + checkValue + " instead"));
        }
    }

    public static void gc() {
        memoryMXBean.gc();
    }

    public static void resetPeakUsage(String name) {
        for (MemoryPoolMXBean mbean : memoryPoolMXBeans) {
            if (!name.equals("all") && !name.equals(mbean.getName())) continue;
            mbean.resetPeakUsage();
        }
    }

    public static boolean setUsageThreshold(String name, long threshold) {
        for (MemoryPoolMXBean mbean : memoryPoolMXBeans) {
            if (!name.equals(mbean.getName())) continue;
            try {
                mbean.setUsageThreshold(threshold);
                return true;
            }
            catch (IllegalArgumentException | UnsupportedOperationException runtimeException) {
                return false;
            }
        }
        return false;
    }

    public static boolean setCollectionUsageThreshold(String name, long threshold) {
        for (MemoryPoolMXBean mbean : memoryPoolMXBeans) {
            if (!name.equals(mbean.getName())) continue;
            try {
                mbean.setCollectionUsageThreshold(threshold);
                return true;
            }
            catch (IllegalArgumentException | UnsupportedOperationException runtimeException) {
                return false;
            }
        }
        return false;
    }

    private static String getThreadDumpHeader(ThreadInfo ti) {
        StringBuilder sb = new StringBuilder("\"" + ti.getThreadName() + "\"");
        sb.append(" Id=" + ti.getThreadId());
        sb.append(" cpu=" + threadMXBean.getThreadCpuTime(ti.getThreadId()) + " ns");
        sb.append(" usr=" + threadMXBean.getThreadUserTime(ti.getThreadId()) + " ns");
        sb.append(" blocked " + ti.getBlockedCount() + " for " + ti.getBlockedTime() + " ms");
        sb.append(" waited " + ti.getWaitedCount() + " for " + ti.getWaitedTime() + " ms");
        if (ti.isSuspended()) {
            sb.append(" (suspended)");
        }
        if (ti.isInNative()) {
            sb.append(" (running in native)");
        }
        sb.append(CRLF);
        sb.append("   java.lang.Thread.State: " + (Object)((Object)ti.getThreadState()));
        sb.append(CRLF);
        return sb.toString();
    }

    private static String getThreadDump(ThreadInfo ti) {
        MonitorInfo[] mis;
        StringBuilder sb = new StringBuilder(Diagnostics.getThreadDumpHeader(ti));
        for (LockInfo li : ti.getLockedSynchronizers()) {
            sb.append("\tlocks " + li.toString() + CRLF);
        }
        boolean start = true;
        StackTraceElement[] stes = ti.getStackTrace();
        Object[] monitorDepths = new Object[stes.length];
        for (MonitorInfo monitorInfo : mis = ti.getLockedMonitors()) {
            monitorDepths[monitorInfo.getLockedStackDepth()] = monitorInfo;
        }
        for (int i = 0; i < stes.length; ++i) {
            StackTraceElement ste = stes[i];
            sb.append("\tat " + ste.toString() + CRLF);
            if (start) {
                if (ti.getLockName() != null) {
                    sb.append("\t- waiting on (a " + ti.getLockName() + ")");
                    if (ti.getLockOwnerName() != null) {
                        sb.append(" owned by " + ti.getLockOwnerName() + " Id=" + ti.getLockOwnerId());
                    }
                    sb.append(CRLF);
                }
                start = false;
            }
            if (monitorDepths[i] == null) continue;
            MonitorInfo mi = (MonitorInfo)monitorDepths[i];
            sb.append("\t- locked (a " + mi.toString() + ") index " + mi.getLockedStackDepth() + " frame " + mi.getLockedStackFrame().toString());
            sb.append(CRLF);
        }
        return sb.toString();
    }

    private static String getThreadDump(ThreadInfo[] tinfos) {
        StringBuilder sb = new StringBuilder();
        for (ThreadInfo tinfo : tinfos) {
            sb.append(Diagnostics.getThreadDump(tinfo));
            sb.append(CRLF);
        }
        return sb.toString();
    }

    public static String findDeadlock() {
        ThreadInfo[] tinfos = null;
        long[] ids = threadMXBean.findDeadlockedThreads();
        if (ids != null && (tinfos = threadMXBean.getThreadInfo(threadMXBean.findDeadlockedThreads(), true, true)) != null) {
            StringBuilder sb = new StringBuilder("Deadlock found between the following threads:");
            sb.append(CRLF);
            sb.append(Diagnostics.getThreadDump(tinfos));
            return sb.toString();
        }
        return "";
    }

    public static String getThreadDump() {
        return Diagnostics.getThreadDump(sm);
    }

    public static String getThreadDump(Enumeration<Locale> requestedLocales) {
        return Diagnostics.getThreadDump(StringManager.getManager(PACKAGE, requestedLocales));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getThreadDump(StringManager requestedSm) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat simpleDateFormat = timeformat;
        synchronized (simpleDateFormat) {
            sb.append(timeformat.format(new Date()));
        }
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.threadDumpTitle"));
        sb.append(' ');
        sb.append(runtimeMXBean.getVmName());
        sb.append(" (");
        sb.append(runtimeMXBean.getVmVersion());
        String vminfo = System.getProperty(vminfoSystemProperty);
        if (vminfo != null) {
            sb.append(" " + vminfo);
        }
        sb.append("):\r\n");
        sb.append(CRLF);
        ThreadInfo[] tis = threadMXBean.dumpAllThreads(true, true);
        sb.append(Diagnostics.getThreadDump(tis));
        sb.append(Diagnostics.findDeadlock());
        return sb.toString();
    }

    private static String formatMemoryUsage(String name, MemoryUsage usage) {
        if (usage != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(INDENT1 + name + " init: " + usage.getInit() + CRLF);
            sb.append(INDENT1 + name + " used: " + usage.getUsed() + CRLF);
            sb.append(INDENT1 + name + " committed: " + usage.getCommitted() + CRLF);
            sb.append(INDENT1 + name + " max: " + usage.getMax() + CRLF);
            return sb.toString();
        }
        return "";
    }

    public static String getVMInfo() {
        return Diagnostics.getVMInfo(sm);
    }

    public static String getVMInfo(Enumeration<Locale> requestedLocales) {
        return Diagnostics.getVMInfo(StringManager.getManager(PACKAGE, requestedLocales));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getVMInfo(StringManager requestedSm) {
        Object names;
        StringBuilder sb = new StringBuilder();
        Iterator<PlatformManagedObject> iterator = timeformat;
        synchronized (iterator) {
            sb.append(timeformat.format(new Date()));
        }
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.vmInfoRuntime"));
        sb.append(":\r\n");
        sb.append("  vmName: " + runtimeMXBean.getVmName() + CRLF);
        sb.append("  vmVersion: " + runtimeMXBean.getVmVersion() + CRLF);
        sb.append("  vmVendor: " + runtimeMXBean.getVmVendor() + CRLF);
        sb.append("  specName: " + runtimeMXBean.getSpecName() + CRLF);
        sb.append("  specVersion: " + runtimeMXBean.getSpecVersion() + CRLF);
        sb.append("  specVendor: " + runtimeMXBean.getSpecVendor() + CRLF);
        sb.append("  managementSpecVersion: " + runtimeMXBean.getManagementSpecVersion() + CRLF);
        sb.append("  name: " + runtimeMXBean.getName() + CRLF);
        sb.append("  startTime: " + runtimeMXBean.getStartTime() + CRLF);
        sb.append("  uptime: " + runtimeMXBean.getUptime() + CRLF);
        sb.append("  isBootClassPathSupported: " + runtimeMXBean.isBootClassPathSupported() + CRLF);
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.vmInfoOs"));
        sb.append(":\r\n");
        sb.append("  name: " + operatingSystemMXBean.getName() + CRLF);
        sb.append("  version: " + operatingSystemMXBean.getVersion() + CRLF);
        sb.append("  architecture: " + operatingSystemMXBean.getArch() + CRLF);
        sb.append("  availableProcessors: " + operatingSystemMXBean.getAvailableProcessors() + CRLF);
        sb.append("  systemLoadAverage: " + operatingSystemMXBean.getSystemLoadAverage() + CRLF);
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.vmInfoThreadMxBean"));
        sb.append(":\r\n");
        sb.append("  isCurrentThreadCpuTimeSupported: " + threadMXBean.isCurrentThreadCpuTimeSupported() + CRLF);
        sb.append("  isThreadCpuTimeSupported: " + threadMXBean.isThreadCpuTimeSupported() + CRLF);
        sb.append("  isThreadCpuTimeEnabled: " + threadMXBean.isThreadCpuTimeEnabled() + CRLF);
        sb.append("  isObjectMonitorUsageSupported: " + threadMXBean.isObjectMonitorUsageSupported() + CRLF);
        sb.append("  isSynchronizerUsageSupported: " + threadMXBean.isSynchronizerUsageSupported() + CRLF);
        sb.append("  isThreadContentionMonitoringSupported: " + threadMXBean.isThreadContentionMonitoringSupported() + CRLF);
        sb.append("  isThreadContentionMonitoringEnabled: " + threadMXBean.isThreadContentionMonitoringEnabled() + CRLF);
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.vmInfoThreadCounts"));
        sb.append(":\r\n");
        sb.append("  daemon: " + threadMXBean.getDaemonThreadCount() + CRLF);
        sb.append("  total: " + threadMXBean.getThreadCount() + CRLF);
        sb.append("  peak: " + threadMXBean.getPeakThreadCount() + CRLF);
        sb.append("  totalStarted: " + threadMXBean.getTotalStartedThreadCount() + CRLF);
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.vmInfoStartup"));
        sb.append(":\r\n");
        for (String string : runtimeMXBean.getInputArguments()) {
            sb.append(INDENT1 + string + CRLF);
        }
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.vmInfoPath"));
        sb.append(":\r\n");
        if (runtimeMXBean.isBootClassPathSupported()) {
            sb.append("  bootClassPath: " + runtimeMXBean.getBootClassPath() + CRLF);
        }
        sb.append("  classPath: " + runtimeMXBean.getClassPath() + CRLF);
        sb.append("  libraryPath: " + runtimeMXBean.getLibraryPath() + CRLF);
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.vmInfoClassLoading"));
        sb.append(":\r\n");
        sb.append("  loaded: " + classLoadingMXBean.getLoadedClassCount() + CRLF);
        sb.append("  unloaded: " + classLoadingMXBean.getUnloadedClassCount() + CRLF);
        sb.append("  totalLoaded: " + classLoadingMXBean.getTotalLoadedClassCount() + CRLF);
        sb.append("  isVerbose: " + classLoadingMXBean.isVerbose() + CRLF);
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.vmInfoClassCompilation"));
        sb.append(":\r\n");
        sb.append("  name: " + compilationMXBean.getName() + CRLF);
        sb.append("  totalCompilationTime: " + compilationMXBean.getTotalCompilationTime() + CRLF);
        sb.append("  isCompilationTimeMonitoringSupported: " + compilationMXBean.isCompilationTimeMonitoringSupported() + CRLF);
        sb.append(CRLF);
        for (MemoryManagerMXBean memoryManagerMXBean : memoryManagerMXBeans) {
            sb.append(requestedSm.getString("diagnostics.vmInfoMemoryManagers", memoryManagerMXBean.getName()));
            sb.append(":\r\n");
            sb.append("  isValid: " + memoryManagerMXBean.isValid() + CRLF);
            sb.append("  mbean.getMemoryPoolNames: \r\n");
            names = memoryManagerMXBean.getMemoryPoolNames();
            Arrays.sort((Object[])names);
            for (Object name : names) {
                sb.append(INDENT2 + (String)name + CRLF);
            }
            sb.append(CRLF);
        }
        for (GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans) {
            sb.append(requestedSm.getString("diagnostics.vmInfoGarbageCollectors", garbageCollectorMXBean.getName()));
            sb.append(":\r\n");
            sb.append("  isValid: " + garbageCollectorMXBean.isValid() + CRLF);
            sb.append("  mbean.getMemoryPoolNames: \r\n");
            names = garbageCollectorMXBean.getMemoryPoolNames();
            Arrays.sort((Object[])names);
            for (Object name : names) {
                sb.append(INDENT2 + (String)name + CRLF);
            }
            sb.append("  getCollectionCount: " + garbageCollectorMXBean.getCollectionCount() + CRLF);
            sb.append("  getCollectionTime: " + garbageCollectorMXBean.getCollectionTime() + CRLF);
            sb.append(CRLF);
        }
        sb.append(requestedSm.getString("diagnostics.vmInfoMemory"));
        sb.append(":\r\n");
        sb.append("  isVerbose: " + memoryMXBean.isVerbose() + CRLF);
        sb.append("  getObjectPendingFinalizationCount: " + memoryMXBean.getObjectPendingFinalizationCount() + CRLF);
        sb.append(Diagnostics.formatMemoryUsage("heap", memoryMXBean.getHeapMemoryUsage()));
        sb.append(Diagnostics.formatMemoryUsage("non-heap", memoryMXBean.getNonHeapMemoryUsage()));
        sb.append(CRLF);
        for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
            sb.append(requestedSm.getString("diagnostics.vmInfoMemoryPools", memoryPoolMXBean.getName()));
            sb.append(":\r\n");
            sb.append("  isValid: " + memoryPoolMXBean.isValid() + CRLF);
            sb.append("  getType: " + (Object)((Object)memoryPoolMXBean.getType()) + CRLF);
            sb.append("  mbean.getMemoryManagerNames: \r\n");
            names = memoryPoolMXBean.getMemoryManagerNames();
            Arrays.sort((Object[])names);
            for (Object name : names) {
                sb.append(INDENT2 + (String)name + CRLF);
            }
            sb.append("  isUsageThresholdSupported: " + memoryPoolMXBean.isUsageThresholdSupported() + CRLF);
            try {
                sb.append("  isUsageThresholdExceeded: " + memoryPoolMXBean.isUsageThresholdExceeded() + CRLF);
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
            sb.append("  isCollectionUsageThresholdSupported: " + memoryPoolMXBean.isCollectionUsageThresholdSupported() + CRLF);
            try {
                sb.append("  isCollectionUsageThresholdExceeded: " + memoryPoolMXBean.isCollectionUsageThresholdExceeded() + CRLF);
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
            try {
                sb.append("  getUsageThreshold: " + memoryPoolMXBean.getUsageThreshold() + CRLF);
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
            try {
                sb.append("  getUsageThresholdCount: " + memoryPoolMXBean.getUsageThresholdCount() + CRLF);
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
            try {
                sb.append("  getCollectionUsageThreshold: " + memoryPoolMXBean.getCollectionUsageThreshold() + CRLF);
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
            try {
                sb.append("  getCollectionUsageThresholdCount: " + memoryPoolMXBean.getCollectionUsageThresholdCount() + CRLF);
            }
            catch (UnsupportedOperationException unsupportedOperationException) {
                // empty catch block
            }
            sb.append(Diagnostics.formatMemoryUsage("current", memoryPoolMXBean.getUsage()));
            sb.append(Diagnostics.formatMemoryUsage("collection", memoryPoolMXBean.getCollectionUsage()));
            sb.append(Diagnostics.formatMemoryUsage("peak", memoryPoolMXBean.getPeakUsage()));
            sb.append(CRLF);
        }
        sb.append(requestedSm.getString("diagnostics.vmInfoSystem"));
        sb.append(":\r\n");
        Map<String, String> props = runtimeMXBean.getSystemProperties();
        ArrayList<String> arrayList = new ArrayList<String>(props.keySet());
        Collections.sort(arrayList);
        for (String prop : arrayList) {
            sb.append(INDENT1 + prop + ": " + props.get(prop) + CRLF);
        }
        sb.append(CRLF);
        sb.append(requestedSm.getString("diagnostics.vmInfoLogger"));
        sb.append(":\r\n");
        List<String> loggers = loggingMXBean.getLoggerNames();
        Collections.sort(loggers);
        for (String logger : loggers) {
            sb.append(INDENT1 + logger + ": level=" + loggingMXBean.getLoggerLevel(logger) + ", parent=" + loggingMXBean.getParentLoggerName(logger) + CRLF);
        }
        sb.append(CRLF);
        return sb.toString();
    }
}

