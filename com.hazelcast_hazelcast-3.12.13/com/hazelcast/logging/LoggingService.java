/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.logging;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LogListener;
import java.util.logging.Level;

public interface LoggingService {
    public void addLogListener(Level var1, LogListener var2);

    public void removeLogListener(LogListener var1);

    public ILogger getLogger(String var1);

    public ILogger getLogger(Class var1);
}

