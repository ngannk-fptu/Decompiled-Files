/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.IO
 *  org.eclipse.jetty.util.thread.AutoLock$WithCondition
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.api.Result;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputStreamResponseListener
extends Response.Listener.Adapter {
    private static final Logger LOG = LoggerFactory.getLogger(InputStreamResponseListener.class);
    private static final Chunk EOF = new Chunk(BufferUtil.EMPTY_BUFFER, Callback.NOOP);
    private final AutoLock.WithCondition lock = new AutoLock.WithCondition();
    private final CountDownLatch responseLatch = new CountDownLatch(1);
    private final CountDownLatch resultLatch = new CountDownLatch(1);
    private final AtomicReference<InputStream> stream = new AtomicReference();
    private final Queue<Chunk> chunks = new ArrayDeque<Chunk>();
    private Response response;
    private Result result;
    private Throwable failure;
    private boolean closed;

    @Override
    public void onHeaders(Response response) {
        try (AutoLock.WithCondition ignored = this.lock.lock();){
            this.response = response;
            this.responseLatch.countDown();
        }
    }

    @Override
    public void onContent(Response response, ByteBuffer content, Callback callback) {
        boolean closed;
        if (content.remaining() == 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skipped empty content {}", (Object)content);
            }
            callback.succeeded();
            return;
        }
        try (AutoLock.WithCondition l = this.lock.lock();){
            closed = this.closed;
            if (!closed) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Queueing content {}", (Object)content);
                }
                this.chunks.add(new Chunk(content, callback));
                l.signalAll();
            }
        }
        if (closed) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("InputStream closed, ignored content {}", (Object)content);
            }
            callback.failed((Throwable)new AsynchronousCloseException());
        }
    }

    @Override
    public void onSuccess(Response response) {
        try (AutoLock.WithCondition l = this.lock.lock();){
            if (!this.closed) {
                this.chunks.add(EOF);
            }
            l.signalAll();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("End of content");
        }
    }

    @Override
    public void onFailure(Response response, Throwable failure) {
        List<Callback> callbacks;
        try (AutoLock.WithCondition l = this.lock.lock();){
            if (this.failure != null) {
                return;
            }
            this.failure = failure;
            callbacks = this.drain();
            l.signalAll();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Content failure", failure);
        }
        callbacks.forEach(callback -> callback.failed(failure));
    }

    @Override
    public void onComplete(Result result) {
        Throwable failure = result.getFailure();
        List<Object> callbacks = Collections.emptyList();
        try (AutoLock.WithCondition l = this.lock.lock();){
            this.result = result;
            if (result.isFailed() && this.failure == null) {
                this.failure = failure;
                callbacks = this.drain();
            }
            this.responseLatch.countDown();
            this.resultLatch.countDown();
            l.signalAll();
        }
        if (LOG.isDebugEnabled()) {
            if (failure == null) {
                LOG.debug("Result success");
            } else {
                LOG.debug("Result failure", failure);
            }
        }
        callbacks.forEach(callback -> callback.failed(failure));
    }

    public Response get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        boolean expired;
        boolean bl = expired = !this.responseLatch.await(timeout, unit);
        if (expired) {
            throw new TimeoutException();
        }
        try (AutoLock.WithCondition ignored = this.lock.lock();){
            if (this.response == null) {
                throw new ExecutionException(this.failure);
            }
            Response response = this.response;
            return response;
        }
    }

    public Result await(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        boolean expired;
        boolean bl = expired = !this.resultLatch.await(timeout, unit);
        if (expired) {
            throw new TimeoutException();
        }
        try (AutoLock.WithCondition ignored = this.lock.lock();){
            Result result = this.result;
            return result;
        }
    }

    public InputStream getInputStream() {
        Input result = new Input();
        if (this.stream.compareAndSet(null, result)) {
            return result;
        }
        return IO.getClosedStream();
    }

    private List<Callback> drain() {
        ArrayList<Callback> callbacks = new ArrayList<Callback>();
        try (AutoLock.WithCondition ignored = this.lock.lock();){
            Chunk chunk;
            while ((chunk = this.chunks.peek()) != null) {
                if (chunk == EOF) {
                    break;
                }
                callbacks.add(chunk.callback);
                this.chunks.poll();
            }
        }
        return callbacks;
    }

    private static class Chunk {
        private final ByteBuffer buffer;
        private final Callback callback;

        private Chunk(ByteBuffer buffer, Callback callback) {
            this.buffer = Objects.requireNonNull(buffer);
            this.callback = Objects.requireNonNull(callback);
        }
    }

    private class Input
    extends InputStream {
        private Input() {
        }

        @Override
        public int read() throws IOException {
            byte[] tmp = new byte[1];
            int read = this.read(tmp);
            if (read < 0) {
                return read;
            }
            return tmp[0] & 0xFF;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public int read(byte[] b, int offset, int length) throws IOException {
            try {
                int result;
                Callback callback;
                block15: {
                    callback = null;
                    try (AutoLock.WithCondition l = InputStreamResponseListener.this.lock.lock();){
                        Chunk chunk;
                        while (true) {
                            if ((chunk = InputStreamResponseListener.this.chunks.peek()) == EOF) {
                                int n = -1;
                                return n;
                            }
                            if (chunk != null) {
                                ByteBuffer buffer = chunk.buffer;
                                result = Math.min(buffer.remaining(), length);
                                buffer.get(b, offset, result);
                                if (!buffer.hasRemaining()) {
                                    break;
                                }
                                break block15;
                            }
                            if (InputStreamResponseListener.this.failure != null) {
                                throw this.toIOException(InputStreamResponseListener.this.failure);
                            }
                            if (InputStreamResponseListener.this.closed) {
                                throw new AsynchronousCloseException();
                            }
                            l.await();
                        }
                        callback = chunk.callback;
                        InputStreamResponseListener.this.chunks.poll();
                    }
                }
                if (callback == null) return result;
                callback.succeeded();
                return result;
            }
            catch (InterruptedException x) {
                throw new InterruptedIOException();
            }
        }

        private IOException toIOException(Throwable failure) {
            if (failure instanceof IOException) {
                return (IOException)failure;
            }
            return new IOException(failure);
        }

        @Override
        public void close() throws IOException {
            List<Callback> callbacks;
            try (AutoLock.WithCondition l = InputStreamResponseListener.this.lock.lock();){
                if (InputStreamResponseListener.this.closed) {
                    return;
                }
                InputStreamResponseListener.this.closed = true;
                callbacks = InputStreamResponseListener.this.drain();
                l.signalAll();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("InputStream close");
            }
            if (!callbacks.isEmpty()) {
                AsynchronousCloseException failure = new AsynchronousCloseException();
                callbacks.forEach(callback -> callback.failed(failure));
            }
            super.close();
        }
    }
}

