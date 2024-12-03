/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import org.apache.velocity.runtime.RuntimeServices;

public interface LogChute {
    public static final String TRACE_PREFIX = " [trace] ";
    public static final String DEBUG_PREFIX = " [debug] ";
    public static final String INFO_PREFIX = "  [info] ";
    public static final String WARN_PREFIX = "  [warn] ";
    public static final String ERROR_PREFIX = " [error] ";
    public static final int TRACE_ID = -1;
    public static final int DEBUG_ID = 0;
    public static final int INFO_ID = 1;
    public static final int WARN_ID = 2;
    public static final int ERROR_ID = 3;

    public void init(RuntimeServices var1) throws Exception;

    public void log(int var1, String var2);

    public void log(int var1, String var2, Throwable var3);

    public boolean isLevelEnabled(int var1);
}

