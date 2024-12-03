/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.nio.entity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.DataStreamChannel;
import org.apache.hc.core5.io.Closer;
import org.apache.hc.core5.util.Args;
import org.apache.hc.core5.util.Asserts;

public final class PathEntityProducer
implements AsyncEntityProducer {
    private static final int BUFFER_SIZE = 8192;
    private final Path file;
    private final OpenOption[] openOptions;
    private final ByteBuffer byteBuffer;
    private final long length;
    private final ContentType contentType;
    private final boolean chunked;
    private final AtomicReference<Exception> exception;
    private final AtomicReference<SeekableByteChannel> channelRef;
    private boolean eof;

    public PathEntityProducer(Path file, ContentType contentType, boolean chunked, OpenOption ... openOptions) throws IOException {
        this(file, 8192, contentType, chunked, openOptions);
    }

    public PathEntityProducer(Path file, ContentType contentType, OpenOption ... openOptions) throws IOException {
        this(file, contentType, false, openOptions);
    }

    public PathEntityProducer(Path file, int bufferSize, ContentType contentType, boolean chunked, OpenOption ... openOptions) throws IOException {
        this.file = Args.notNull(file, "file");
        this.openOptions = openOptions;
        this.length = Files.size(file);
        this.byteBuffer = ByteBuffer.allocate(bufferSize);
        this.contentType = contentType;
        this.chunked = chunked;
        this.channelRef = new AtomicReference();
        this.exception = new AtomicReference();
    }

    public PathEntityProducer(Path file, OpenOption ... openOptions) throws IOException {
        this(file, ContentType.APPLICATION_OCTET_STREAM, openOptions);
    }

    @Override
    public int available() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void failed(Exception cause) {
        if (this.exception.compareAndSet(null, cause)) {
            this.releaseResources();
        }
    }

    @Override
    public String getContentEncoding() {
        return null;
    }

    @Override
    public long getContentLength() {
        return this.length;
    }

    @Override
    public String getContentType() {
        return this.contentType != null ? this.contentType.toString() : null;
    }

    public Exception getException() {
        return this.exception.get();
    }

    @Override
    public Set<String> getTrailerNames() {
        return null;
    }

    @Override
    public boolean isChunked() {
        return this.chunked;
    }

    @Override
    public boolean isRepeatable() {
        return true;
    }

    @Override
    public void produce(DataStreamChannel dataStreamChannel) throws IOException {
        int bytesRead;
        SeekableByteChannel seekableByteChannel = this.channelRef.get();
        if (seekableByteChannel == null) {
            seekableByteChannel = Files.newByteChannel(this.file, this.openOptions);
            Asserts.check(this.channelRef.getAndSet(seekableByteChannel) == null, "Illegal producer state");
        }
        if (!this.eof && (bytesRead = seekableByteChannel.read(this.byteBuffer)) < 0) {
            this.eof = true;
        }
        if (this.byteBuffer.position() > 0) {
            this.byteBuffer.flip();
            dataStreamChannel.write(this.byteBuffer);
            this.byteBuffer.compact();
        }
        if (this.eof && this.byteBuffer.position() == 0) {
            dataStreamChannel.endStream();
            this.releaseResources();
        }
    }

    @Override
    public void releaseResources() {
        this.eof = false;
        Closer.closeQuietly(this.channelRef.getAndSet(null));
    }
}

