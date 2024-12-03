/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.client.util;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.HttpContentResponse;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.client.util.BufferingResponseListener;

public class FutureResponseListener
extends BufferingResponseListener
implements Future<ContentResponse> {
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Request request;
    private ContentResponse response;
    private Throwable failure;
    private volatile boolean cancelled;

    public FutureResponseListener(Request request) {
        this(request, 0x200000);
    }

    public FutureResponseListener(Request request, int maxLength) {
        super(maxLength);
        this.request = request;
    }

    public Request getRequest() {
        return this.request;
    }

    @Override
    public void onComplete(Result result) {
        this.response = new HttpContentResponse(result.getResponse(), this.getContent(), this.getMediaType(), this.getEncoding());
        this.failure = result.getFailure();
        this.latch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.cancelled = true;
        return this.request.abort(new CancellationException());
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public boolean isDone() {
        return this.latch.getCount() == 0L || this.isCancelled();
    }

    @Override
    public ContentResponse get() throws InterruptedException, ExecutionException {
        this.latch.await();
        return this.getResult();
    }

    @Override
    public ContentResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean expired;
        boolean bl = expired = !this.latch.await(timeout, unit);
        if (expired) {
            throw new TimeoutException();
        }
        return this.getResult();
    }

    private ContentResponse getResult() throws ExecutionException {
        if (this.isCancelled()) {
            throw (CancellationException)new CancellationException().initCause(this.failure);
        }
        if (this.failure != null) {
            throw new ExecutionException(this.failure);
        }
        return this.response;
    }
}

