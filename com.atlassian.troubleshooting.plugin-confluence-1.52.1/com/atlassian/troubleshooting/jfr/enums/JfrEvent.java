/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.enums;

public enum JfrEvent {
    THREAD_DUMP("jdk.ThreadDump"),
    THREAD_CPU_LOAD("jdk.ThreadCPULoad");

    private final String name;

    private JfrEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

