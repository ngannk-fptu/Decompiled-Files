/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import com.hazelcast.logging.ILogger;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationResponseHandler;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import com.hazelcast.spi.properties.GroupProperty;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class LocalRetryableExecution
implements Runnable,
OperationResponseHandler {
    private static final int LOG_MAX_INVOCATION_COUNT = 99;
    private final ILogger logger;
    private final CountDownLatch done = new CountDownLatch(1);
    private final Operation op;
    private final NodeEngine nodeEngine;
    private final long invocationRetryPauseMillis;
    private final int invocationMaxRetryCount;
    private volatile Object response;
    private int tryCount;

    LocalRetryableExecution(NodeEngine nodeEngine, Operation op) {
        this.nodeEngine = nodeEngine;
        this.logger = nodeEngine.getLogger(LocalRetryableExecution.class);
        this.invocationMaxRetryCount = nodeEngine.getProperties().getInteger(GroupProperty.INVOCATION_MAX_RETRY_COUNT);
        this.invocationRetryPauseMillis = nodeEngine.getProperties().getMillis(GroupProperty.INVOCATION_RETRY_PAUSE);
        this.op = op;
        op.setOperationResponseHandler(this);
    }

    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
        return this.done.await(timeout, unit);
    }

    public Object getResponse() {
        return this.response;
    }

    @Override
    public void run() {
        this.nodeEngine.getOperationService().execute(this.op);
    }

    public void sendResponse(Operation op, Object response) {
        ++this.tryCount;
        if (response instanceof RetryableHazelcastException && this.tryCount < this.invocationMaxRetryCount) {
            Level level;
            Level level2 = level = this.tryCount > 99 ? Level.WARNING : Level.FINEST;
            if (this.logger.isLoggable(level)) {
                this.logger.log(level, "Retrying local execution: " + this.toString() + ", Reason: " + response);
            }
            this.nodeEngine.getExecutionService().schedule(this, this.invocationRetryPauseMillis, TimeUnit.MILLISECONDS);
        } else {
            this.response = response;
            this.done.countDown();
        }
    }
}

