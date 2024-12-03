/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.fusesource.jansi.Ansi
 *  org.fusesource.jansi.Ansi$Attribute
 *  org.fusesource.jansi.Ansi$Color
 */
package org.codehaus.groovy.tools.shell.util;

import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.util.Preferences;
import org.fusesource.jansi.Ansi;

public final class Logger {
    public static IO io;
    public final String name;
    private static final String DEBUG = "DEBUG";
    private static final String WARN = "WARN";
    private static final String ERROR = "ERROR";

    private Logger(String name) {
        assert (name != null);
        this.name = name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    private void log(String level, Object msg, Throwable cause) {
        assert (level != null);
        assert (msg != null);
        if (io == null) {
            Class<Logger> clazz = Logger.class;
            // MONITORENTER : org.codehaus.groovy.tools.shell.util.Logger.class
            if (io == null) {
                io = new IO();
            }
            // MONITOREXIT : clazz
        }
        if (cause == null && msg instanceof Throwable) {
            cause = (Throwable)msg;
            msg = cause.getMessage();
        }
        Ansi.Color color = Ansi.Color.GREEN;
        if (WARN.equals(level) || ERROR.equals(level)) {
            color = Ansi.Color.RED;
        }
        Logger.io.out.println(Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD).fg(color).a(level).reset().a(" [").a(this.name).a("] ").a(msg));
        if (cause != null) {
            cause.printStackTrace(Logger.io.out);
        }
        io.flush();
    }

    public boolean isDebugEnabled() {
        return Preferences.verbosity == IO.Verbosity.DEBUG;
    }

    public boolean isDebug() {
        return this.isDebugEnabled();
    }

    public void debug(Object msg) {
        if (this.isDebugEnabled()) {
            this.log(DEBUG, msg, null);
        }
    }

    public void debug(Object msg, Throwable cause) {
        if (this.isDebugEnabled()) {
            this.log(DEBUG, msg, cause);
        }
    }

    public void warn(Object msg) {
        this.log(WARN, msg, null);
    }

    public void warn(Object msg, Throwable cause) {
        this.log(WARN, msg, cause);
    }

    public void error(Object msg) {
        this.log(ERROR, msg, null);
    }

    public void error(Object msg, Throwable cause) {
        this.log(ERROR, msg, cause);
    }

    public static Logger create(Class type) {
        return new Logger(type.getName());
    }

    public static Logger create(Class type, String suffix) {
        return new Logger(type.getName() + "." + suffix);
    }
}

