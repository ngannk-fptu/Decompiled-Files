/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.salext.bundle.threaddump;

import java.lang.management.ThreadInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadHelper {
    private static final Logger LOG = LoggerFactory.getLogger(ThreadHelper.class);

    public static String getThreadState(ThreadInfo ti) {
        StackTraceElement topOfStackTrace = ThreadHelper.getTopOfStackTrace(ti);
        switch (ti.getThreadState()) {
            case RUNNABLE: {
                return "runnable";
            }
            case BLOCKED: {
                return "waiting for monitor entry";
            }
            case WAITING: {
                if (ThreadHelper.isObjectWait(topOfStackTrace)) {
                    return "in Object.wait()";
                }
                return "waiting on condition";
            }
            case TIMED_WAITING: {
                if (ThreadHelper.isThreadSleep(topOfStackTrace)) {
                    return "sleeping";
                }
                if (ThreadHelper.isObjectWait(topOfStackTrace)) {
                    return "in Object.wait()";
                }
                return "waiting on condition";
            }
        }
        return "thread-state";
    }

    public static String getThreadStatusName(ThreadInfo ti) {
        StackTraceElement topOfStackTrace = ThreadHelper.getTopOfStackTrace(ti);
        switch (ti.getThreadState()) {
            case NEW: {
                return "NEW";
            }
            case RUNNABLE: {
                return "RUNNABLE";
            }
            case BLOCKED: {
                return "BLOCKED (on object monitor)";
            }
            case WAITING: {
                if (ThreadHelper.isObjectWait(topOfStackTrace)) {
                    return "WAITING (on object monitor)";
                }
                return "WAITING (parking)";
            }
            case TIMED_WAITING: {
                if (ThreadHelper.isThreadSleep(topOfStackTrace)) {
                    return "TIMED_WAITING (sleeping)";
                }
                if (ThreadHelper.isObjectWait(topOfStackTrace)) {
                    return "TIMED_WAITING (on object monitor)";
                }
                return "TIMED_WAITING (parking)";
            }
            case TERMINATED: {
                return "TERMINATED";
            }
        }
        return "UNKNOWN";
    }

    public static boolean isObjectWait(StackTraceElement element) {
        return element != null && Object.class.getName().equals(element.getClassName()) && "wait".equals(element.getMethodName());
    }

    public static boolean isThreadSleep(StackTraceElement element) {
        return element != null && Thread.class.getName().equals(element.getClassName()) && "sleep".equals(element.getMethodName());
    }

    public static StackTraceElement getTopOfStackTrace(ThreadInfo ti) {
        StackTraceElement[] stackTrace = ti.getStackTrace();
        return stackTrace.length > 0 ? stackTrace[0] : null;
    }
}

