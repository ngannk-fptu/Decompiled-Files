/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.eclipse.gemini.blueprint.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.util.SimpleLogger;

class LogUtils {
    LogUtils() {
    }

    public static Log createLogger(final Class<?> logName) {
        if (System.getSecurityManager() != null) {
            return AccessController.doPrivileged(new PrivilegedAction<Log>(){

                @Override
                public Log run() {
                    return LogUtils.doCreateLogger(logName);
                }
            });
        }
        return LogUtils.doCreateLogger(logName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Log doCreateLogger(Class<?> logName) {
        Log logger;
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(logName.getClassLoader());
        try {
            logger = LogFactory.getLog(logName);
        }
        catch (Throwable th) {
            logger = new SimpleLogger();
            logger.fatal((Object)"logger infrastructure not properly set up. If commons-logging jar is used try switching to slf4j (see the FAQ for more info).", th);
        }
        finally {
            Thread.currentThread().setContextClassLoader(ccl);
        }
        return logger;
    }
}

