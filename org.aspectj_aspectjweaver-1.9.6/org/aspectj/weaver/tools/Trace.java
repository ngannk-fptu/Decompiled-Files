/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

public interface Trace {
    public void enter(String var1, Object var2, Object[] var3);

    public void enter(String var1, Object var2);

    public void exit(String var1, Object var2);

    public void exit(String var1, Throwable var2);

    public void exit(String var1);

    public void event(String var1);

    public void event(String var1, Object var2, Object[] var3);

    public void debug(String var1);

    public void info(String var1);

    public void warn(String var1);

    public void warn(String var1, Throwable var2);

    public void error(String var1);

    public void error(String var1, Throwable var2);

    public void fatal(String var1);

    public void fatal(String var1, Throwable var2);

    public void enter(String var1, Object var2, Object var3);

    public void enter(String var1, Object var2, boolean var3);

    public void exit(String var1, boolean var2);

    public void exit(String var1, int var2);

    public void event(String var1, Object var2, Object var3);

    public boolean isTraceEnabled();

    public void setTraceEnabled(boolean var1);
}

