/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.util.logging;

import java.lang.invoke.MethodHandles;
import org.hibernate.validator.internal.util.logging.Log;
import org.jboss.logging.Logger;

public final class LoggerFactory {
    public static Log make(MethodHandles.Lookup creationContext) {
        String className = creationContext.lookupClass().getName();
        return Logger.getMessageLogger(Log.class, className);
    }

    private LoggerFactory() {
    }
}

