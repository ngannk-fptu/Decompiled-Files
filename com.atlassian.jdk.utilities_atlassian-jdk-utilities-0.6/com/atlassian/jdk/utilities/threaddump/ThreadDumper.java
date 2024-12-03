/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.jdk.utilities.threaddump;

import java.util.Map;
import org.apache.log4j.Logger;

class ThreadDumper {
    private static final long MEGABYTE = 0x100000L;
    private static Logger log = Logger.getLogger(ThreadDumper.class);

    ThreadDumper() {
    }

    void printThreadDump() {
        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        long timeStamp = System.currentTimeMillis();
        log.error((Object)("************* Start Thread Dump " + timeStamp + " *******************"));
        log.error((Object)" -- Memory Details --");
        long totalMemory = Runtime.getRuntime().totalMemory() / 0x100000L;
        log.error((Object)("Total Memory = " + totalMemory + "MB"));
        long freeMemory = Runtime.getRuntime().freeMemory() / 0x100000L;
        log.error((Object)("Used Memory = " + (totalMemory - freeMemory) + "MB"));
        log.error((Object)("Free Memory = " + freeMemory + "MB"));
        log.error((Object)" --- --- --- ---");
        for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stackTraceElement = entry.getValue();
            log.error((Object)("Thread= " + thread.getName() + " " + (thread.isDaemon() ? "daemon" : "") + " prio=" + thread.getPriority() + "id=" + thread.getId() + " " + (Object)((Object)thread.getState())));
            for (int i = 0; i <= stackTraceElement.length - 1; ++i) {
                log.error((Object)("\t" + stackTraceElement[i]));
            }
            log.error((Object)" --- --- --- ---");
        }
        log.error((Object)("************* End Thread Dump " + timeStamp + " *******************"));
    }
}

