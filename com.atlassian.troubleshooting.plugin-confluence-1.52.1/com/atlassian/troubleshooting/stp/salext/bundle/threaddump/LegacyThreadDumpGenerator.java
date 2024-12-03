/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.bundle.threaddump;

import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.bundle.threaddump.ThreadDumpGenerator;
import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.MessageFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyThreadDumpGenerator
implements ThreadDumpGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(LegacyThreadDumpGenerator.class);
    private static final int MAX_THREAD_DEPTH = Integer.MAX_VALUE;
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public LegacyThreadDumpGenerator() {
        Preconditions.checkState((this.threadMXBean != null ? 1 : 0) != 0, (Object)"No thread dump facility available.");
    }

    @Override
    public void generateThreadDump(OutputStream output, SupportApplicationInfo info) throws IOException {
        PrintWriter a = new PrintWriter(output);
        a.append(MessageFormat.format("{0} {1} {2} {3}\n Thread dump taken on {4,date,medium} at {4,time,medium}:\n", info.getApplicationName(), info.getApplicationVersion(), info.getApplicationBuildDate(), info.getApplicationBuildNumber(), new Date()));
        ThreadInfo[] threadInfo = this.threadMXBean.getThreadInfo(this.threadMXBean.getAllThreadIds(), Integer.MAX_VALUE);
        if (threadInfo == null || threadInfo.length == 0) {
            a.append("No thread information was generated.");
            return;
        }
        for (ThreadInfo ti : threadInfo) {
            if (ti == null) continue;
            a.append("[").append("" + ti.getThreadId()).append("] ").append(ti.getThreadName()).append(": ").append(ti.getThreadState().toString());
            if (ti.getLockName() != null) {
                a.append(" (waiting on ").append(ti.getLockName().trim());
                if (ti.getLockOwnerId() != -1L) {
                    a.append(" held by ").append("" + ti.getLockOwnerId());
                }
                a.append(")");
            }
            a.append("\n");
            for (StackTraceElement ste : ti.getStackTrace()) {
                this.printStackTraceElement(a, ste);
            }
            a.append("\n");
        }
        a.flush();
    }

    private void printStackTraceElement(Appendable a, StackTraceElement element) throws IOException {
        a.append("\t").append(element.toString()).append("\n");
    }
}

