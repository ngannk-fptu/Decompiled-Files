/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.logging;

public interface Log {
    public boolean isFatalEnabled();

    public boolean isErrorEnabled();

    public boolean isWarnEnabled();

    public boolean isInfoEnabled();

    public boolean isDebugEnabled();

    public boolean isTraceEnabled();

    public void fatal(Object var1);

    public void fatal(Object var1, Throwable var2);

    public void error(Object var1);

    public void error(Object var1, Throwable var2);

    public void warn(Object var1);

    public void warn(Object var1, Throwable var2);

    public void info(Object var1);

    public void info(Object var1, Throwable var2);

    public void debug(Object var1);

    public void debug(Object var1, Throwable var2);

    public void trace(Object var1);

    public void trace(Object var1, Throwable var2);
}

