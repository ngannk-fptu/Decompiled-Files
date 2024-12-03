/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class InputStreamContentProvider
implements ContentProvider,
Callback,
Closeable {
    private static final Logger LOG = LoggerFactory.getLogger(InputStreamContentProvider.class);
    private final InputStreamContentProviderIterator iterator = new InputStreamContentProviderIterator();
    private final InputStream stream;
    private final int bufferSize;
    private final boolean autoClose;

    public InputStreamContentProvider(InputStream stream) {
        this(stream, 4096);
    }

    public InputStreamContentProvider(InputStream stream, int bufferSize) {
        this(stream, bufferSize, true);
    }

    public InputStreamContentProvider(InputStream stream, int bufferSize, boolean autoClose) {
        this.stream = stream;
        this.bufferSize = bufferSize;
        this.autoClose = autoClose;
    }

    @Override
    public long getLength() {
        return -1L;
    }

    protected ByteBuffer onRead(byte[] buffer, int offset, int length) {
        if (length <= 0) {
            return BufferUtil.EMPTY_BUFFER;
        }
        return ByteBuffer.wrap(buffer, offset, length);
    }

    protected void onReadFailure(Throwable failure) {
    }

    @Override
    public Iterator<ByteBuffer> iterator() {
        return this.iterator;
    }

    @Override
    public void close() {
        if (this.autoClose) {
            try {
                this.stream.close();
            }
            catch (IOException x) {
                LOG.trace("IGNORED", (Throwable)x);
            }
        }
    }

    public void failed(Throwable failure) {
        this.close();
    }

    private class InputStreamContentProviderIterator
    implements Iterator<ByteBuffer>,
    Closeable {
        private Throwable failure;
        private ByteBuffer buffer;
        private Boolean hasNext;

        private InputStreamContentProviderIterator() {
        }

        @Override
        public boolean hasNext() {
            try {
                if (this.hasNext != null) {
                    return this.hasNext;
                }
                byte[] bytes = new byte[InputStreamContentProvider.this.bufferSize];
                int read = InputStreamContentProvider.this.stream.read(bytes);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Read {} bytes from {}", (Object)read, (Object)InputStreamContentProvider.this.stream);
                }
                if (read > 0) {
                    this.hasNext = Boolean.TRUE;
                    this.buffer = InputStreamContentProvider.this.onRead(bytes, 0, read);
                    return true;
                }
                if (read < 0) {
                    this.hasNext = Boolean.FALSE;
                    this.buffer = null;
                    this.close();
                    return false;
                }
                this.hasNext = Boolean.TRUE;
                this.buffer = BufferUtil.EMPTY_BUFFER;
                return true;
            }
            catch (Throwable x) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Failed to read", x);
                }
                if (this.failure == null) {
                    this.failure = x;
                    InputStreamContentProvider.this.onReadFailure(x);
                    this.hasNext = Boolean.TRUE;
                    this.buffer = null;
                    this.close();
                    return true;
                }
                throw new IllegalStateException();
            }
        }

        @Override
        public ByteBuffer next() {
            if (this.failure != null) {
                this.hasNext = Boolean.FALSE;
                this.buffer = null;
                throw (NoSuchElementException)new NoSuchElementException().initCause(this.failure);
            }
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            ByteBuffer result = this.buffer;
            if (result == null) {
                this.hasNext = Boolean.FALSE;
                this.buffer = null;
                throw new NoSuchElementException();
            }
            this.hasNext = null;
            this.buffer = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            InputStreamContentProvider.this.close();
        }
    }
}

