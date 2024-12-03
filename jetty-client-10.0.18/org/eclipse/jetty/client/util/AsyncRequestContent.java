/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.thread.AutoLock
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.util;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Condition;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncRequestContent
implements Request.Content,
Request.Content.Subscription,
Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(AsyncRequestContent.class);
    private final AutoLock lock = new AutoLock();
    private final Condition flush = this.lock.newCondition();
    private final Deque<Chunk> chunks = new ArrayDeque<Chunk>();
    private final String contentType;
    private long length = -1L;
    private Request.Content.Consumer consumer;
    private boolean emitInitialContent;
    private int demand;
    private boolean stalled;
    private boolean committed;
    private boolean closed;
    private boolean terminated;
    private Throwable failure;

    public AsyncRequestContent(ByteBuffer ... buffers) {
        this("application/octet-stream", buffers);
    }

    public AsyncRequestContent(String contentType, ByteBuffer ... buffers) {
        this.contentType = contentType;
        Stream.of(buffers).forEach(this::offer);
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public long getLength() {
        return this.length;
    }

    @Override
    public Request.Content.Subscription subscribe(Request.Content.Consumer consumer, boolean emitInitialContent) {
        try (AutoLock ignored = this.lock.lock();){
            if (this.consumer != null) {
                throw new IllegalStateException("Multiple subscriptions not supported on " + this);
            }
            this.consumer = consumer;
            this.emitInitialContent = emitInitialContent;
            this.stalled = true;
            if (this.closed) {
                this.length = this.chunks.stream().mapToLong(chunk -> chunk.buffer.remaining()).sum();
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Content subscription for {}: {}", (Object)this, (Object)consumer);
        }
        return this;
    }

    @Override
    public void demand() {
        boolean produce;
        try (AutoLock ignored = this.lock.lock();){
            ++this.demand;
            produce = this.stalled;
            if (this.stalled) {
                this.stalled = false;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Content demand, producing {} for {}", (Object)produce, (Object)this);
        }
        if (produce) {
            this.produce();
        }
    }

    @Override
    public void fail(Throwable failure) {
        List<Object> toFail = List.of();
        try (AutoLock l = this.lock.lock();){
            if (this.failure == null) {
                this.failure = failure;
                toFail = this.chunks.stream().map(chunk -> chunk.callback).collect(Collectors.toList());
                this.chunks.clear();
                this.flush.signal();
            }
        }
        toFail.forEach(c -> c.failed(failure));
    }

    public boolean offer(ByteBuffer buffer) {
        return this.offer(buffer, Callback.NOOP);
    }

    public boolean offer(ByteBuffer buffer, Callback callback) {
        return this.offer(new Chunk(buffer, callback));
    }

    private boolean offer(Chunk chunk) {
        Throwable failure;
        boolean produce = false;
        try (AutoLock ignored = this.lock.lock();){
            failure = this.failure;
            if (failure == null) {
                if (this.closed) {
                    failure = new IOException("closed");
                } else {
                    this.chunks.offer(chunk);
                    if (this.demand > 0 && this.stalled) {
                        this.stalled = false;
                        produce = true;
                    }
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Content offer {}, producing {} for {}", new Object[]{failure == null ? "succeeded" : "failed", produce, this, failure});
        }
        if (failure != null) {
            chunk.callback.failed(failure);
            return false;
        }
        if (produce) {
            this.produce();
        }
        return true;
    }

    private void produce() {
        while (true) {
            Throwable failure;
            try (AutoLock ignored = this.lock.lock();){
                failure = this.failure;
            }
            if (failure != null) {
                this.notifyFailure(this.consumer, failure);
                return;
            }
            try {
                boolean noDemand;
                Request.Content.Consumer consumer;
                Chunk chunk = Chunk.EMPTY;
                boolean lastContent = false;
                try (AutoLock ignored = this.lock.lock();){
                    if (this.terminated) {
                        throw new EOFException("Demand after last content");
                    }
                    consumer = this.consumer;
                    if (this.committed || this.emitInitialContent) {
                        chunk = this.chunks.poll();
                        boolean bl = lastContent = this.closed && this.chunks.isEmpty();
                        if (lastContent) {
                            this.terminated = true;
                        }
                    }
                    if (chunk == null && (lastContent || !this.committed)) {
                        chunk = Chunk.EMPTY;
                    }
                    if (chunk == null) {
                        this.stalled = true;
                    } else {
                        --this.demand;
                        this.committed = true;
                    }
                }
                if (chunk == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("No content, processing stalled for {}", (Object)this);
                    }
                    return;
                }
                this.notifyContent(consumer, chunk.buffer, lastContent, Callback.from(this::notifyFlush, (Callback)chunk.callback));
                try (AutoLock ignored = this.lock.lock();){
                    boolean bl = noDemand = this.demand == 0;
                    if (noDemand) {
                        this.stalled = true;
                    }
                }
                if (!noDemand) continue;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("No demand, processing stalled for {}", (Object)this);
                }
                return;
            }
            catch (Throwable x) {
                this.fail(x);
                continue;
            }
            break;
        }
    }

    private void notifyContent(Request.Content.Consumer consumer, ByteBuffer buffer, boolean last, Callback callback) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Notifying content last={} {} for {}", new Object[]{last, BufferUtil.toDetailString((ByteBuffer)buffer), this});
            }
            consumer.onContent(buffer, last, callback);
        }
        catch (Throwable x) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Failure while notifying content", x);
            }
            callback.failed(x);
            this.fail(x);
        }
    }

    private void notifyFailure(Request.Content.Consumer consumer, Throwable failure) {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Notifying failure for {}", (Object)this, (Object)failure);
            }
            consumer.onFailure(failure);
        }
        catch (Throwable x) {
            LOG.trace("Failure while notifying content failure {}", (Object)failure, (Object)x);
        }
    }

    private void notifyFlush() {
        try (AutoLock l = this.lock.lock();){
            this.flush.signal();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void flush() throws IOException {
        try (AutoLock l = this.lock.lock();){
            while (true) {
                if (this.failure != null) {
                    throw new IOException(this.failure);
                }
                if (this.chunks.isEmpty()) {
                    return;
                }
                this.flush.await();
                continue;
                break;
            }
        }
    }

    @Override
    public void close() {
        boolean produce = false;
        try (AutoLock l = this.lock.lock();){
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (this.demand > 0 && this.stalled) {
                this.stalled = false;
                produce = true;
            }
            this.flush.signal();
        }
        if (produce) {
            this.produce();
        }
    }

    public boolean isClosed() {
        try (AutoLock ignored = this.lock.lock();){
            boolean bl = this.closed;
            return bl;
        }
    }

    public String toString() {
        int chunks;
        boolean stalled;
        int demand;
        try (AutoLock ignored = this.lock.lock();){
            demand = this.demand;
            stalled = this.stalled;
            chunks = this.chunks.size();
        }
        return String.format("%s@%x[demand=%d,stalled=%b,chunks=%d]", this.getClass().getSimpleName(), this.hashCode(), demand, stalled, chunks);
    }

    private static class Chunk {
        private static final Chunk EMPTY = new Chunk(BufferUtil.EMPTY_BUFFER, Callback.NOOP);
        private final ByteBuffer buffer;
        private final Callback callback;

        private Chunk(ByteBuffer buffer, Callback callback) {
            this.buffer = Objects.requireNonNull(buffer);
            this.callback = Objects.requireNonNull(callback);
        }
    }
}

