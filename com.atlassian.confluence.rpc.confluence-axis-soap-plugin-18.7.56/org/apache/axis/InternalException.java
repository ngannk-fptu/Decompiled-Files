/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis;

import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

public class InternalException
extends RuntimeException {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$InternalException == null ? (class$org$apache$axis$InternalException = InternalException.class$("org.apache.axis.InternalException")) : class$org$apache$axis$InternalException).getName());
    private static boolean shouldLog = true;
    static /* synthetic */ Class class$org$apache$axis$InternalException;

    public static void setLogging(boolean logging) {
        shouldLog = logging;
    }

    public static boolean getLogging() {
        return shouldLog;
    }

    public InternalException(String message) {
        this(new Exception(message));
    }

    public InternalException(Exception e) {
        super(e.toString());
        if (shouldLog) {
            if (e instanceof InternalException) {
                log.debug((Object)"InternalException: ", (Throwable)e);
            } else {
                log.fatal((Object)Messages.getMessage("exception00"), (Throwable)e);
            }
        }
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

