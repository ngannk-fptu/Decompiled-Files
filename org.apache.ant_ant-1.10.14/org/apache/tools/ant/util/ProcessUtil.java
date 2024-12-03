/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.lang.management.ManagementFactory;

public class ProcessUtil {
    private ProcessUtil() {
    }

    public static String getProcessId(String fallback) {
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        int index = jvmName.indexOf(64);
        if (index < 1) {
            return fallback;
        }
        try {
            return Long.toString(Long.parseLong(jvmName.substring(0, index)));
        }
        catch (NumberFormatException numberFormatException) {
            return fallback;
        }
    }

    public static void main(String[] args) {
        System.out.println(ProcessUtil.getProcessId("<PID>"));
        try {
            Thread.sleep(120000L);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

