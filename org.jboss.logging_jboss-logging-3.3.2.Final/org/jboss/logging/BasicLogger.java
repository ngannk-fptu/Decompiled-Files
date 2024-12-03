/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import org.jboss.logging.Logger;

public interface BasicLogger {
    public boolean isEnabled(Logger.Level var1);

    public boolean isTraceEnabled();

    public void trace(Object var1);

    public void trace(Object var1, Throwable var2);

    public void trace(String var1, Object var2, Throwable var3);

    public void trace(String var1, Object var2, Object[] var3, Throwable var4);

    public void tracev(String var1, Object ... var2);

    public void tracev(String var1, Object var2);

    public void tracev(String var1, Object var2, Object var3);

    public void tracev(String var1, Object var2, Object var3, Object var4);

    public void tracev(Throwable var1, String var2, Object ... var3);

    public void tracev(Throwable var1, String var2, Object var3);

    public void tracev(Throwable var1, String var2, Object var3, Object var4);

    public void tracev(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void tracef(String var1, Object ... var2);

    public void tracef(String var1, Object var2);

    public void tracef(String var1, Object var2, Object var3);

    public void tracef(String var1, Object var2, Object var3, Object var4);

    public void tracef(Throwable var1, String var2, Object ... var3);

    public void tracef(Throwable var1, String var2, Object var3);

    public void tracef(Throwable var1, String var2, Object var3, Object var4);

    public void tracef(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void tracef(String var1, int var2);

    public void tracef(String var1, int var2, int var3);

    public void tracef(String var1, int var2, Object var3);

    public void tracef(String var1, int var2, int var3, int var4);

    public void tracef(String var1, int var2, int var3, Object var4);

    public void tracef(String var1, int var2, Object var3, Object var4);

    public void tracef(Throwable var1, String var2, int var3);

    public void tracef(Throwable var1, String var2, int var3, int var4);

    public void tracef(Throwable var1, String var2, int var3, Object var4);

    public void tracef(Throwable var1, String var2, int var3, int var4, int var5);

    public void tracef(Throwable var1, String var2, int var3, int var4, Object var5);

    public void tracef(Throwable var1, String var2, int var3, Object var4, Object var5);

    public void tracef(String var1, long var2);

    public void tracef(String var1, long var2, long var4);

    public void tracef(String var1, long var2, Object var4);

    public void tracef(String var1, long var2, long var4, long var6);

    public void tracef(String var1, long var2, long var4, Object var6);

    public void tracef(String var1, long var2, Object var4, Object var5);

    public void tracef(Throwable var1, String var2, long var3);

    public void tracef(Throwable var1, String var2, long var3, long var5);

    public void tracef(Throwable var1, String var2, long var3, Object var5);

    public void tracef(Throwable var1, String var2, long var3, long var5, long var7);

    public void tracef(Throwable var1, String var2, long var3, long var5, Object var7);

    public void tracef(Throwable var1, String var2, long var3, Object var5, Object var6);

    public boolean isDebugEnabled();

    public void debug(Object var1);

    public void debug(Object var1, Throwable var2);

    public void debug(String var1, Object var2, Throwable var3);

    public void debug(String var1, Object var2, Object[] var3, Throwable var4);

    public void debugv(String var1, Object ... var2);

    public void debugv(String var1, Object var2);

    public void debugv(String var1, Object var2, Object var3);

    public void debugv(String var1, Object var2, Object var3, Object var4);

    public void debugv(Throwable var1, String var2, Object ... var3);

    public void debugv(Throwable var1, String var2, Object var3);

    public void debugv(Throwable var1, String var2, Object var3, Object var4);

    public void debugv(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void debugf(String var1, Object ... var2);

    public void debugf(String var1, Object var2);

    public void debugf(String var1, Object var2, Object var3);

    public void debugf(String var1, Object var2, Object var3, Object var4);

    public void debugf(Throwable var1, String var2, Object ... var3);

    public void debugf(Throwable var1, String var2, Object var3);

    public void debugf(Throwable var1, String var2, Object var3, Object var4);

    public void debugf(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void debugf(String var1, int var2);

    public void debugf(String var1, int var2, int var3);

    public void debugf(String var1, int var2, Object var3);

    public void debugf(String var1, int var2, int var3, int var4);

    public void debugf(String var1, int var2, int var3, Object var4);

    public void debugf(String var1, int var2, Object var3, Object var4);

    public void debugf(Throwable var1, String var2, int var3);

    public void debugf(Throwable var1, String var2, int var3, int var4);

    public void debugf(Throwable var1, String var2, int var3, Object var4);

    public void debugf(Throwable var1, String var2, int var3, int var4, int var5);

    public void debugf(Throwable var1, String var2, int var3, int var4, Object var5);

    public void debugf(Throwable var1, String var2, int var3, Object var4, Object var5);

    public void debugf(String var1, long var2);

    public void debugf(String var1, long var2, long var4);

    public void debugf(String var1, long var2, Object var4);

    public void debugf(String var1, long var2, long var4, long var6);

    public void debugf(String var1, long var2, long var4, Object var6);

    public void debugf(String var1, long var2, Object var4, Object var5);

    public void debugf(Throwable var1, String var2, long var3);

    public void debugf(Throwable var1, String var2, long var3, long var5);

    public void debugf(Throwable var1, String var2, long var3, Object var5);

    public void debugf(Throwable var1, String var2, long var3, long var5, long var7);

    public void debugf(Throwable var1, String var2, long var3, long var5, Object var7);

    public void debugf(Throwable var1, String var2, long var3, Object var5, Object var6);

    public boolean isInfoEnabled();

    public void info(Object var1);

    public void info(Object var1, Throwable var2);

    public void info(String var1, Object var2, Throwable var3);

    public void info(String var1, Object var2, Object[] var3, Throwable var4);

    public void infov(String var1, Object ... var2);

    public void infov(String var1, Object var2);

    public void infov(String var1, Object var2, Object var3);

    public void infov(String var1, Object var2, Object var3, Object var4);

    public void infov(Throwable var1, String var2, Object ... var3);

    public void infov(Throwable var1, String var2, Object var3);

    public void infov(Throwable var1, String var2, Object var3, Object var4);

    public void infov(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void infof(String var1, Object ... var2);

    public void infof(String var1, Object var2);

    public void infof(String var1, Object var2, Object var3);

    public void infof(String var1, Object var2, Object var3, Object var4);

    public void infof(Throwable var1, String var2, Object ... var3);

    public void infof(Throwable var1, String var2, Object var3);

    public void infof(Throwable var1, String var2, Object var3, Object var4);

    public void infof(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void warn(Object var1);

    public void warn(Object var1, Throwable var2);

    public void warn(String var1, Object var2, Throwable var3);

    public void warn(String var1, Object var2, Object[] var3, Throwable var4);

    public void warnv(String var1, Object ... var2);

    public void warnv(String var1, Object var2);

    public void warnv(String var1, Object var2, Object var3);

    public void warnv(String var1, Object var2, Object var3, Object var4);

    public void warnv(Throwable var1, String var2, Object ... var3);

    public void warnv(Throwable var1, String var2, Object var3);

    public void warnv(Throwable var1, String var2, Object var3, Object var4);

    public void warnv(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void warnf(String var1, Object ... var2);

    public void warnf(String var1, Object var2);

    public void warnf(String var1, Object var2, Object var3);

    public void warnf(String var1, Object var2, Object var3, Object var4);

    public void warnf(Throwable var1, String var2, Object ... var3);

    public void warnf(Throwable var1, String var2, Object var3);

    public void warnf(Throwable var1, String var2, Object var3, Object var4);

    public void warnf(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void error(Object var1);

    public void error(Object var1, Throwable var2);

    public void error(String var1, Object var2, Throwable var3);

    public void error(String var1, Object var2, Object[] var3, Throwable var4);

    public void errorv(String var1, Object ... var2);

    public void errorv(String var1, Object var2);

    public void errorv(String var1, Object var2, Object var3);

    public void errorv(String var1, Object var2, Object var3, Object var4);

    public void errorv(Throwable var1, String var2, Object ... var3);

    public void errorv(Throwable var1, String var2, Object var3);

    public void errorv(Throwable var1, String var2, Object var3, Object var4);

    public void errorv(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void errorf(String var1, Object ... var2);

    public void errorf(String var1, Object var2);

    public void errorf(String var1, Object var2, Object var3);

    public void errorf(String var1, Object var2, Object var3, Object var4);

    public void errorf(Throwable var1, String var2, Object ... var3);

    public void errorf(Throwable var1, String var2, Object var3);

    public void errorf(Throwable var1, String var2, Object var3, Object var4);

    public void errorf(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void fatal(Object var1);

    public void fatal(Object var1, Throwable var2);

    public void fatal(String var1, Object var2, Throwable var3);

    public void fatal(String var1, Object var2, Object[] var3, Throwable var4);

    public void fatalv(String var1, Object ... var2);

    public void fatalv(String var1, Object var2);

    public void fatalv(String var1, Object var2, Object var3);

    public void fatalv(String var1, Object var2, Object var3, Object var4);

    public void fatalv(Throwable var1, String var2, Object ... var3);

    public void fatalv(Throwable var1, String var2, Object var3);

    public void fatalv(Throwable var1, String var2, Object var3, Object var4);

    public void fatalv(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void fatalf(String var1, Object ... var2);

    public void fatalf(String var1, Object var2);

    public void fatalf(String var1, Object var2, Object var3);

    public void fatalf(String var1, Object var2, Object var3, Object var4);

    public void fatalf(Throwable var1, String var2, Object ... var3);

    public void fatalf(Throwable var1, String var2, Object var3);

    public void fatalf(Throwable var1, String var2, Object var3, Object var4);

    public void fatalf(Throwable var1, String var2, Object var3, Object var4, Object var5);

    public void log(Logger.Level var1, Object var2);

    public void log(Logger.Level var1, Object var2, Throwable var3);

    public void log(Logger.Level var1, String var2, Object var3, Throwable var4);

    public void log(String var1, Logger.Level var2, Object var3, Object[] var4, Throwable var5);

    public void logv(Logger.Level var1, String var2, Object ... var3);

    public void logv(Logger.Level var1, String var2, Object var3);

    public void logv(Logger.Level var1, String var2, Object var3, Object var4);

    public void logv(Logger.Level var1, String var2, Object var3, Object var4, Object var5);

    public void logv(Logger.Level var1, Throwable var2, String var3, Object ... var4);

    public void logv(Logger.Level var1, Throwable var2, String var3, Object var4);

    public void logv(Logger.Level var1, Throwable var2, String var3, Object var4, Object var5);

    public void logv(Logger.Level var1, Throwable var2, String var3, Object var4, Object var5, Object var6);

    public void logv(String var1, Logger.Level var2, Throwable var3, String var4, Object ... var5);

    public void logv(String var1, Logger.Level var2, Throwable var3, String var4, Object var5);

    public void logv(String var1, Logger.Level var2, Throwable var3, String var4, Object var5, Object var6);

    public void logv(String var1, Logger.Level var2, Throwable var3, String var4, Object var5, Object var6, Object var7);

    public void logf(Logger.Level var1, String var2, Object ... var3);

    public void logf(Logger.Level var1, String var2, Object var3);

    public void logf(Logger.Level var1, String var2, Object var3, Object var4);

    public void logf(Logger.Level var1, String var2, Object var3, Object var4, Object var5);

    public void logf(Logger.Level var1, Throwable var2, String var3, Object ... var4);

    public void logf(Logger.Level var1, Throwable var2, String var3, Object var4);

    public void logf(Logger.Level var1, Throwable var2, String var3, Object var4, Object var5);

    public void logf(Logger.Level var1, Throwable var2, String var3, Object var4, Object var5, Object var6);

    public void logf(String var1, Logger.Level var2, Throwable var3, String var4, Object var5);

    public void logf(String var1, Logger.Level var2, Throwable var3, String var4, Object var5, Object var6);

    public void logf(String var1, Logger.Level var2, Throwable var3, String var4, Object var5, Object var6, Object var7);

    public void logf(String var1, Logger.Level var2, Throwable var3, String var4, Object ... var5);
}

