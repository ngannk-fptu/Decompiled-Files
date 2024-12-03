/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.extras.common.log;

import com.atlassian.extras.common.log.Logger;
import java.io.PrintStream;

class StdErrLogger
implements Logger.Log {
    private final Logger.Level level;
    private static final PrintStream PRINT_STREAM = System.err;

    StdErrLogger() {
        this(Logger.Level.INFO);
    }

    StdErrLogger(Logger.Level level) {
        this.level = level;
    }

    @Override
    public void setClass(Class clazz) {
    }

    @Override
    public void debug(Object msg) {
        if (this.level.compareTo(Logger.Level.DEBUG) <= 0) {
            PRINT_STREAM.println(msg);
        }
    }

    @Override
    public void debug(Object msg, Throwable t) {
        if (this.level.compareTo(Logger.Level.DEBUG) <= 0) {
            PRINT_STREAM.println(msg);
            t.printStackTrace(PRINT_STREAM);
        }
    }

    @Override
    public void info(Object msg) {
        if (this.level.compareTo(Logger.Level.INFO) <= 0) {
            PRINT_STREAM.println(msg);
        }
    }

    @Override
    public void info(Object msg, Throwable t) {
        if (this.level.compareTo(Logger.Level.INFO) <= 0) {
            PRINT_STREAM.println(msg);
            t.printStackTrace(PRINT_STREAM);
        }
    }

    @Override
    public void warn(Object msg) {
        if (this.level.compareTo(Logger.Level.WARN) <= 0) {
            PRINT_STREAM.println(msg);
        }
    }

    @Override
    public void warn(Object msg, Throwable t) {
        if (this.level.compareTo(Logger.Level.WARN) <= 0) {
            PRINT_STREAM.println(msg);
            t.printStackTrace(PRINT_STREAM);
        }
    }

    @Override
    public void error(Object msg) {
        if (this.level.compareTo(Logger.Level.ERROR) <= 0) {
            PRINT_STREAM.println(msg);
        }
    }

    @Override
    public void error(Object msg, Throwable t) {
        if (this.level.compareTo(Logger.Level.ERROR) <= 0) {
            PRINT_STREAM.println(msg);
            t.printStackTrace(PRINT_STREAM);
        }
    }

    public void error(Throwable t) {
        if (this.level.compareTo(Logger.Level.ERROR) <= 0) {
            t.printStackTrace(PRINT_STREAM);
        }
    }

    @Override
    public void fatal(Object msg) {
        if (this.level.compareTo(Logger.Level.FATAL) <= 0) {
            PRINT_STREAM.println(msg);
        }
    }

    @Override
    public void fatal(Object msg, Throwable t) {
        if (this.level.compareTo(Logger.Level.FATAL) <= 0) {
            PRINT_STREAM.println(msg);
            t.printStackTrace(PRINT_STREAM);
        }
    }

    public void fatal(Throwable t) {
        if (this.level.compareTo(Logger.Level.FATAL) <= 0) {
            t.printStackTrace(PRINT_STREAM);
        }
    }
}

