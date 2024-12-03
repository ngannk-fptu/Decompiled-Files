/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.ByteBufferPool
 *  org.eclipse.jetty.util.BufferUtil
 *  org.eclipse.jetty.util.Callback
 *  org.eclipse.jetty.util.IO
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.util;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.AbstractRequestContent;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathRequestContent
extends AbstractRequestContent {
    private static final Logger LOG = LoggerFactory.getLogger(PathRequestContent.class);
    private final Path filePath;
    private final long fileSize;
    private final int bufferSize;
    private ByteBufferPool bufferPool;
    private boolean useDirectByteBuffers = true;

    public PathRequestContent(Path filePath) throws IOException {
        this(filePath, 4096);
    }

    public PathRequestContent(Path filePath, int bufferSize) throws IOException {
        this("application/octet-stream", filePath, bufferSize);
    }

    public PathRequestContent(String contentType, Path filePath) throws IOException {
        this(contentType, filePath, 4096);
    }

    public PathRequestContent(String contentType, Path filePath, int bufferSize) throws IOException {
        super(contentType);
        if (!Files.isRegularFile(filePath, new LinkOption[0])) {
            throw new NoSuchFileException(filePath.toString());
        }
        if (!Files.isReadable(filePath)) {
            throw new AccessDeniedException(filePath.toString());
        }
        this.filePath = filePath;
        this.fileSize = Files.size(filePath);
        this.bufferSize = bufferSize;
    }

    @Override
    public long getLength() {
        return this.fileSize;
    }

    @Override
    public boolean isReproducible() {
        return true;
    }

    public ByteBufferPool getByteBufferPool() {
        return this.bufferPool;
    }

    public void setByteBufferPool(ByteBufferPool byteBufferPool) {
        this.bufferPool = byteBufferPool;
    }

    public boolean isUseDirectByteBuffers() {
        return this.useDirectByteBuffers;
    }

    public void setUseDirectByteBuffers(boolean useDirectByteBuffers) {
        this.useDirectByteBuffers = useDirectByteBuffers;
    }

    @Override
    protected Request.Content.Subscription newSubscription(Request.Content.Consumer consumer, boolean emitInitialContent) {
        return new SubscriptionImpl(consumer, emitInitialContent);
    }

    private class SubscriptionImpl
    extends AbstractRequestContent.AbstractSubscription {
        private ReadableByteChannel channel;
        private long readTotal;

        private SubscriptionImpl(Request.Content.Consumer consumer, boolean emitInitialContent) {
            super(PathRequestContent.this, consumer, emitInitialContent);
        }

        @Override
        protected boolean produceContent(AbstractRequestContent.Producer producer) throws IOException {
            boolean last;
            if (this.channel == null) {
                this.channel = Files.newByteChannel(PathRequestContent.this.filePath, StandardOpenOption.READ);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Opened file {}", (Object)PathRequestContent.this.filePath);
                }
            }
            ByteBuffer buffer = PathRequestContent.this.bufferPool == null ? BufferUtil.allocate((int)PathRequestContent.this.bufferSize, (boolean)PathRequestContent.this.isUseDirectByteBuffers()) : PathRequestContent.this.bufferPool.acquire(PathRequestContent.this.bufferSize, PathRequestContent.this.isUseDirectByteBuffers());
            BufferUtil.clearToFill((ByteBuffer)buffer);
            int read = this.channel.read(buffer);
            BufferUtil.flipToFlush((ByteBuffer)buffer, (int)0);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Read {} bytes from {}", (Object)read, (Object)PathRequestContent.this.filePath);
            }
            if (!this.channel.isOpen() && read < 0) {
                throw new EOFException("EOF reached for " + PathRequestContent.this.filePath);
            }
            if (read > 0) {
                this.readTotal += (long)read;
            }
            boolean bl = last = this.readTotal == PathRequestContent.this.fileSize;
            if (last) {
                IO.close((Closeable)this.channel);
            }
            return producer.produce(buffer, last, Callback.from(() -> this.release(buffer)));
        }

        private void release(ByteBuffer buffer) {
            if (PathRequestContent.this.bufferPool != null) {
                PathRequestContent.this.bufferPool.release(buffer);
            }
        }

        @Override
        public void fail(Throwable failure) {
            super.fail(failure);
            IO.close((Closeable)this.channel);
        }
    }
}

