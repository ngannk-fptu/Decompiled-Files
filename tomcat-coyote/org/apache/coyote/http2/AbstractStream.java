/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.coyote.http2.ConnectionException;
import org.apache.coyote.http2.Http2Error;
import org.apache.coyote.http2.Http2Exception;
import org.apache.coyote.http2.StreamException;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

abstract class AbstractStream {
    private static final Log log = LogFactory.getLog(AbstractStream.class);
    private static final StringManager sm = StringManager.getManager(AbstractStream.class);
    private final Integer identifier;
    private final String idAsString;
    private long windowSize = 65535L;
    protected final Lock windowAllocationLock = new ReentrantLock();
    protected final Condition windowAllocationAvailable = this.windowAllocationLock.newCondition();
    private volatile int connectionAllocationRequested = 0;
    private volatile int connectionAllocationMade = 0;

    AbstractStream(Integer identifier) {
        this.identifier = identifier;
        this.idAsString = identifier.toString();
    }

    final Integer getIdentifier() {
        return this.identifier;
    }

    final String getIdAsString() {
        return this.idAsString;
    }

    final int getIdAsInt() {
        return this.identifier;
    }

    final void setWindowSize(long windowSize) {
        this.windowAllocationLock.lock();
        try {
            this.windowSize = windowSize;
        }
        finally {
            this.windowAllocationLock.unlock();
        }
    }

    final long getWindowSize() {
        this.windowAllocationLock.lock();
        try {
            long l = this.windowSize;
            return l;
        }
        finally {
            this.windowAllocationLock.unlock();
        }
    }

    void incrementWindowSize(int increment) throws Http2Exception {
        this.windowAllocationLock.lock();
        try {
            this.windowSize += (long)increment;
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("abstractStream.windowSizeInc", new Object[]{this.getConnectionId(), this.getIdAsString(), Integer.toString(increment), Long.toString(this.windowSize)}));
            }
            if (this.windowSize > Integer.MAX_VALUE) {
                String msg = sm.getString("abstractStream.windowSizeTooBig", new Object[]{this.getConnectionId(), this.identifier, Integer.toString(increment), Long.toString(this.windowSize)});
                if (this.identifier == 0) {
                    throw new ConnectionException(msg, Http2Error.FLOW_CONTROL_ERROR);
                }
                throw new StreamException(msg, Http2Error.FLOW_CONTROL_ERROR, this.identifier);
            }
        }
        finally {
            this.windowAllocationLock.unlock();
        }
    }

    final void decrementWindowSize(int decrement) {
        this.windowAllocationLock.lock();
        try {
            this.windowSize -= (long)decrement;
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("abstractStream.windowSizeDec", new Object[]{this.getConnectionId(), this.getIdAsString(), Integer.toString(decrement), Long.toString(this.windowSize)}));
            }
        }
        finally {
            this.windowAllocationLock.unlock();
        }
    }

    final int getConnectionAllocationRequested() {
        return this.connectionAllocationRequested;
    }

    final void setConnectionAllocationRequested(int connectionAllocationRequested) {
        log.debug((Object)sm.getString("abstractStream.setConnectionAllocationRequested", new Object[]{this.getConnectionId(), this.getIdAsString(), Integer.toString(this.connectionAllocationRequested), Integer.toString(connectionAllocationRequested)}));
        this.connectionAllocationRequested = connectionAllocationRequested;
    }

    final int getConnectionAllocationMade() {
        return this.connectionAllocationMade;
    }

    final void setConnectionAllocationMade(int connectionAllocationMade) {
        log.debug((Object)sm.getString("abstractStream.setConnectionAllocationMade", new Object[]{this.getConnectionId(), this.getIdAsString(), Integer.toString(this.connectionAllocationMade), Integer.toString(connectionAllocationMade)}));
        this.connectionAllocationMade = connectionAllocationMade;
    }

    abstract String getConnectionId();
}

