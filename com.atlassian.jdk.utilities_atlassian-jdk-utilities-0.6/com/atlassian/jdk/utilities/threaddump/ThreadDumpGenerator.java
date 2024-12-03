/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.log4j.Logger
 */
package com.atlassian.jdk.utilities.threaddump;

import com.atlassian.jdk.utilities.threaddump.ThreadDumper;
import org.apache.log4j.Logger;

public class ThreadDumpGenerator {
    private static Logger LOG = Logger.getLogger(ThreadDumpGenerator.class);

    public static void generateThreadDump() {
        ThreadDumpGenerator.printThreadDump(new ThreadDumper());
    }

    static void printThreadDump(ThreadDumper threadDumper) {
        try {
            threadDumper.printThreadDump();
        }
        catch (Throwable t) {
            LOG.error((Object)"Error occurred while generating thread dump.", t);
        }
    }
}

