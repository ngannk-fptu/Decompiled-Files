/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.CyclicTimeout
 *  org.eclipse.jetty.util.NanoTime
 *  org.eclipse.jetty.util.thread.Scheduler
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.client.HttpRequest;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.io.CyclicTimeout;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class TimeoutCompleteListener
extends CyclicTimeout
implements Response.CompleteListener {
    private static final Logger LOG = LoggerFactory.getLogger(TimeoutCompleteListener.class);
    private final AtomicReference<Request> requestTimeout = new AtomicReference();

    public TimeoutCompleteListener(Scheduler scheduler) {
        super(scheduler);
    }

    public void onTimeoutExpired() {
        Request request = this.requestTimeout.getAndSet(null);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Total timeout {} ms elapsed for {} on {}", new Object[]{request.getTimeout(), request, this});
        }
        if (request != null) {
            request.abort(new TimeoutException("Total timeout " + request.getTimeout() + " ms elapsed"));
        }
    }

    @Override
    public void onComplete(Result result) {
        Request request = this.requestTimeout.getAndSet(null);
        if (request != null) {
            boolean cancelled = this.cancel();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cancelled ({}) timeout for {} on {}", new Object[]{cancelled, request, this});
            }
        }
    }

    void schedule(HttpRequest request, long timeoutAt) {
        if (this.requestTimeout.compareAndSet(null, request)) {
            long delay = Math.max(0L, NanoTime.until((long)timeoutAt));
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scheduling timeout in {} ms for {} on {}", new Object[]{TimeUnit.NANOSECONDS.toMillis(delay), request, this});
            }
            this.schedule(delay, TimeUnit.NANOSECONDS);
        }
    }
}

