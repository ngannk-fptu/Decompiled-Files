/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.io.ByteBufferPool
 *  org.eclipse.jetty.util.BufferUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.client.util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.eclipse.jetty.client.util.AbstractTypedContentProvider;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.util.BufferUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class PathContentProvider
extends AbstractTypedContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PathContentProvider.class);
    private final Path filePath;
    private final long fileSize;
    private final int bufferSize;
    private ByteBufferPool bufferPool;
    private boolean useDirectByteBuffers = true;

    public PathContentProvider(Path filePath) throws IOException {
        this(filePath, 4096);
    }

    public PathContentProvider(Path filePath, int bufferSize) throws IOException {
        this("application/octet-stream", filePath, bufferSize);
    }

    public PathContentProvider(String contentType, Path filePath) throws IOException {
        this(contentType, filePath, 4096);
    }

    public PathContentProvider(String contentType, Path filePath, int bufferSize) throws IOException {
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
    public Iterator<ByteBuffer> iterator() {
        return new PathIterator();
    }

    private class PathIterator
    implements Iterator<ByteBuffer>,
    Closeable {
        private ByteBuffer buffer;
        private SeekableByteChannel channel;
        private long position;

        private PathIterator() {
        }

        @Override
        public boolean hasNext() {
            return this.position < PathContentProvider.this.getLength();
        }

        @Override
        public ByteBuffer next() {
            try {
                if (this.channel == null) {
                    this.buffer = PathContentProvider.this.bufferPool == null ? BufferUtil.allocate((int)PathContentProvider.this.bufferSize, (boolean)PathContentProvider.this.isUseDirectByteBuffers()) : PathContentProvider.this.bufferPool.acquire(PathContentProvider.this.bufferSize, PathContentProvider.this.isUseDirectByteBuffers());
                    this.channel = Files.newByteChannel(PathContentProvider.this.filePath, StandardOpenOption.READ);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Opened file {}", (Object)PathContentProvider.this.filePath);
                    }
                }
                this.buffer.clear();
                int read = this.channel.read(this.buffer);
                if (read < 0) {
                    throw new NoSuchElementException();
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Read {} bytes from {}", (Object)read, (Object)PathContentProvider.this.filePath);
                }
                this.position += (long)read;
                this.buffer.flip();
                return this.buffer;
            }
            catch (NoSuchElementException x) {
                this.close();
                throw x;
            }
            catch (Throwable x) {
                this.close();
                throw (NoSuchElementException)new NoSuchElementException().initCause(x);
            }
        }

        @Override
        public void close() {
            try {
                if (PathContentProvider.this.bufferPool != null && this.buffer != null) {
                    PathContentProvider.this.bufferPool.release(this.buffer);
                }
                if (this.channel != null) {
                    this.channel.close();
                }
            }
            catch (Throwable x) {
                LOG.trace("IGNORED", x);
            }
        }
    }
}

