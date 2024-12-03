/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.validator.internal.util.logging;

import java.lang.invoke.MethodHandles;
import org.hibernate.validator.internal.util.logging.Log;
import org.jboss.logging.Logger;

public final class LoggerFactory {
    public static Log make(MethodHandles.Lookup creationContext) {
        String className = creationContext.lookupClass().getName();
        return (Log)Logger.getMessageLogger(Log.class, (String)className);
    }

    private LoggerFactory() {
    }
}

