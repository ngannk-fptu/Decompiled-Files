/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.logging;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.LoggerProviders;
import org.jboss.logging.LoggingLocale;
import org.jboss.logging.SerializedLogger;

public abstract class Logger
implements Serializable,
BasicLogger {
    private static final long serialVersionUID = 4232175575988879434L;
    private static final String FQCN = Logger.class.getName();
    private final String name;

    protected Logger(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    protected abstract void doLog(Level var1, String var2, Object var3, Object[] var4, Throwable var5);

    protected abstract void doLogf(Level var1, String var2, String var3, Object[] var4, Throwable var5);

    @Override
    public boolean isTraceEnabled() {
        return this.isEnabled(Level.TRACE);
    }

    @Override
    public void trace(Object message) {
        this.doLog(Level.TRACE, FQCN, message, null, null);
    }

    @Override
    public void trace(Object message, Throwable t) {
        this.doLog(Level.TRACE, FQCN, message, null, t);
    }

    @Override
    public void trace(String loggerFqcn, Object message, Throwable t) {
        this.doLog(Level.TRACE, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void trace(Object message, Object[] params) {
        this.doLog(Level.TRACE, FQCN, message, params, null);
    }

    @Deprecated
    public void trace(Object message, Object[] params, Throwable t) {
        this.doLog(Level.TRACE, FQCN, message, params, t);
    }

    @Override
    public void trace(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.doLog(Level.TRACE, loggerFqcn, message, params, t);
    }

    @Override
    public void tracev(String format, Object ... params) {
        this.doLog(Level.TRACE, FQCN, format, params, null);
    }

    @Override
    public void tracev(String format, Object param1) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLog(Level.TRACE, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void tracev(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLog(Level.TRACE, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void tracev(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLog(Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void tracev(Throwable t, String format, Object ... params) {
        this.doLog(Level.TRACE, FQCN, format, params, t);
    }

    @Override
    public void tracev(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLog(Level.TRACE, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void tracev(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLog(Level.TRACE, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void tracev(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLog(Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void tracef(String format, Object ... params) {
        this.doLogf(Level.TRACE, FQCN, format, params, null);
    }

    @Override
    public void tracef(String format, Object param1) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void tracef(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void tracef(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void tracef(Throwable t, String format, Object ... params) {
        this.doLogf(Level.TRACE, FQCN, format, params, t);
    }

    @Override
    public void tracef(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void tracef(String format, int arg) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void tracef(String format, int arg1, int arg2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void tracef(String format, int arg1, Object arg2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void tracef(String format, int arg1, int arg2, int arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void tracef(String format, int arg1, int arg2, Object arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void tracef(String format, int arg1, Object arg2, Object arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void tracef(Throwable t, String format, int arg) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, int arg1, int arg2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, int arg1, Object arg2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, int arg1, int arg2, int arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, int arg1, int arg2, Object arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, int arg1, Object arg2, Object arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void tracef(String format, long arg) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void tracef(String format, long arg1, long arg2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void tracef(String format, long arg1, Object arg2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void tracef(String format, long arg1, long arg2, long arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void tracef(String format, long arg1, long arg2, Object arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void tracef(String format, long arg1, Object arg2, Object arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void tracef(Throwable t, String format, long arg) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, long arg1, long arg2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, long arg1, Object arg2) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, long arg1, long arg2, long arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, long arg1, long arg2, Object arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void tracef(Throwable t, String format, long arg1, Object arg2, Object arg3) {
        if (this.isEnabled(Level.TRACE)) {
            this.doLogf(Level.TRACE, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return this.isEnabled(Level.DEBUG);
    }

    @Override
    public void debug(Object message) {
        this.doLog(Level.DEBUG, FQCN, message, null, null);
    }

    @Override
    public void debug(Object message, Throwable t) {
        this.doLog(Level.DEBUG, FQCN, message, null, t);
    }

    @Override
    public void debug(String loggerFqcn, Object message, Throwable t) {
        this.doLog(Level.DEBUG, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void debug(Object message, Object[] params) {
        this.doLog(Level.DEBUG, FQCN, message, params, null);
    }

    @Deprecated
    public void debug(Object message, Object[] params, Throwable t) {
        this.doLog(Level.DEBUG, FQCN, message, params, t);
    }

    @Override
    public void debug(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.doLog(Level.DEBUG, loggerFqcn, message, params, t);
    }

    @Override
    public void debugv(String format, Object ... params) {
        this.doLog(Level.DEBUG, FQCN, format, params, null);
    }

    @Override
    public void debugv(String format, Object param1) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLog(Level.DEBUG, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void debugv(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLog(Level.DEBUG, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void debugv(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLog(Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void debugv(Throwable t, String format, Object ... params) {
        this.doLog(Level.DEBUG, FQCN, format, params, t);
    }

    @Override
    public void debugv(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLog(Level.DEBUG, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void debugv(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLog(Level.DEBUG, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void debugv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLog(Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void debugf(String format, Object ... params) {
        this.doLogf(Level.DEBUG, FQCN, format, params, null);
    }

    @Override
    public void debugf(String format, Object param1) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void debugf(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void debugf(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void debugf(Throwable t, String format, Object ... params) {
        this.doLogf(Level.DEBUG, FQCN, format, params, t);
    }

    @Override
    public void debugf(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void debugf(String format, int arg) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void debugf(String format, int arg1, int arg2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void debugf(String format, int arg1, Object arg2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void debugf(String format, int arg1, int arg2, int arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void debugf(String format, int arg1, int arg2, Object arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void debugf(String format, int arg1, Object arg2, Object arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void debugf(Throwable t, String format, int arg) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, int arg1, int arg2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, int arg1, Object arg2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, int arg1, int arg2, int arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, int arg1, int arg2, Object arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, int arg1, Object arg2, Object arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void debugf(String format, long arg) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void debugf(String format, long arg1, long arg2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void debugf(String format, long arg1, Object arg2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void debugf(String format, long arg1, long arg2, long arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void debugf(String format, long arg1, long arg2, Object arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void debugf(String format, long arg1, Object arg2, Object arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, null);
        }
    }

    @Override
    public void debugf(Throwable t, String format, long arg) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, long arg1, long arg2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, long arg1, Object arg2) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, long arg1, long arg2, long arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, long arg1, long arg2, Object arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public void debugf(Throwable t, String format, long arg1, Object arg2, Object arg3) {
        if (this.isEnabled(Level.DEBUG)) {
            this.doLogf(Level.DEBUG, FQCN, format, new Object[]{arg1, arg2, arg3}, t);
        }
    }

    @Override
    public boolean isInfoEnabled() {
        return this.isEnabled(Level.INFO);
    }

    @Override
    public void info(Object message) {
        this.doLog(Level.INFO, FQCN, message, null, null);
    }

    @Override
    public void info(Object message, Throwable t) {
        this.doLog(Level.INFO, FQCN, message, null, t);
    }

    @Override
    public void info(String loggerFqcn, Object message, Throwable t) {
        this.doLog(Level.INFO, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void info(Object message, Object[] params) {
        this.doLog(Level.INFO, FQCN, message, params, null);
    }

    @Deprecated
    public void info(Object message, Object[] params, Throwable t) {
        this.doLog(Level.INFO, FQCN, message, params, t);
    }

    @Override
    public void info(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.doLog(Level.INFO, loggerFqcn, message, params, t);
    }

    @Override
    public void infov(String format, Object ... params) {
        this.doLog(Level.INFO, FQCN, format, params, null);
    }

    @Override
    public void infov(String format, Object param1) {
        if (this.isEnabled(Level.INFO)) {
            this.doLog(Level.INFO, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void infov(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.INFO)) {
            this.doLog(Level.INFO, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void infov(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.INFO)) {
            this.doLog(Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void infov(Throwable t, String format, Object ... params) {
        this.doLog(Level.INFO, FQCN, format, params, t);
    }

    @Override
    public void infov(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.INFO)) {
            this.doLog(Level.INFO, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void infov(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.INFO)) {
            this.doLog(Level.INFO, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void infov(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.INFO)) {
            this.doLog(Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void infof(String format, Object ... params) {
        this.doLogf(Level.INFO, FQCN, format, params, null);
    }

    @Override
    public void infof(String format, Object param1) {
        if (this.isEnabled(Level.INFO)) {
            this.doLogf(Level.INFO, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void infof(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.INFO)) {
            this.doLogf(Level.INFO, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void infof(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.INFO)) {
            this.doLogf(Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void infof(Throwable t, String format, Object ... params) {
        this.doLogf(Level.INFO, FQCN, format, params, t);
    }

    @Override
    public void infof(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.INFO)) {
            this.doLogf(Level.INFO, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void infof(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.INFO)) {
            this.doLogf(Level.INFO, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void infof(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.INFO)) {
            this.doLogf(Level.INFO, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void warn(Object message) {
        this.doLog(Level.WARN, FQCN, message, null, null);
    }

    @Override
    public void warn(Object message, Throwable t) {
        this.doLog(Level.WARN, FQCN, message, null, t);
    }

    @Override
    public void warn(String loggerFqcn, Object message, Throwable t) {
        this.doLog(Level.WARN, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void warn(Object message, Object[] params) {
        this.doLog(Level.WARN, FQCN, message, params, null);
    }

    @Deprecated
    public void warn(Object message, Object[] params, Throwable t) {
        this.doLog(Level.WARN, FQCN, message, params, t);
    }

    @Override
    public void warn(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.doLog(Level.WARN, loggerFqcn, message, params, t);
    }

    @Override
    public void warnv(String format, Object ... params) {
        this.doLog(Level.WARN, FQCN, format, params, null);
    }

    @Override
    public void warnv(String format, Object param1) {
        if (this.isEnabled(Level.WARN)) {
            this.doLog(Level.WARN, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void warnv(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.WARN)) {
            this.doLog(Level.WARN, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void warnv(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.WARN)) {
            this.doLog(Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void warnv(Throwable t, String format, Object ... params) {
        this.doLog(Level.WARN, FQCN, format, params, t);
    }

    @Override
    public void warnv(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.WARN)) {
            this.doLog(Level.WARN, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void warnv(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.WARN)) {
            this.doLog(Level.WARN, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void warnv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.WARN)) {
            this.doLog(Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void warnf(String format, Object ... params) {
        this.doLogf(Level.WARN, FQCN, format, params, null);
    }

    @Override
    public void warnf(String format, Object param1) {
        if (this.isEnabled(Level.WARN)) {
            this.doLogf(Level.WARN, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void warnf(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.WARN)) {
            this.doLogf(Level.WARN, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void warnf(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.WARN)) {
            this.doLogf(Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void warnf(Throwable t, String format, Object ... params) {
        this.doLogf(Level.WARN, FQCN, format, params, t);
    }

    @Override
    public void warnf(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.WARN)) {
            this.doLogf(Level.WARN, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void warnf(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.WARN)) {
            this.doLogf(Level.WARN, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void warnf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.WARN)) {
            this.doLogf(Level.WARN, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void error(Object message) {
        this.doLog(Level.ERROR, FQCN, message, null, null);
    }

    @Override
    public void error(Object message, Throwable t) {
        this.doLog(Level.ERROR, FQCN, message, null, t);
    }

    @Override
    public void error(String loggerFqcn, Object message, Throwable t) {
        this.doLog(Level.ERROR, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void error(Object message, Object[] params) {
        this.doLog(Level.ERROR, FQCN, message, params, null);
    }

    @Deprecated
    public void error(Object message, Object[] params, Throwable t) {
        this.doLog(Level.ERROR, FQCN, message, params, t);
    }

    @Override
    public void error(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.doLog(Level.ERROR, loggerFqcn, message, params, t);
    }

    @Override
    public void errorv(String format, Object ... params) {
        this.doLog(Level.ERROR, FQCN, format, params, null);
    }

    @Override
    public void errorv(String format, Object param1) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLog(Level.ERROR, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void errorv(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLog(Level.ERROR, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void errorv(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLog(Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void errorv(Throwable t, String format, Object ... params) {
        this.doLog(Level.ERROR, FQCN, format, params, t);
    }

    @Override
    public void errorv(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLog(Level.ERROR, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void errorv(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLog(Level.ERROR, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void errorv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLog(Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void errorf(String format, Object ... params) {
        this.doLogf(Level.ERROR, FQCN, format, params, null);
    }

    @Override
    public void errorf(String format, Object param1) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLogf(Level.ERROR, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void errorf(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLogf(Level.ERROR, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void errorf(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLogf(Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void errorf(Throwable t, String format, Object ... params) {
        this.doLogf(Level.ERROR, FQCN, format, params, t);
    }

    @Override
    public void errorf(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLogf(Level.ERROR, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void errorf(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLogf(Level.ERROR, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void errorf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.ERROR)) {
            this.doLogf(Level.ERROR, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void fatal(Object message) {
        this.doLog(Level.FATAL, FQCN, message, null, null);
    }

    @Override
    public void fatal(Object message, Throwable t) {
        this.doLog(Level.FATAL, FQCN, message, null, t);
    }

    @Override
    public void fatal(String loggerFqcn, Object message, Throwable t) {
        this.doLog(Level.FATAL, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void fatal(Object message, Object[] params) {
        this.doLog(Level.FATAL, FQCN, message, params, null);
    }

    @Deprecated
    public void fatal(Object message, Object[] params, Throwable t) {
        this.doLog(Level.FATAL, FQCN, message, params, t);
    }

    @Override
    public void fatal(String loggerFqcn, Object message, Object[] params, Throwable t) {
        this.doLog(Level.FATAL, loggerFqcn, message, params, t);
    }

    @Override
    public void fatalv(String format, Object ... params) {
        this.doLog(Level.FATAL, FQCN, format, params, null);
    }

    @Override
    public void fatalv(String format, Object param1) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLog(Level.FATAL, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void fatalv(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLog(Level.FATAL, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void fatalv(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLog(Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void fatalv(Throwable t, String format, Object ... params) {
        this.doLog(Level.FATAL, FQCN, format, params, t);
    }

    @Override
    public void fatalv(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLog(Level.FATAL, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void fatalv(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLog(Level.FATAL, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void fatalv(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLog(Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void fatalf(String format, Object ... params) {
        this.doLogf(Level.FATAL, FQCN, format, params, null);
    }

    @Override
    public void fatalf(String format, Object param1) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLogf(Level.FATAL, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void fatalf(String format, Object param1, Object param2) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLogf(Level.FATAL, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void fatalf(String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLogf(Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void fatalf(Throwable t, String format, Object ... params) {
        this.doLogf(Level.FATAL, FQCN, format, params, t);
    }

    @Override
    public void fatalf(Throwable t, String format, Object param1) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLogf(Level.FATAL, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void fatalf(Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLogf(Level.FATAL, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void fatalf(Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(Level.FATAL)) {
            this.doLogf(Level.FATAL, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void log(Level level, Object message) {
        this.doLog(level, FQCN, message, null, null);
    }

    @Override
    public void log(Level level, Object message, Throwable t) {
        this.doLog(level, FQCN, message, null, t);
    }

    @Override
    public void log(Level level, String loggerFqcn, Object message, Throwable t) {
        this.doLog(level, loggerFqcn, message, null, t);
    }

    @Deprecated
    public void log(Level level, Object message, Object[] params) {
        this.doLog(level, FQCN, message, params, null);
    }

    @Deprecated
    public void log(Level level, Object message, Object[] params, Throwable t) {
        this.doLog(level, FQCN, message, params, t);
    }

    @Override
    public void log(String loggerFqcn, Level level, Object message, Object[] params, Throwable t) {
        this.doLog(level, loggerFqcn, message, params, t);
    }

    @Override
    public void logv(Level level, String format, Object ... params) {
        this.doLog(level, FQCN, format, params, null);
    }

    @Override
    public void logv(Level level, String format, Object param1) {
        if (this.isEnabled(level)) {
            this.doLog(level, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void logv(Level level, String format, Object param1, Object param2) {
        if (this.isEnabled(level)) {
            this.doLog(level, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void logv(Level level, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(level)) {
            this.doLog(level, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void logv(Level level, Throwable t, String format, Object ... params) {
        this.doLog(level, FQCN, format, params, t);
    }

    @Override
    public void logv(Level level, Throwable t, String format, Object param1) {
        if (this.isEnabled(level)) {
            this.doLog(level, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void logv(Level level, Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(level)) {
            this.doLog(level, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void logv(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(level)) {
            this.doLog(level, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object ... params) {
        this.doLog(level, loggerFqcn, format, params, t);
    }

    @Override
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
        if (this.isEnabled(level)) {
            this.doLog(level, loggerFqcn, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(level)) {
            this.doLog(level, loggerFqcn, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void logv(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(level)) {
            this.doLog(level, loggerFqcn, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void logf(Level level, String format, Object ... params) {
        this.doLogf(level, FQCN, format, params, null);
    }

    @Override
    public void logf(Level level, String format, Object param1) {
        if (this.isEnabled(level)) {
            this.doLogf(level, FQCN, format, new Object[]{param1}, null);
        }
    }

    @Override
    public void logf(Level level, String format, Object param1, Object param2) {
        if (this.isEnabled(level)) {
            this.doLogf(level, FQCN, format, new Object[]{param1, param2}, null);
        }
    }

    @Override
    public void logf(Level level, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(level)) {
            this.doLogf(level, FQCN, format, new Object[]{param1, param2, param3}, null);
        }
    }

    @Override
    public void logf(Level level, Throwable t, String format, Object ... params) {
        this.doLogf(level, FQCN, format, params, t);
    }

    @Override
    public void logf(Level level, Throwable t, String format, Object param1) {
        if (this.isEnabled(level)) {
            this.doLogf(level, FQCN, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void logf(Level level, Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(level)) {
            this.doLogf(level, FQCN, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void logf(Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(level)) {
            this.doLogf(level, FQCN, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1) {
        if (this.isEnabled(level)) {
            this.doLogf(level, loggerFqcn, format, new Object[]{param1}, t);
        }
    }

    @Override
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2) {
        if (this.isEnabled(level)) {
            this.doLogf(level, loggerFqcn, format, new Object[]{param1, param2}, t);
        }
    }

    @Override
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object param1, Object param2, Object param3) {
        if (this.isEnabled(level)) {
            this.doLogf(level, loggerFqcn, format, new Object[]{param1, param2, param3}, t);
        }
    }

    @Override
    public void logf(String loggerFqcn, Level level, Throwable t, String format, Object ... params) {
        this.doLogf(level, loggerFqcn, format, params, t);
    }

    protected final Object writeReplace() {
        return new SerializedLogger(this.name);
    }

    public static Logger getLogger(String name) {
        return LoggerProviders.PROVIDER.getLogger(name);
    }

    public static Logger getLogger(String name, String suffix) {
        return Logger.getLogger(name == null || name.length() == 0 ? suffix : name + "." + suffix);
    }

    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    public static Logger getLogger(Class<?> clazz, String suffix) {
        return Logger.getLogger(clazz.getName(), suffix);
    }

    public static <T> T getMessageLogger(Class<T> type, String category) {
        return Logger.getMessageLogger(type, category, LoggingLocale.getLocale());
    }

    public static <T> T getMessageLogger(final Class<T> type, final String category, final Locale locale) {
        return AccessController.doPrivileged(new PrivilegedAction<T>(){

            @Override
            public T run() {
                Constructor constructor;
                String language = locale.getLanguage();
                String country = locale.getCountry();
                String variant = locale.getVariant();
                Class loggerClass = null;
                ClassLoader classLoader = type.getClassLoader();
                String typeName = type.getName();
                if (variant != null && variant.length() > 0) {
                    try {
                        loggerClass = Class.forName(Logger.join(typeName, "$logger", language, country, variant), true, classLoader).asSubclass(type);
                    }
                    catch (ClassNotFoundException e) {
                        // empty catch block
                    }
                }
                if (loggerClass == null && country != null && country.length() > 0) {
                    try {
                        loggerClass = Class.forName(Logger.join(typeName, "$logger", language, country, null), true, classLoader).asSubclass(type);
                    }
                    catch (ClassNotFoundException e) {
                        // empty catch block
                    }
                }
                if (loggerClass == null && language != null && language.length() > 0) {
                    try {
                        loggerClass = Class.forName(Logger.join(typeName, "$logger", language, null, null), true, classLoader).asSubclass(type);
                    }
                    catch (ClassNotFoundException e) {
                        // empty catch block
                    }
                }
                if (loggerClass == null) {
                    try {
                        loggerClass = Class.forName(Logger.join(typeName, "$logger", null, null, null), true, classLoader).asSubclass(type);
                    }
                    catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("Invalid logger " + type + " (implementation not found in " + classLoader + ")");
                    }
                }
                try {
                    constructor = loggerClass.getConstructor(Logger.class);
                }
                catch (NoSuchMethodException e) {
                    throw new IllegalArgumentException("Logger implementation " + loggerClass + " has no matching constructor");
                }
                try {
                    return constructor.newInstance(Logger.getLogger(category));
                }
                catch (InstantiationException e) {
                    throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", e);
                }
                catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", e);
                }
                catch (InvocationTargetException e) {
                    throw new IllegalArgumentException("Logger implementation " + loggerClass + " could not be instantiated", e.getCause());
                }
            }
        });
    }

    private static String join(String interfaceName, String a, String b, String c, String d) {
        StringBuilder build = new StringBuilder();
        build.append(interfaceName).append('_').append(a);
        if (b != null && b.length() > 0) {
            build.append('_');
            build.append(b);
        }
        if (c != null && c.length() > 0) {
            build.append('_');
            build.append(c);
        }
        if (d != null && d.length() > 0) {
            build.append('_');
            build.append(d);
        }
        return build.toString();
    }

    public static enum Level {
        FATAL,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE;

    }
}

