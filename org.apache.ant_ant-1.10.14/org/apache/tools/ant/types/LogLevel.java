/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import org.apache.tools.ant.types.EnumeratedAttribute;

public class LogLevel
extends EnumeratedAttribute {
    public static final LogLevel ERR = new LogLevel("error");
    public static final LogLevel WARN = new LogLevel("warn");
    public static final LogLevel INFO = new LogLevel("info");
    public static final LogLevel VERBOSE = new LogLevel("verbose");
    public static final LogLevel DEBUG = new LogLevel("debug");
    private static int[] levels = new int[]{0, 1, 1, 2, 3, 4};

    public LogLevel() {
    }

    private LogLevel(String value) {
        this();
        this.setValue(value);
    }

    @Override
    public String[] getValues() {
        return new String[]{"error", "warn", "warning", "info", "verbose", "debug"};
    }

    public int getLevel() {
        return levels[this.getIndex()];
    }
}

