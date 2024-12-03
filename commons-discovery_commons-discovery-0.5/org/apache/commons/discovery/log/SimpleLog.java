/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.commons.discovery.log;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.logging.Log;

@Deprecated
public class SimpleLog
implements Log {
    public static final int LOG_LEVEL_TRACE = 1;
    public static final int LOG_LEVEL_DEBUG = 2;
    public static final int LOG_LEVEL_INFO = 3;
    public static final int LOG_LEVEL_WARN = 4;
    public static final int LOG_LEVEL_ERROR = 5;
    public static final int LOG_LEVEL_FATAL = 6;
    public static final int LOG_LEVEL_ALL = 0;
    public static final int LOG_LEVEL_OFF = 7;
    protected static final String PROP_LEVEL = "org.apache.commons.discovery.log.level";
    protected static boolean showLogName = false;
    protected static boolean showShortName = true;
    protected static boolean showDateTime = false;
    protected static DateFormat dateFormatter = null;
    protected static int logLevel = 3;
    private static PrintStream out = System.out;
    protected String logName = null;
    private String prefix = null;

    public static void setLevel(int currentLogLevel) {
        logLevel = currentLogLevel;
    }

    public static int getLevel() {
        return logLevel;
    }

    protected static boolean isLevelEnabled(int level) {
        return level >= SimpleLog.getLevel();
    }

    public SimpleLog(String name) {
        this.logName = name;
    }

    protected void log(int type, Object message, Throwable t) {
        StringBuffer buf = new StringBuffer();
        if (showDateTime) {
            buf.append(dateFormatter.format(new Date()));
            buf.append(" ");
        }
        switch (type) {
            case 1: {
                buf.append("[TRACE] ");
                break;
            }
            case 2: {
                buf.append("[DEBUG] ");
                break;
            }
            case 3: {
                buf.append("[INFO ] ");
                break;
            }
            case 4: {
                buf.append("[WARN ] ");
                break;
            }
            case 5: {
                buf.append("[ERROR] ");
                break;
            }
            case 6: {
                buf.append("[FATAL] ");
            }
        }
        if (showShortName) {
            if (this.prefix == null) {
                this.prefix = this.logName.substring(this.logName.lastIndexOf(".") + 1) + " - ";
                this.prefix = this.prefix.substring(this.prefix.lastIndexOf("/") + 1) + "-";
            }
            buf.append(this.prefix);
        } else if (showLogName) {
            buf.append(String.valueOf(this.logName)).append(" - ");
        }
        buf.append(String.valueOf(message));
        if (t != null) {
            buf.append(" <");
            buf.append(t.toString());
            buf.append(">");
        }
        out.println(buf.toString());
        if (t != null) {
            t.printStackTrace(System.err);
        }
    }

    public final void debug(Object message) {
        if (SimpleLog.isLevelEnabled(2)) {
            this.log(2, message, null);
        }
    }

    public final void debug(Object message, Throwable t) {
        if (SimpleLog.isLevelEnabled(2)) {
            this.log(2, message, t);
        }
    }

    public final void trace(Object message) {
        if (SimpleLog.isLevelEnabled(1)) {
            this.log(1, message, null);
        }
    }

    public final void trace(Object message, Throwable t) {
        if (SimpleLog.isLevelEnabled(1)) {
            this.log(1, message, t);
        }
    }

    public final void info(Object message) {
        if (SimpleLog.isLevelEnabled(3)) {
            this.log(3, message, null);
        }
    }

    public final void info(Object message, Throwable t) {
        if (SimpleLog.isLevelEnabled(3)) {
            this.log(3, message, t);
        }
    }

    public final void warn(Object message) {
        if (SimpleLog.isLevelEnabled(4)) {
            this.log(4, message, null);
        }
    }

    public final void warn(Object message, Throwable t) {
        if (SimpleLog.isLevelEnabled(4)) {
            this.log(4, message, t);
        }
    }

    public final void error(Object message) {
        if (SimpleLog.isLevelEnabled(5)) {
            this.log(5, message, null);
        }
    }

    public final void error(Object message, Throwable t) {
        if (SimpleLog.isLevelEnabled(5)) {
            this.log(5, message, t);
        }
    }

    public final void fatal(Object message) {
        if (SimpleLog.isLevelEnabled(6)) {
            this.log(6, message, null);
        }
    }

    public final void fatal(Object message, Throwable t) {
        if (SimpleLog.isLevelEnabled(6)) {
            this.log(6, message, t);
        }
    }

    public final boolean isDebugEnabled() {
        return SimpleLog.isLevelEnabled(2);
    }

    public final boolean isErrorEnabled() {
        return SimpleLog.isLevelEnabled(5);
    }

    public final boolean isFatalEnabled() {
        return SimpleLog.isLevelEnabled(6);
    }

    public final boolean isInfoEnabled() {
        return SimpleLog.isLevelEnabled(3);
    }

    public final boolean isTraceEnabled() {
        return SimpleLog.isLevelEnabled(1);
    }

    public final boolean isWarnEnabled() {
        return SimpleLog.isLevelEnabled(4);
    }

    static {
        if (showDateTime) {
            dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS zzz");
        }
        try {
            String lvl = System.getProperty(PROP_LEVEL);
            if ("all".equalsIgnoreCase(lvl)) {
                SimpleLog.setLevel(0);
            } else if ("trace".equalsIgnoreCase(lvl)) {
                SimpleLog.setLevel(1);
            } else if ("debug".equalsIgnoreCase(lvl)) {
                SimpleLog.setLevel(2);
            } else if ("info".equalsIgnoreCase(lvl)) {
                SimpleLog.setLevel(3);
            } else if ("warn".equalsIgnoreCase(lvl)) {
                SimpleLog.setLevel(4);
            } else if ("error".equalsIgnoreCase(lvl)) {
                SimpleLog.setLevel(5);
            } else if ("fatal".equalsIgnoreCase(lvl)) {
                SimpleLog.setLevel(6);
            } else if ("off".equalsIgnoreCase(lvl)) {
                SimpleLog.setLevel(7);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
    }
}

