/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver;

import org.apache.felix.resolver.ResolutionError;
import org.osgi.resource.Resource;

public class Logger {
    public static final int LOG_ERROR = 1;
    public static final int LOG_WARNING = 2;
    public static final int LOG_INFO = 3;
    public static final int LOG_DEBUG = 4;
    private int m_logLevel = 1;

    public Logger(int i) {
        this.m_logLevel = i;
    }

    public final synchronized void setLogLevel(int i) {
        this.m_logLevel = i;
    }

    public final synchronized int getLogLevel() {
        return this.m_logLevel;
    }

    public final void log(int level, String msg) {
        this._log(level, msg, null);
    }

    public final void log(int level, String msg, Throwable throwable) {
        this._log(level, msg, throwable);
    }

    public boolean isDebugEnabled() {
        return this.m_logLevel >= 4;
    }

    public final void debug(String msg) {
        this._log(4, msg, null);
    }

    protected void doLog(int level, String msg, Throwable throwable) {
        if (level > this.m_logLevel) {
            return;
        }
        String s = msg;
        if (throwable != null) {
            s = s + " (" + throwable + ")";
        }
        switch (level) {
            case 4: {
                System.out.println("DEBUG: " + s);
                break;
            }
            case 1: {
                System.out.println("ERROR: " + s);
                if (throwable == null) break;
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

    private void _log(int level, String msg, Throwable throwable) {
        if (this.m_logLevel >= level) {
            this.doLog(level, msg, throwable);
        }
    }

    public void logUsesConstraintViolation(Resource resource, ResolutionError error) {
    }
}

