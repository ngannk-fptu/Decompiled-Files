/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.annotation.Contract
 *  org.apache.hc.core5.annotation.ThreadingBehavior
 *  org.apache.hc.core5.concurrent.DefaultThreadFactory
 *  org.apache.hc.core5.pool.ConnPoolControl
 *  org.apache.hc.core5.util.Args
 *  org.apache.hc.core5.util.TimeValue
 *  org.apache.hc.core5.util.Timeout
 */
package org.apache.hc.client5.http.impl;

import java.util.concurrent.ThreadFactory;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;
import org.apache.hc.core5.concurrent.DefaultThreadFactory;
import org.apache.hc.core5.pool.ConnPoolControl;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

@Contract(threading=ThreadingBehavior.SAFE_CONDITIONAL)
public final class IdleConnectionEvictor {
    private final ThreadFactory threadFactory;
    private final Thread thread;

    public IdleConnectionEvictor(ConnPoolControl<?> connectionManager, ThreadFactory threadFactory, TimeValue sleepTime, TimeValue maxIdleTime) {
        Args.notNull(connectionManager, (String)"Connection manager");
        this.threadFactory = threadFactory != null ? threadFactory : new DefaultThreadFactory("idle-connection-evictor", true);
        TimeValue localSleepTime = sleepTime != null ? sleepTime : TimeValue.ofSeconds((long)5L);
        this.thread = this.threadFactory.newThread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    localSleepTime.sleep();
                    connectionManager.closeExpired();
                    if (maxIdleTime == null) continue;
                    connectionManager.closeIdle(maxIdleTime);
                }
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            catch (Exception exception) {
                // empty catch block
            }
        });
    }

    public IdleConnectionEvictor(ConnPoolControl<?> connectionManager, TimeValue sleepTime, TimeValue maxIdleTime) {
        this(connectionManager, null, sleepTime, maxIdleTime);
    }

    public IdleConnectionEvictor(ConnPoolControl<?> connectionManager, TimeValue maxIdleTime) {
        this(connectionManager, null, maxIdleTime, maxIdleTime);
    }

    public void start() {
        this.thread.start();
    }

    public void shutdown() {
        this.thread.interrupt();
    }

    public boolean isRunning() {
        return this.thread.isAlive();
    }

    public void awaitTermination(Timeout timeout) throws InterruptedException {
        this.thread.join(timeout != null ? timeout.toMilliseconds() : Long.MAX_VALUE);
    }
}

