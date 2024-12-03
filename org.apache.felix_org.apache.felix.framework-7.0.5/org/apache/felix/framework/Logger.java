/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.lang.reflect.Method;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;

public class Logger
extends org.apache.felix.resolver.Logger {
    private Object[] m_logger;

    public Logger() {
        super(1);
    }

    public void setLogger(Object logger) {
        if (logger == null) {
            this.m_logger = null;
        } else {
            try {
                Method mth = logger.getClass().getMethod("log", Integer.TYPE, String.class, Throwable.class);
                mth.setAccessible(true);
                this.m_logger = new Object[]{logger, mth};
            }
            catch (NoSuchMethodException ex) {
                System.err.println("Logger: " + ex);
                this.m_logger = null;
            }
        }
    }

    public final void log(ServiceReference sr, int level, String msg) {
        this._log(null, sr, level, msg, null);
    }

    public final void log(ServiceReference sr, int level, String msg, Throwable throwable) {
        this._log(null, sr, level, msg, throwable);
    }

    public final void log(Bundle bundle, int level, String msg) {
        this._log(bundle, null, level, msg, null);
    }

    public final void log(Bundle bundle, int level, String msg, Throwable throwable) {
        this._log(bundle, null, level, msg, throwable);
    }

    protected void _log(Bundle bundle, ServiceReference sr, int level, String msg, Throwable throwable) {
        if (this.getLogLevel() >= level) {
            this.doLog(bundle, sr, level, msg, throwable);
        }
    }

    protected void doLog(Bundle bundle, ServiceReference sr, int level, String msg, Throwable throwable) {
        StringBuilder s = new StringBuilder();
        if (sr != null) {
            s.append("SvcRef ").append(sr).append(" ").append(msg);
        } else if (bundle != null) {
            s.append("Bundle ").append(bundle.toString()).append(" ").append(msg);
        } else {
            s.append(msg);
        }
        if (throwable != null) {
            s.append(" (").append(throwable).append(")");
        }
        this.doLog(level, s.toString(), throwable);
    }

    @Override
    protected void doLog(int level, String msg, Throwable throwable) {
        if (this.m_logger != null) {
            this.doLogReflectively(level, msg, throwable);
        } else {
            this.doLogOut(level, msg, throwable);
        }
    }

    protected void doLogOut(int level, String s, Throwable throwable) {
        switch (level) {
            case 4: {
                System.out.println("DEBUG: " + s);
                break;
            }
            case 1: {
                System.out.println("ERROR: " + s);
                if (throwable == null) break;
                if (throwable instanceof BundleException && ((BundleException)throwable).getNestedException() != null) {
                    throwable = ((BundleException)throwable).getNestedException();
                }
                throwable.printStackTrace();
                break;
            }
            case 3: {
                System.out.println("INFO: " + s);
                break;
            }
            case 2: {
                System.out.println("WARNING: " + s);
                break;
            }
            default: {
                System.out.println("UNKNOWN[" + level + "]: " + s);
            }
        }
    }

    protected void doLogReflectively(int level, String msg, Throwable throwable) {
        try {
            ((Method)this.m_logger[1]).invoke(this.m_logger[0], level, msg, throwable);
        }
        catch (Exception ex) {
            System.err.println("Logger: " + ex);
        }
    }
}

