/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.internal;

import org.hibernate.internal.EntityManagerMessageLogger;
import org.jboss.logging.Logger;

public class HEMLogging {
    private HEMLogging() {
    }

    public static EntityManagerMessageLogger messageLogger(Class classNeedingLogging) {
        return HEMLogging.messageLogger(classNeedingLogging.getName());
    }

    public static EntityManagerMessageLogger messageLogger(String loggerName) {
        return (EntityManagerMessageLogger)Logger.getMessageLogger(EntityManagerMessageLogger.class, (String)loggerName);
    }

    public static Logger logger(Class classNeedingLogging) {
        return Logger.getLogger((Class)classNeedingLogging);
    }

    public static Logger logger(String loggerName) {
        return Logger.getLogger((String)loggerName);
    }
}

