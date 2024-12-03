/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.websocket.SendHandler
 *  javax.websocket.SendResult
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.websocket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import javax.websocket.SendHandler;
import javax.websocket.SendResult;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.websocket.WsSession;

class FutureToSendHandler
implements Future<Void>,
SendHandler {
    private static final StringManager sm = StringManager.getManager(FutureToSendHandler.class);
    private final CountDownLatch latch = new CountDownLatch(1);
    private final WsSession wsSession;
    private volatile AtomicReference<SendResult> result = new AtomicReference<Object>(null);

    FutureToSendHandler(WsSession wsSession) {
        this.wsSession = wsSession;
    }

    public void onResult(SendResult result) {
        this.result.compareAndSet(null, result);
        this.latch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return this.latch.getCount() == 0L;
    }

    @Override
    public Void get() throws InterruptedException, ExecutionException {
        try {
            this.wsSession.registerFuture(this);
            this.latch.await();
        }
        finally {
            this.wsSession.unregisterFuture(this);
        }
        if (this.result.get().getException() != null) {
            throw new ExecutionException(this.result.get().getException());
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean retval = false;
        try {
            this.wsSession.registerFuture(this);
            retval = this.latch.await(timeout, unit);
        }
        finally {
            this.wsSession.unregisterFuture(this);
        }
        if (!retval) {
            throw new TimeoutException(sm.getString("futureToSendHandler.timeout", new Object[]{timeout, unit.toString().toLowerCase()}));
        }
        if (this.result.get().getException() != null) {
            throw new ExecutionException(this.result.get().getException());
        }
        return null;
    }
}

