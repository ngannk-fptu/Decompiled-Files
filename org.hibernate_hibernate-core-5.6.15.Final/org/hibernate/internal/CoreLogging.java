/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.internal;

import org.hibernate.internal.CoreMessageLogger;
import org.jboss.logging.Logger;

public class CoreLogging {
    private CoreLogging() {
    }

    public static CoreMessageLogger messageLogger(Class classNeedingLogging) {
        return CoreLogging.messageLogger(classNeedingLogging.getName());
    }

    public static CoreMessageLogger messageLogger(String loggerName) {
        return (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)loggerName);
    }

    public static Logger logger(Class classNeedingLogging) {
        return Logger.getLogger((Class)classNeedingLogging);
    }

    public static Logger logger(String loggerName) {
        return Logger.getLogger((String)loggerName);
    }
}

