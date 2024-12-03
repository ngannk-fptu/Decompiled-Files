/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceReference
 *  org.osgi.service.log.LogService
 */
package org.apache.felix.bundlerepository;

import java.io.PrintStream;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Logger {
    public static final int LOG_ERROR = 1;
    public static final int LOG_WARNING = 2;
    public static final int LOG_INFO = 3;
    public static final int LOG_DEBUG = 4;
    private final BundleContext m_context;
    private boolean m_isLogClassPresent;
    static /* synthetic */ Class class$org$osgi$service$log$LogService;

    Logger(BundleContext context) {
        this.m_context = context;
        try {
            (class$org$osgi$service$log$LogService == null ? (class$org$osgi$service$log$LogService = Logger.class$("org.osgi.service.log.LogService")) : class$org$osgi$service$log$LogService).getName();
            this.m_isLogClassPresent = true;
        }
        catch (NoClassDefFoundError ex) {
            this.m_isLogClassPresent = false;
        }
    }

    public void log(int level, String message) {
        this.log(level, message, null);
    }

    public void log(int level, String message, Throwable exception) {
        if (!this.m_isLogClassPresent || !this._log(level, message, exception)) {
            PrintStream stream = this.getStream(level);
            stream.println(message);
            if (exception != null) {
                exception.printStackTrace(stream);
            }
        }
    }

    private boolean _log(int level, String message, Throwable exception) {
        try {
            LogService logService;
            ServiceReference reference = null;
            reference = this.m_context.getServiceReference((class$org$osgi$service$log$LogService == null ? (class$org$osgi$service$log$LogService = Logger.class$("org.osgi.service.log.LogService")) : class$org$osgi$service$log$LogService).getName());
            if (reference != null && (logService = (LogService)this.m_context.getService(reference)) != null) {
                logService.log(level, message, exception);
                this.m_context.ungetService(reference);
                return true;
            }
        }
        catch (NoClassDefFoundError e) {
            // empty catch block
        }
        return false;
    }

    private PrintStream getStream(int level) {
        switch (level) {
            case 1: {
                System.err.print("ERROR: ");
                return System.err;
            }
            case 2: {
                System.err.print("WARNING: ");
                return System.err;
            }
            case 3: {
                System.out.print("INFO: ");
                return System.out;
            }
            case 4: {
                System.out.print("DEBUG: ");
                return System.out;
            }
        }
        System.out.print("UNKNOWN: ");
        return System.out;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

