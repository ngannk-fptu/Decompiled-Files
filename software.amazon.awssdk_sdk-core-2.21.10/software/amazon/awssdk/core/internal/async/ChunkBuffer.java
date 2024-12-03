/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.builder.SdkBuilder
 */
package software.amazon.awssdk.core.internal.async;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkInternalApi
public final class ChunkBuffer {
    private static final Logger log = Logger.loggerFor(ChunkBuffer.class);
    private final AtomicLong transferredBytes;
    private final ByteBuffer currentBuffer;
    private final int chunkSize;
    private final Long totalBytes;

    private ChunkBuffer(Long totalBytes, Integer bufferSize) {
        int chunkSize;
        this.chunkSize = chunkSize = bufferSize != null ? bufferSize : 16384;
        this.currentBuffer = ByteBuffer.allocate(chunkSize);
        this.totalBytes = totalBytes;
        this.transferredBytes = new AtomicLong(0L);
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    public synchronized Iterable<ByteBuffer> split(ByteBuffer inputByteBuffer) {
        if (!inputByteBuffer.hasRemaining()) {
            return Collections.singletonList(inputByteBuffer);
        }
        ArrayList<ByteBuffer> byteBuffers = new ArrayList<ByteBuffer>();
        if (this.currentBuffer.position() != 0) {
            this.fillCurrentBuffer(inputByteBuffer);
            if (this.isCurrentBufferFull()) {
                this.addCurrentBufferToIterable(byteBuffers);
            }
        }
        if (inputByteBuffer.hasRemaining()) {
            this.splitRemainingInputByteBuffer(inputByteBuffer, byteBuffers);
        }
        if (this.isLastChunk()) {
            this.addCurrentBufferToIterable(byteBuffers);
        }
        return byteBuffers;
    }

    private boolean isCurrentBufferFull() {
        return this.currentBuffer.position() == this.chunkSize;
    }

    private void splitRemainingInputByteBuffer(ByteBuffer inputByteBuffer, List<ByteBuffer> byteBuffers) {
        while (inputByteBuffer.hasRemaining()) {
            ByteBuffer inputByteBufferCopy = inputByteBuffer.asReadOnlyBuffer();
            if (inputByteBuffer.remaining() < this.chunkSize) {
                this.currentBuffer.put(inputByteBuffer);
                break;
            }
            int newLimit = inputByteBufferCopy.position() + this.chunkSize;
            inputByteBufferCopy.limit(newLimit);
            inputByteBuffer.position(newLimit);
            byteBuffers.add(inputByteBufferCopy);
            this.transferredBytes.addAndGet(this.chunkSize);
        }
    }

    public Optional<ByteBuffer> getBufferedData() {
        int remainingBytesInBuffer = this.currentBuffer.position();
        if (remainingBytesInBuffer == 0) {
            return Optional.empty();
        }
        ByteBuffer bufferedChunk = ByteBuffer.allocate(remainingBytesInBuffer);
        this.currentBuffer.flip();
        bufferedChunk.put(this.currentBuffer);
        bufferedChunk.flip();
        return Optional.of(bufferedChunk);
    }

    private boolean isLastChunk() {
        if (this.totalBytes == null) {
            return false;
        }
        long remainingBytes = this.totalBytes - this.transferredBytes.get();
        return remainingBytes != 0L && remainingBytes == (long)this.currentBuffer.position();
    }

    private void addCurrentBufferToIterable(List<ByteBuffer> byteBuffers) {
        Optional<ByteBuffer> bufferedChunk = this.getBufferedData();
        if (bufferedChunk.isPresent()) {
            byteBuffers.add(bufferedChunk.get());
            this.transferredBytes.addAndGet(bufferedChunk.get().remaining());
            this.currentBuffer.clear();
        }
    }

    private void fillCurrentBuffer(ByteBuffer inputByteBuffer) {
        while (this.currentBuffer.position() < this.chunkSize && inputByteBuffer.hasRemaining()) {
            int remainingCapacity = this.chunkSize - this.currentBuffer.position();
            if (inputByteBuffer.remaining() < remainingCapacity) {
                this.currentBuffer.put(inputByteBuffer);
                continue;
            }
            ByteBuffer remainingChunk = inputByteBuffer.asReadOnlyBuffer();
            int newLimit = inputByteBuffer.position() + remainingCapacity;
            remainingChunk.limit(newLimit);
            inputByteBuffer.position(newLimit);
            this.currentBuffer.put(remainingChunk);
        }
    }

    private static final class DefaultBuilder
    implements Builder {
        private Integer bufferSize;
        private Long totalBytes;

        private DefaultBuilder() {
        }

        public ChunkBuffer build() {
            return new ChunkBuffer(this.totalBytes, this.bufferSize);
        }

        @Override
        public Builder bufferSize(int bufferSize) {
            this.bufferSize = bufferSize;
            return this;
        }

        @Override
        public Builder totalBytes(long totalBytes) {
            this.totalBytes = totalBytes;
            return this;
        }
    }

    public static interface Builder
    extends SdkBuilder<Builder, ChunkBuffer> {
        public Builder bufferSize(int var1);

        public Builder totalBytes(long var1);
    }
}

