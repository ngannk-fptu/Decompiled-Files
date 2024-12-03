/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 */
package org.eclipse.jetty.client.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.jetty.client.AsyncContentProvider;
import org.eclipse.jetty.client.Synchronizable;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;

@Deprecated
public class DeferredContentProvider
implements AsyncContentProvider,
Callback,
Closeable {
    private static final Chunk CLOSE = new Chunk(BufferUtil.EMPTY_BUFFER, Callback.NOOP);
    private final Object lock = this;
    private final Deque<Chunk> chunks = new ArrayDeque<Chunk>();
    private final AtomicReference<AsyncContentProvider.Listener> listener = new AtomicReference();
    private final DeferredContentProviderIterator iterator = new DeferredContentProviderIterator();
    private final AtomicBoolean closed = new AtomicBoolean();
    private long length = -1L;
    private int size;
    private Throwable failure;

    public DeferredContentProvider(ByteBuffer ... buffers) {
        for (ByteBuffer buffer : buffers) {
            this.offer(buffer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setListener(AsyncContentProvider.Listener listener) {
        if (!this.listener.compareAndSet(null, listener)) {
            throw new IllegalStateException(String.format("The same %s instance cannot be used in multiple requests", AsyncContentProvider.class.getName()));
        }
        if (this.isClosed()) {
            Object object = this.lock;
            synchronized (object) {
                long total = 0L;
                for (Chunk chunk : this.chunks) {
                    total += (long)chunk.buffer.remaining();
                }
                this.length = total;
            }
        }
    }

    @Override
    public long getLength() {
        return this.length;
    }

    public boolean offer(ByteBuffer buffer) {
        return this.offer(buffer, Callback.NOOP);
    }

    public boolean offer(ByteBuffer buffer, Callback callback) {
        return this.offer(new Chunk(buffer, callback));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean offer(Chunk chunk) {
        Throwable failure;
        boolean result = false;
        Object object = this.lock;
        synchronized (object) {
            failure = this.failure;
            if (failure == null && (result = this.chunks.offer(chunk)) && chunk != CLOSE) {
                ++this.size;
            }
        }
        if (failure != null) {
            chunk.callback.failed(failure);
        } else if (result) {
            this.notifyListener();
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void clear() {
        Object object = this.lock;
        synchronized (object) {
            this.chunks.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void flush() throws IOException {
        Object object = this.lock;
        synchronized (object) {
            try {
                while (true) {
                    if (this.failure != null) {
                        throw new IOException(this.failure);
                    }
                    if (this.size != 0) {
                        this.lock.wait();
                        continue;
                    }
                    break;
                }
            }
            catch (InterruptedException x) {
                throw new InterruptedIOException();
            }
        }
    }

    @Override
    public void close() {
        if (this.closed.compareAndSet(false, true)) {
            this.offer(CLOSE);
        }
    }

    public boolean isClosed() {
        return this.closed.get();
    }

    public void failed(Throwable failure) {
        this.iterator.failed(failure);
    }

    private void notifyListener() {
        AsyncContentProvider.Listener listener = this.listener.get();
        if (listener != null) {
            listener.onContent();
        }
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        return this.iterator;
    }

    private class DeferredContentProviderIterator
    implements Iterator<ByteBuffer>,
    Callback,
    Synchronizable {
        private Chunk current;

        private DeferredContentProviderIterator() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasNext() {
            Object object = DeferredContentProvider.this.lock;
            synchronized (object) {
                return DeferredContentProvider.this.chunks.peek() != CLOSE;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public ByteBuffer next() {
            Object object = DeferredContentProvider.this.lock;
            synchronized (object) {
                Chunk chunk = this.current = DeferredContentProvider.this.chunks.poll();
                if (chunk == CLOSE) {
                    DeferredContentProvider.this.chunks.offerFirst(CLOSE);
                    throw new NoSuchElementException();
                }
                return chunk == null ? null : chunk.buffer;
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void succeeded() {
            Chunk chunk;
            Object object = DeferredContentProvider.this.lock;
            synchronized (object) {
                chunk = this.current;
                this.current = null;
                if (chunk != null) {
                    --DeferredContentProvider.this.size;
                    DeferredContentProvider.this.lock.notify();
                }
            }
            if (chunk != null) {
                chunk.callback.succeeded();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void failed(Throwable x) {
            ArrayList<Chunk> chunks = new ArrayList<Chunk>();
            Iterator iterator = DeferredContentProvider.this.lock;
            synchronized (iterator) {
                DeferredContentProvider.this.failure = x;
                Chunk chunk = this.current;
                this.current = null;
                if (chunk != null) {
                    chunks.add(chunk);
                }
                chunks.addAll(DeferredContentProvider.this.chunks);
                DeferredContentProvider.this.clear();
                DeferredContentProvider.this.lock.notify();
            }
            for (Chunk chunk : chunks) {
                chunk.callback.failed(x);
            }
        }

        @Override
        public Object getLock() {
            return DeferredContentProvider.this.lock;
        }
    }

    public static class Chunk {
        public final ByteBuffer buffer;
        public final Callback callback;

        public Chunk(ByteBuffer buffer, Callback callback) {
            this.buffer = Objects.requireNonNull(buffer);
            this.callback = Objects.requireNonNull(callback);
        }

        public String toString() {
            return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
        }
    }
}

