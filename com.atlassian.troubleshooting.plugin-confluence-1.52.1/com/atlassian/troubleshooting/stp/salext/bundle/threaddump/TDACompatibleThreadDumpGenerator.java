/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Ordering
 *  com.google.common.primitives.Longs
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.bundle.threaddump;

import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.threaddump.ThreadDumpGenerator;
import com.atlassian.troubleshooting.stp.salext.bundle.threaddump.ThreadHelper;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TDACompatibleThreadDumpGenerator
implements ThreadDumpGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(TDACompatibleThreadDumpGenerator.class);
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Ordering<Thread> THREAD_ID_ORDERING = Ordering.from((o1, o2) -> Long.valueOf(o1.getId()).compareTo(o2.getId()));
    private static final Function<Thread, Long> GET_THREAD_ID = thread -> thread.getId();
    private final ThreadMXBean threadMXBean;
    private final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

    public TDACompatibleThreadDumpGenerator() {
        Preconditions.checkState((this.runtimeMXBean != null ? 1 : 0) != 0, (Object)"No thread dump facility available.");
        this.threadMXBean = ManagementFactory.getThreadMXBean();
        Preconditions.checkState((this.threadMXBean != null ? 1 : 0) != 0, (Object)"No thread dump facility available.");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void generateThreadDump(OutputStream output, SupportApplicationInfo info) throws IOException {
        PrintWriter writer = new PrintWriter(output);
        try {
            ThreadInfo[] threadInfoArray;
            this.printThreadDumpHeader(writer);
            Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
            ImmutableMap threadMap = Maps.uniqueIndex(stackTraces.keySet(), GET_THREAD_ID);
            long[] threadIdArray = Longs.toArray((Collection)Ordering.natural().reverse().sortedCopy(Iterables.transform(stackTraces.keySet(), GET_THREAD_ID)));
            for (ThreadInfo threadInfo : threadInfoArray = this.threadMXBean.getThreadInfo(threadIdArray, this.threadMXBean.isObjectMonitorUsageSupported(), this.threadMXBean.isSynchronizerUsageSupported())) {
                if (threadInfo == null) continue;
                long threadId = threadInfo.getThreadId();
                Thread thread = (Thread)threadMap.get(threadId);
                if (thread != null) {
                    this.printThreadInfo(writer, threadInfo, thread);
                    this.printThreadState(writer, threadInfo);
                    this.printStackTrace(writer, threadInfo);
                    writer.append("\n");
                    this.printLockedOwnableSynchronizers(writer, threadInfo);
                    writer.append("\n");
                    continue;
                }
                LOG.info("Unable to find thread with id " + threadId);
            }
            writer.append("\"VM Periodic Task Thread\" prio=10 tid=0x0000000000000000 nid=0 fake entry so TDA can understand where thread dump ends\n");
            writer.append("\n");
        }
        finally {
            writer.flush();
        }
    }

    private void printThreadDumpHeader(Appendable appendable) throws IOException {
        appendable.append(DATE_FORMAT.format(new Date())).append("\n").append("Full thread dump ").append(this.runtimeMXBean.getVmName()).append(" (").append(this.runtimeMXBean.getVmVersion()).append("):").append("\n").append("\n");
    }

    private void printThreadInfo(Appendable appendable, ThreadInfo ti, Thread t) throws IOException {
        appendable.append('\"').append(ti.getThreadName()).append('\"').append(" ").append(t.isDaemon() ? "daemon " : "").append("prio=").append(Integer.toString(t.getPriority())).append(" ").append("tid=").append(this.toHexString(t.getId())).append(" ").append("nid=0").append(" ").append(ThreadHelper.getThreadState(ti)).append(" ");
        appendable.append("\n");
    }

    private void printThreadState(Appendable appendable, ThreadInfo ti) throws IOException {
        appendable.append("   ").append(((Object)((Object)ti.getThreadState())).getClass().getCanonicalName()).append(": ").append(ThreadHelper.getThreadStatusName(ti)).append("\n");
    }

    private void printStackTrace(Appendable a, ThreadInfo ti) throws IOException {
        StackTraceElement[] stackTrace = ti.getStackTrace();
        LockInfo lockInfo = ti.getLockInfo();
        for (int frameCount = 0; frameCount < stackTrace.length; ++frameCount) {
            StackTraceElement frame = stackTrace[frameCount];
            a.append("\t").append("at ").append(frame.toString()).append("\n");
            if (frameCount == 0) {
                this.printThreadLockInfo(a, ti, frame);
            }
            boolean foundFirstMonitor = false;
            for (MonitorInfo mi : ti.getLockedMonitors()) {
                if (mi.getLockedStackDepth() != frameCount) continue;
                if (!foundFirstMonitor && frameCount == 0 && ti.getThreadState() == Thread.State.BLOCKED && lockInfo != null) {
                    a.append("\t- waiting to lock ").append(this.lockInfoToString(lockInfo)).append("\n");
                }
                a.append("\t- locked ").append(this.lockInfoToString(mi)).append("\n");
                foundFirstMonitor = true;
            }
        }
    }

    private void printThreadLockInfo(Appendable a, ThreadInfo ti, StackTraceElement frame0) throws IOException {
        LockInfo lockInfo = ti.getLockInfo();
        if (lockInfo != null) {
            switch (ti.getThreadState()) {
                case BLOCKED: {
                    a.append("\t- waiting to lock ").append(this.lockInfoToString(lockInfo)).append("\n");
                    this.printThreadLockOwner(a, ti);
                    break;
                }
                case WAITING: 
                case TIMED_WAITING: {
                    if (ThreadHelper.isObjectWait(frame0)) {
                        a.append("\t- waiting on ").append(this.lockInfoToString(lockInfo)).append("\n");
                    } else {
                        a.append("\t- parking to wait for ").append(this.lockInfoToString(lockInfo)).append("\n");
                    }
                    this.printThreadLockOwner(a, ti);
                    break;
                }
                default: {
                    LOG.warn("Unrecognized thread {} state {} for which lock info is not empty", (Object)this.toHexString(ti.getThreadId()), (Object)ti.getThreadState());
                }
            }
        }
    }

    private void printThreadLockOwner(Appendable a, ThreadInfo ti) throws IOException {
        if (ti.getLockOwnerName() != null) {
            a.append("\t owned by ").append(ti.getLockOwnerName()).append(" id=").append(this.toHexString(ti.getLockOwnerId())).append("\n");
        }
    }

    private String lockInfoToString(LockInfo li) {
        return String.format("<%s> (a %s)", this.toHexString(li.getIdentityHashCode()), li.getClassName());
    }

    private void printLockedOwnableSynchronizers(Appendable a, ThreadInfo ti) throws IOException {
        a.append("   Locked ownable synchronizers:").append("\n");
        LockInfo[] lockedSynchronizers = ti.getLockedSynchronizers();
        if (lockedSynchronizers != null && lockedSynchronizers.length > 0) {
            for (LockInfo li : lockedSynchronizers) {
                a.append("\t").append("- ").append(li.toString()).append("\n");
            }
        } else {
            a.append("\t").append("- None").append("\n");
        }
    }

    private String toHexString(Integer value) {
        return "0x" + StringUtils.leftPad((String)Integer.toHexString(value), (int)16, (char)'0');
    }

    private String toHexString(Long value) {
        return "0x" + StringUtils.leftPad((String)Long.toHexString(value), (int)16, (char)'0');
    }
}

