/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.jfree.util.LogContext;
import org.jfree.util.LogTarget;

public class Log {
    private int debuglevel = 100;
    private LogTarget[] logTargets;
    private HashMap logContexts = new HashMap();
    private static Log singleton;

    protected Log() {
        this.logTargets = new LogTarget[0];
    }

    public static synchronized Log getInstance() {
        if (singleton == null) {
            singleton = new Log();
        }
        return singleton;
    }

    protected static synchronized void defineLog(Log log) {
        singleton = log;
    }

    public int getDebuglevel() {
        return this.debuglevel;
    }

    protected void setDebuglevel(int debuglevel) {
        this.debuglevel = debuglevel;
    }

    public synchronized void addTarget(LogTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        LogTarget[] data = new LogTarget[this.logTargets.length + 1];
        System.arraycopy(this.logTargets, 0, data, 0, this.logTargets.length);
        data[this.logTargets.length] = target;
        this.logTargets = data;
    }

    public synchronized void removeTarget(LogTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        ArrayList<LogTarget> l = new ArrayList<LogTarget>();
        l.addAll(Arrays.asList(this.logTargets));
        l.remove(target);
        LogTarget[] targets = new LogTarget[l.size()];
        this.logTargets = l.toArray(targets);
    }

    public LogTarget[] getTargets() {
        return (LogTarget[])this.logTargets.clone();
    }

    public synchronized void replaceTargets(LogTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        this.logTargets = new LogTarget[]{target};
    }

    public static void debug(Object message) {
        Log.log(3, message);
    }

    public static void debug(Object message, Exception e) {
        Log.log(3, message, e);
    }

    public static void info(Object message) {
        Log.log(2, message);
    }

    public static void info(Object message, Exception e) {
        Log.log(2, message, e);
    }

    public static void warn(Object message) {
        Log.log(1, message);
    }

    public static void warn(Object message, Exception e) {
        Log.log(1, message, e);
    }

    public static void error(Object message) {
        Log.log(0, message);
    }

    public static void error(Object message, Exception e) {
        Log.log(0, message, e);
    }

    protected void doLog(int level, Object message) {
        if (level > 3) {
            level = 3;
        }
        if (level <= this.debuglevel) {
            for (int i = 0; i < this.logTargets.length; ++i) {
                LogTarget t = this.logTargets[i];
                t.log(level, message);
            }
        }
    }

    public static void log(int level, Object message) {
        Log.getInstance().doLog(level, message);
    }

    public static void log(int level, Object message, Exception e) {
        Log.getInstance().doLog(level, message, e);
    }

    protected void doLog(int level, Object message, Exception e) {
        if (level > 3) {
            level = 3;
        }
        if (level <= this.debuglevel) {
            for (int i = 0; i < this.logTargets.length; ++i) {
                LogTarget t = this.logTargets[i];
                t.log(level, message, e);
            }
        }
    }

    public void init() {
    }

    public static boolean isDebugEnabled() {
        return Log.getInstance().getDebuglevel() >= 3;
    }

    public static boolean isInfoEnabled() {
        return Log.getInstance().getDebuglevel() >= 2;
    }

    public static boolean isWarningEnabled() {
        return Log.getInstance().getDebuglevel() >= 1;
    }

    public static boolean isErrorEnabled() {
        return Log.getInstance().getDebuglevel() >= 0;
    }

    public static LogContext createContext(Class context) {
        return Log.createContext(context.getName());
    }

    public static LogContext createContext(String context) {
        return Log.getInstance().internalCreateContext(context);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected LogContext internalCreateContext(String context) {
        Log log = this;
        synchronized (log) {
            LogContext ctx = (LogContext)this.logContexts.get(context);
            if (ctx == null) {
                ctx = new LogContext(context);
                this.logContexts.put(context, ctx);
            }
            return ctx;
        }
    }

    public static class SimpleMessage {
        private String message;
        private Object[] param;

        public SimpleMessage(String message, Object param1) {
            this.message = message;
            this.param = new Object[]{param1};
        }

        public SimpleMessage(String message, Object param1, Object param2) {
            this.message = message;
            this.param = new Object[]{param1, param2};
        }

        public SimpleMessage(String message, Object param1, Object param2, Object param3) {
            this.message = message;
            this.param = new Object[]{param1, param2, param3};
        }

        public SimpleMessage(String message, Object param1, Object param2, Object param3, Object param4) {
            this.message = message;
            this.param = new Object[]{param1, param2, param3, param4};
        }

        public SimpleMessage(String message, Object[] param) {
            this.message = message;
            this.param = param;
        }

        public String toString() {
            StringBuffer b = new StringBuffer();
            b.append(this.message);
            if (this.param != null) {
                for (int i = 0; i < this.param.length; ++i) {
                    b.append(this.param[i]);
                }
            }
            return b.toString();
        }
    }
}

