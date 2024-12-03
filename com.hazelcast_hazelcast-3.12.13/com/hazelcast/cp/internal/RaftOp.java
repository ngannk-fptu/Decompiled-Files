/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.exception.RetryableException;
import com.hazelcast.spi.exception.SilentException;
import com.hazelcast.util.EmptyStatement;
import java.util.logging.Level;

public abstract class RaftOp
implements DataSerializable {
    private transient NodeEngine nodeEngine;

    public abstract Object run(CPGroupId var1, long var2) throws Exception;

    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    public RaftOp setNodeEngine(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
        return this;
    }

    public <T> T getService() {
        return this.nodeEngine.getService(this.getServiceName());
    }

    protected ILogger getLogger() {
        return this.getNodeEngine().getLogger(this.getClass());
    }

    protected abstract String getServiceName();

    public void logFailure(Throwable e) {
        ILogger logger = this.getLogger();
        if (e instanceof SilentException) {
            if (logger.isFinestEnabled()) {
                logger.finest(e.getMessage(), e);
            }
        } else if (e instanceof RetryableException) {
            if (logger.isFineEnabled()) {
                logger.fine(e.getClass().getName() + ": " + e.getMessage());
            }
        } else if (e instanceof OutOfMemoryError) {
            try {
                logger.severe(e.getMessage(), e);
            }
            catch (Throwable t) {
                EmptyStatement.ignore(t);
            }
        } else {
            Level level;
            Level level2 = level = this.nodeEngine != null && this.nodeEngine.isRunning() ? Level.WARNING : Level.FINE;
            if (logger.isLoggable(level)) {
                logger.log(level, e.getMessage(), e);
            }
        }
    }

    protected void toString(StringBuilder sb) {
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName()).append('{');
        sb.append("serviceName='").append(this.getServiceName()).append('\'');
        this.toString(sb);
        sb.append('}');
        return sb.toString();
    }
}

