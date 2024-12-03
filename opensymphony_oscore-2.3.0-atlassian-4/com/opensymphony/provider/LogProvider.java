/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.provider;

import com.opensymphony.provider.Provider;

public interface LogProvider
extends Provider {
    public static final int DEBUG = 1;
    public static final int ERROR = 4;
    public static final int FATAL = 5;
    public static final int INFO = 2;
    public static final int WARN = 3;

    public Object getContext(String var1);

    public boolean isEnabled(Object var1, int var2);

    public void log(Object var1, int var2, Object var3, Throwable var4);
}

