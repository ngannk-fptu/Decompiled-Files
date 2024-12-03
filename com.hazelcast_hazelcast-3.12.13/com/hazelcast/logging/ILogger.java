/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.logging;

import com.hazelcast.logging.LogEvent;
import java.util.logging.Level;

public interface ILogger {
    public void finest(String var1);

    public void finest(Throwable var1);

    public void finest(String var1, Throwable var2);

    public boolean isFinestEnabled();

    public void fine(String var1);

    public void fine(Throwable var1);

    public void fine(String var1, Throwable var2);

    public boolean isFineEnabled();

    public void info(String var1);

    public void info(Throwable var1);

    public void info(String var1, Throwable var2);

    public boolean isInfoEnabled();

    public void warning(String var1);

    public void warning(Throwable var1);

    public void warning(String var1, Throwable var2);

    public boolean isWarningEnabled();

    public void severe(String var1);

    public void severe(Throwable var1);

    public void severe(String var1, Throwable var2);

    public boolean isSevereEnabled();

    public void log(Level var1, String var2);

    public void log(Level var1, String var2, Throwable var3);

    public void log(LogEvent var1);

    public Level getLevel();

    public boolean isLoggable(Level var1);
}

