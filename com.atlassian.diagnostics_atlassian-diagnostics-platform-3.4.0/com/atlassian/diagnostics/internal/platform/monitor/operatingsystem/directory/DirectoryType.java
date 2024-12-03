/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.directory;

public enum DirectoryType {
    HOME("HOME"),
    SHARED("SHARED"),
    UNKNOWN("UNKNOWN");

    public final String name;

    private DirectoryType(String name) {
        this.name = name;
    }
}

