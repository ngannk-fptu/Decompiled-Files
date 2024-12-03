/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.logging;

public interface LogWriter {
    public long systemTime();

    public void enter(String var1, Object[] var2);

    public void leave(String var1, Object[] var2, Object var3);

    public void error(String var1, Object[] var2, Exception var3);
}

