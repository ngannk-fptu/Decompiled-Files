/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 */
package org.apache.tomcat.dbcp.dbcp2;

import org.apache.juli.logging.Log;
import org.apache.tomcat.dbcp.dbcp2.LifetimeExceededException;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.pool2.SwallowedExceptionListener;

public class SwallowedExceptionLogger
implements SwallowedExceptionListener {
    private final Log log;
    private final boolean logExpiredConnections;

    public SwallowedExceptionLogger(Log log) {
        this(log, true);
    }

    public SwallowedExceptionLogger(Log log, boolean logExpiredConnections) {
        this.log = log;
        this.logExpiredConnections = logExpiredConnections;
    }

    @Override
    public void onSwallowException(Exception e) {
        if (this.logExpiredConnections || !(e instanceof LifetimeExceededException)) {
            this.log.warn((Object)Utils.getMessage("swallowedExceptionLogger.onSwallowedException"), (Throwable)e);
        }
    }
}

