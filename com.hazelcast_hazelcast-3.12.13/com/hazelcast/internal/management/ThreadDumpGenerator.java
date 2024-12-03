/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public final class ThreadDumpGenerator {
    private static final ILogger LOGGER = Logger.getLogger(ThreadDumpGenerator.class);

    private ThreadDumpGenerator() {
    }

    public static String dumpAllThreads() {
        LOGGER.finest("Generating full thread dump...");
        StringBuilder s = new StringBuilder();
        s.append("Full thread dump ");
        return ThreadDumpGenerator.dump(ThreadDumpGenerator.getAllThreads(), s);
    }

    public static String dumpDeadlocks() {
        LOGGER.finest("Generating dead-locked threads dump...");
        StringBuilder s = new StringBuilder();
        s.append("Deadlocked thread dump ");
        return ThreadDumpGenerator.dump(ThreadDumpGenerator.findDeadlockedThreads(), s);
    }

    private static String dump(ThreadInfo[] infos, StringBuilder s) {
        ThreadDumpGenerator.header(s);
        ThreadDumpGenerator.appendThreadInfos(infos, s);
        if (LOGGER.isFinestEnabled()) {
            LOGGER.finest("\n" + s);
        }
        return s.toString();
    }

    public static ThreadInfo[] findDeadlockedThreads() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        if (threadMXBean.isSynchronizerUsageSupported()) {
            long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
            if (deadlockedThreads == null || deadlockedThreads.length == 0) {
                return null;
            }
            return threadMXBean.getThreadInfo(deadlockedThreads, true, true);
        }
        long[] monitorDeadlockedThreads = threadMXBean.findMonitorDeadlockedThreads();
        return ThreadDumpGenerator.getThreadInfos(threadMXBean, monitorDeadlockedThreads);
    }

    public static ThreadInfo[] getAllThreads() {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        if (threadMXBean.isObjectMonitorUsageSupported() && threadMXBean.isSynchronizerUsageSupported()) {
            return threadMXBean.dumpAllThreads(true, true);
        }
        long[] allThreadIds = threadMXBean.getAllThreadIds();
        return ThreadDumpGenerator.getThreadInfos(threadMXBean, allThreadIds);
    }

    private static ThreadInfo[] getThreadInfos(ThreadMXBean threadMXBean, long[] threadIds) {
        if (threadIds == null || threadIds.length == 0) {
            return null;
        }
        return threadMXBean.getThreadInfo(threadIds, Integer.MAX_VALUE);
    }

    private static void header(StringBuilder s) {
        s.append(System.getProperty("java.vm.name"));
        s.append(" (");
        s.append(System.getProperty("java.vm.version"));
        s.append(" ");
        s.append(System.getProperty("java.vm.info"));
        s.append("):");
        s.append("\n\n");
    }

    private static void appendThreadInfos(ThreadInfo[] infos, StringBuilder s) {
        if (infos == null || infos.length == 0) {
            return;
        }
        for (ThreadInfo info : infos) {
            s.append(info);
        }
    }
}

