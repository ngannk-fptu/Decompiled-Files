/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.compression.Compressor;
import software.amazon.awssdk.core.internal.io.AwsChunkedInputStream;
import software.amazon.awssdk.core.internal.io.ChunkContentIterator;
import software.amazon.awssdk.core.internal.io.UnderlyingStreamBuffer;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class AwsCompressionInputStream
extends AwsChunkedInputStream {
    private final Compressor compressor;

    private AwsCompressionInputStream(InputStream in, Compressor compressor) {
        this.compressor = compressor;
        if (in instanceof AwsCompressionInputStream) {
            AwsCompressionInputStream originalCompressionStream = (AwsCompressionInputStream)in;
            this.is = originalCompressionStream.is;
            this.underlyingStreamBuffer = originalCompressionStream.underlyingStreamBuffer;
        } else {
            this.is = in;
            this.underlyingStreamBuffer = null;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int count;
        this.abortIfNeeded();
        Validate.notNull((Object)b, (String)"buff", (Object[])new Object[0]);
        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (this.currentChunkIterator == null || !this.currentChunkIterator.hasNext()) {
            if (this.isTerminating) {
                return -1;
            }
            this.isTerminating = this.setUpNextChunk();
        }
        if ((count = this.currentChunkIterator.read(b, off, len)) > 0) {
            this.isAtStart = false;
            log.trace(() -> count + " byte read from the stream.");
        }
        return count;
    }

    private boolean setUpNextChunk() throws IOException {
        byte[] chunkData = new byte[131072];
        int chunkSizeInBytes = 0;
        while (chunkSizeInBytes < 131072) {
            if (this.underlyingStreamBuffer != null && this.underlyingStreamBuffer.hasNext()) {
                chunkData[chunkSizeInBytes++] = this.underlyingStreamBuffer.next();
                continue;
            }
            int bytesToRead = 131072 - chunkSizeInBytes;
            int count = this.is.read(chunkData, chunkSizeInBytes, bytesToRead);
            if (count == -1) break;
            if (this.underlyingStreamBuffer != null) {
                this.underlyingStreamBuffer.buffer(chunkData, chunkSizeInBytes, count);
            }
            chunkSizeInBytes += count;
        }
        if (chunkSizeInBytes == 0) {
            return true;
        }
        if (chunkSizeInBytes < chunkData.length) {
            chunkData = Arrays.copyOf(chunkData, chunkSizeInBytes);
        }
        byte[] compressedChunkData = this.compressor.compress(chunkData);
        this.currentChunkIterator = new ChunkContentIterator(compressedChunkData);
        return false;
    }

    @Override
    public void mark(int readlimit) {
        this.abortIfNeeded();
        if (!this.isAtStart) {
            throw new UnsupportedOperationException("Compression stream only supports mark() at the start of the stream.");
        }
        if (this.is.markSupported()) {
            log.debug(() -> "AwsCompressionInputStream marked at the start of the stream (will directly mark the wrapped stream since it's mark-supported).");
            this.is.mark(readlimit);
        } else {
            log.debug(() -> "AwsCompressionInputStream marked at the start of the stream (initializing the buffer since the wrapped stream is not mark-supported).");
            this.underlyingStreamBuffer = new UnderlyingStreamBuffer(262144);
        }
    }

    @Override
    public void reset() throws IOException {
        this.abortIfNeeded();
        this.currentChunkIterator = null;
        if (this.is.markSupported()) {
            log.debug(() -> "AwsCompressionInputStream reset (will reset the wrapped stream because it is mark-supported).");
            this.is.reset();
        } else {
            log.debug(() -> "AwsCompressionInputStream reset (will use the buffer of the decoded stream).");
            Validate.notNull((Object)this.underlyingStreamBuffer, (String)"Cannot reset the stream because the mark is not set.", (Object[])new Object[0]);
            this.underlyingStreamBuffer.startReadBuffer();
        }
        this.isAtStart = true;
        this.isTerminating = false;
    }

    public static final class Builder {
        InputStream inputStream;
        Compressor compressor;

        public AwsCompressionInputStream build() {
            return new AwsCompressionInputStream(this.inputStream, this.compressor);
        }

        public Builder inputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        public Builder compressor(Compressor compressor) {
            this.compressor = compressor;
            return this;
        }
    }
}

