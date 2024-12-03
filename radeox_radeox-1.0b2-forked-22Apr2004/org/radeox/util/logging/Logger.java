/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.util.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.radeox.util.logging.LogHandler;
import org.radeox.util.logging.SystemErrLogger;

public class Logger {
    private static LogHandler handler = new SystemErrLogger();
    public static final boolean PRINT_STACKTRACE = true;
    public static final DateFormat format = new SimpleDateFormat("hh:mm:ss.SSS");
    public static final int ALL = -1;
    public static final int NONE = 4;
    public static final int PERF = 0;
    public static final int DEBUG = 1;
    public static final int WARN = 2;
    public static final int FATAL = 3;
    public static final int LEVEL = -1;
    public static final String[] levels = new String[]{"PERF  ", "DEBUG ", "WARN  ", "FATAL "};

    public static void log(String output) {
        Logger.log(1, output);
    }

    public static void perf(String output) {
        Logger.log(0, output);
    }

    public static void debug(String output) {
        Logger.log(1, output);
    }

    public static void warn(String output) {
        Logger.log(2, output);
    }

    public static void warn(String output, Throwable e) {
        Logger.log(2, output, e);
    }

    public static void fatal(String output) {
        Logger.log(2, output);
    }

    public static void fatal(String output, Exception e) {
        Logger.log(2, output, e);
    }

    public static void log(String output, Exception e) {
        Logger.log(1, output, e);
    }

    public static void log(int level, String output) {
        handler.log(format.format(new Date()) + " " + levels[level] + " - " + output);
    }

    public static void log(int level, String output, Throwable e) {
        handler.log(format.format(new Date()) + " " + levels[level] + " - " + output + ": " + e.getMessage(), e);
    }

    public static void setHandler(LogHandler handler) {
        Logger.handler = handler;
    }
}

