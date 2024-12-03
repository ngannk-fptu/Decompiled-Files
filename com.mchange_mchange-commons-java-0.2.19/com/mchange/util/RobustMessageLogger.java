/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util;

import com.mchange.util.MessageLogger;

public interface RobustMessageLogger
extends MessageLogger {
    @Override
    public void log(String var1);

    @Override
    public void log(Throwable var1, String var2);
}

