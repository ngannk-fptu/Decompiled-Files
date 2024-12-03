/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import java.io.IOException;

public interface MessageLogger {
    public void log(String var1) throws IOException;

    public void log(Throwable var1, String var2) throws IOException;
}

