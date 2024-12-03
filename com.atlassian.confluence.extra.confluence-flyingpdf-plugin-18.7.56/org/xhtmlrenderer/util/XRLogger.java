/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.util.logging.Level;

public interface XRLogger {
    public void log(String var1, Level var2, String var3);

    public void log(String var1, Level var2, String var3, Throwable var4);

    public void setLevel(String var1, Level var2);
}

