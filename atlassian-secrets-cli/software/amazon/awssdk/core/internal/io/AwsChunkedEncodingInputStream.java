/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.internal.chunked.AwsChunkedEncodingConfig;
import software.amazon.awssdk.core.internal.io.AwsChunkedInputStream;
import software.amazon.awssdk.core.internal.io.ChunkContentIterator;
import software.amazon.awssdk.core.internal.io.UnderlyingStreamBuffer;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public abstract class AwsChunkedEncodingInputStream
extends AwsChunkedInputStream {
    protected static final String CRLF = "\r\n";
    protected static final byte[] FINAL_CHUNK = new byte[0];
    protected static final String HEADER_COLON_SEPARATOR = ":";
    protected byte[] calculatedChecksum = null;
    protected final String checksumHeaderForTrailer;
    protected boolean isTrailingTerminated = true;
    private final int chunkSize;
    private final int maxBufferSize;
    private final SdkChecksum sdkChecksum;
    private boolean isLastTrailingCrlf;

    protected AwsChunkedEncodingInputStream(InputStream in, SdkChecksum sdkChecksum, String checksumHeaderForTrailer, AwsChunkedEncodingConfig config) {
        AwsChunkedEncodingConfig awsChunkedEncodingConfig = config == null ? AwsChunkedEncodingConfig.create() : config;
        int providedMaxBufferSize = awsChunkedEncodingConfig.bufferSize();
        if (in instanceof AwsChunkedEncodingInputStream) {
            AwsChunkedEncodingInputStream originalChunkedStream = (AwsChunkedEncodingInputStream)in;
            providedMaxBufferSize = Math.max(originalChunkedStream.maxBufferSize, providedMaxBufferSize);
            this.is = originalChunkedStream.is;
            this.underlyingStreamBuffer = originalChunkedStream.underlyingStreamBuffer;
        } else {
            this.is = in;
            this.underlyingStreamBuffer = null;
        }
        this.chunkSize = awsChunkedEncodingConfig.chunkSize();
        this.maxBufferSize = providedMaxBufferSize;
        if (this.maxBufferSize < this.chunkSize) {
            throw new IllegalArgumentException("Max buffer size should not be less than chunk size");
        }
        this.sdkChecksum = sdkChecksum;
        this.checksumHeaderForTrailer = checksumHeaderForTrailer;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count;
        this.abortIfNeeded();
        Validate.notNull(b, "buff", new Object[0]);
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (null == this.currentChunkIterator || !this.currentChunkIterator.hasNext()) {
            if (this.isTerminating && this.isTrailingTerminated) {
                return -1;
            }
            if (!this.isTerminating) {
                this.isTerminating = this.setUpNextChunk();
            } else {
                this.isTrailingTerminated = this.setUpTrailingChunks();
            }
        }
        if ((count = this.currentChunkIterator.read(b, off, len)) > 0) {
            this.isAtStart = false;
            log.trace(() -> count + " byte read from the stream.");
        }
        return count;
    }

    private boolean setUpTrailingChunks() {
        if (this.sdkChecksum == null) {
            return true;
        }
        if (this.calculatedChecksum == null) {
            this.calculatedChecksum = this.sdkChecksum.getChecksumBytes();
            this.currentChunkIterator = new ChunkContentIterator(this.createChecksumChunkHeader());
            return false;
        }
        if (!this.isLastTrailingCrlf) {
            this.currentChunkIterator = new ChunkContentIterator(CRLF.getBytes(StandardCharsets.UTF_8));
            this.isLastTrailingCrlf = true;
        }
        return true;
    }

    @Override
    public void mark(int readlimit) {
        this.abortIfNeeded();
        if (!this.isAtStart) {
            throw new UnsupportedOperationException("Chunk-encoded stream only supports mark() at the start of the stream.");
        }
        if (this.sdkChecksum != null) {
            this.sdkChecksum.mark(readlimit);
        }
        if (this.is.markSupported()) {
            log.debug(() -> "AwsChunkedEncodingInputStream marked at the start of the stream (will directly mark the wrapped stream since it's mark-supported).");
            this.is.mark(readlimit);
        } else {
            log.debug(() -> "AwsChunkedEncodingInputStream marked at the start of the stream (initializing the buffer since the wrapped stream is not mark-supported).");
            this.underlyingStreamBuffer = new UnderlyingStreamBuffer(this.maxBufferSize);
        }
    }

    @Override
    public void reset() throws IOException {
        this.abortIfNeeded();
        this.currentChunkIterator = null;
        if (this.sdkChecksum != null) {
            this.sdkChecksum.reset();
        }
        if (this.is.markSupported()) {
            log.debug(() -> "AwsChunkedEncodingInputStream reset (will reset the wrapped stream because it is mark-supported).");
            this.is.reset();
        } else {
            log.debug(() -> "AwsChunkedEncodingInputStream reset (will use the buffer of the decoded stream).");
            Validate.notNull(this.underlyingStreamBuffer, "Cannot reset the stream because the mark is not set.", new Object[0]);
            this.underlyingStreamBuffer.startReadBuffer();
        }
        this.isAtStart = true;
        this.isTerminating = false;
    }

    private boolean setUpNextChunk() throws IOException {
        byte[] chunkData = new byte[this.chunkSize];
        int chunkSizeInBytes = 0;
        while (chunkSizeInBytes < this.chunkSize) {
            if (null != this.underlyingStreamBuffer && this.underlyingStreamBuffer.hasNext()) {
                chunkData[chunkSizeInBytes++] = this.underlyingStreamBuffer.next();
                continue;
            }
            int bytesToRead = this.chunkSize - chunkSizeInBytes;
            int count = this.is.read(chunkData, chunkSizeInBytes, bytesToRead);
            if (count == -1) break;
            if (null != this.underlyingStreamBuffer) {
                this.underlyingStreamBuffer.buffer(chunkData, chunkSizeInBytes, count);
            }
            chunkSizeInBytes += count;
        }
        if (chunkSizeInBytes == 0) {
            if (this.sdkChecksum != null) {
                this.isTrailingTerminated = false;
            }
            byte[] finalChunk = this.createFinalChunk(FINAL_CHUNK);
            this.currentChunkIterator = new ChunkContentIterator(finalChunk);
            return true;
        }
        if (chunkSizeInBytes < chunkData.length) {
            chunkData = Arrays.copyOf(chunkData, chunkSizeInBytes);
        }
        byte[] chunkContent = this.createChunk(chunkData);
        this.currentChunkIterator = new ChunkContentIterator(chunkContent);
        if (this.sdkChecksum != null) {
            this.sdkChecksum.update(chunkData);
        }
        return false;
    }

    protected abstract byte[] createFinalChunk(byte[] var1);

    protected abstract byte[] createChunk(byte[] var1);

    protected abstract byte[] createChecksumChunkHeader();

    protected static abstract class Builder<T extends Builder> {
        protected InputStream inputStream;
        protected SdkChecksum sdkChecksum;
        protected String checksumHeaderForTrailer;
        protected AwsChunkedEncodingConfig awsChunkedEncodingConfig;

        protected Builder() {
        }

        public T inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return (T)this;
        }

        public T awsChunkedEncodingConfig(AwsChunkedEncodingConfig awsChunkedEncodingConfig) {
            this.awsChunkedEncodingConfig = awsChunkedEncodingConfig;
            return (T)this;
        }

        public T sdkChecksum(SdkChecksum sdkChecksum) {
            this.sdkChecksum = sdkChecksum;
            return (T)this;
        }

        public T checksumHeaderForTrailer(String checksumHeaderForTrailer) {
            this.checksumHeaderForTrailer = checksumHeaderForTrailer;
            return (T)this;
        }
    }
}

