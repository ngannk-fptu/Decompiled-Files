/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.async;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.checksums.Algorithm;
import software.amazon.awssdk.core.checksums.SdkChecksum;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.internal.async.ChunkBuffer;
import software.amazon.awssdk.core.internal.util.ChunkContentUtils;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.async.DelegatingSubscriber;
import software.amazon.awssdk.utils.builder.SdkBuilder;

@SdkInternalApi
public class ChecksumCalculatingAsyncRequestBody
implements AsyncRequestBody {
    private static final byte[] FINAL_BYTE = new byte[0];
    private final AsyncRequestBody wrapped;
    private final SdkChecksum sdkChecksum;
    private final Algorithm algorithm;
    private final String trailerHeader;
    private final long totalBytes;

    private ChecksumCalculatingAsyncRequestBody(DefaultBuilder builder) {
        Validate.notNull(builder.asyncRequestBody, "wrapped AsyncRequestBody cannot be null", new Object[0]);
        Validate.notNull(builder.algorithm, "algorithm cannot be null", new Object[0]);
        Validate.notNull(builder.trailerHeader, "trailerHeader cannot be null", new Object[0]);
        this.wrapped = builder.asyncRequestBody;
        this.algorithm = builder.algorithm;
        this.sdkChecksum = builder.algorithm != null ? SdkChecksum.forAlgorithm(this.algorithm) : null;
        this.trailerHeader = builder.trailerHeader;
        this.totalBytes = this.wrapped.contentLength().orElseThrow(() -> new UnsupportedOperationException("Content length must be supplied."));
    }

    public static Builder builder() {
        return new DefaultBuilder();
    }

    @Override
    public Optional<Long> contentLength() {
        if (this.wrapped.contentLength().isPresent() && this.algorithm != null) {
            return Optional.of(ChunkContentUtils.calculateChunkLength(this.wrapped.contentLength().get()) + ChunkContentUtils.LAST_CHUNK_LEN + ChunkContentUtils.calculateChecksumTrailerLength(this.algorithm, this.trailerHeader));
        }
        return this.wrapped.contentLength();
    }

    @Override
    public String contentType() {
        return this.wrapped.contentType();
    }

    @Override
    public void subscribe(Subscriber<? super ByteBuffer> s) {
        Validate.notNull(s, "Subscription MUST NOT be null.", new Object[0]);
        if (this.sdkChecksum != null) {
            this.sdkChecksum.reset();
        }
        SynchronousChunkBuffer synchronousChunkBuffer = new SynchronousChunkBuffer(this.totalBytes);
        this.alwaysInvokeOnNext(this.wrapped).flatMapIterable(x$0 -> synchronousChunkBuffer.buffer(x$0)).subscribe(new ChecksumCalculatingSubscriber(s, this.sdkChecksum, this.trailerHeader, this.totalBytes));
    }

    private SdkPublisher<ByteBuffer> alwaysInvokeOnNext(SdkPublisher<ByteBuffer> source) {
        return subscriber -> source.subscribe(new OnNextGuaranteedSubscriber(subscriber));
    }

    public static class OnNextGuaranteedSubscriber
    extends DelegatingSubscriber<ByteBuffer, ByteBuffer> {
        private volatile boolean onNextInvoked;

        public OnNextGuaranteedSubscriber(Subscriber<? super ByteBuffer> subscriber) {
            super(subscriber);
        }

        @Override
        public void onNext(ByteBuffer t) {
            if (!this.onNextInvoked) {
                this.onNextInvoked = true;
            }
            this.subscriber.onNext(t);
        }

        @Override
        public void onComplete() {
            if (!this.onNextInvoked) {
                this.subscriber.onNext(ByteBuffer.wrap(new byte[0]));
            }
            super.onComplete();
        }
    }

    private static final class SynchronousChunkBuffer {
        private final ChunkBuffer chunkBuffer;

        SynchronousChunkBuffer(long totalBytes) {
            this.chunkBuffer = (ChunkBuffer)ChunkBuffer.builder().bufferSize(16384).totalBytes(totalBytes).build();
        }

        private Iterable<ByteBuffer> buffer(ByteBuffer bytes) {
            return this.chunkBuffer.split(bytes);
        }
    }

    private static final class ChecksumCalculatingSubscriber
    implements Subscriber<ByteBuffer> {
        private final Subscriber<? super ByteBuffer> wrapped;
        private final SdkChecksum checksum;
        private final String trailerHeader;
        private byte[] checksumBytes;
        private final AtomicLong remainingBytes;
        private Subscription subscription;

        ChecksumCalculatingSubscriber(Subscriber<? super ByteBuffer> wrapped, SdkChecksum checksum, String trailerHeader, long totalBytes) {
            this.wrapped = wrapped;
            this.checksum = checksum;
            this.trailerHeader = trailerHeader;
            this.remainingBytes = new AtomicLong(totalBytes);
        }

        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.wrapped.onSubscribe(subscription);
        }

        @Override
        public void onNext(ByteBuffer byteBuffer) {
            boolean lastByte = this.remainingBytes.addAndGet(-byteBuffer.remaining()) <= 0L;
            try {
                if (this.checksum != null) {
                    byteBuffer.mark();
                    this.checksum.update(byteBuffer);
                    byteBuffer.reset();
                }
                if (lastByte && this.checksumBytes == null && this.checksum != null) {
                    this.checksumBytes = this.checksum.getChecksumBytes();
                    ByteBuffer allocatedBuffer = this.getFinalChecksumAppendedChunk(byteBuffer);
                    this.wrapped.onNext(allocatedBuffer);
                } else {
                    ByteBuffer allocatedBuffer = ChunkContentUtils.createChunk(byteBuffer, false);
                    this.wrapped.onNext(allocatedBuffer);
                }
            }
            catch (SdkException sdkException) {
                this.subscription.cancel();
                this.onError(sdkException);
            }
        }

        private ByteBuffer getFinalChecksumAppendedChunk(ByteBuffer byteBuffer) {
            ByteBuffer finalChunkedByteBuffer = ChunkContentUtils.createChunk(ByteBuffer.wrap(FINAL_BYTE), true);
            ByteBuffer checksumTrailerByteBuffer = ChunkContentUtils.createChecksumTrailer(BinaryUtils.toBase64(this.checksumBytes), this.trailerHeader);
            ByteBuffer contentChunk = byteBuffer.hasRemaining() ? ChunkContentUtils.createChunk(byteBuffer, false) : byteBuffer;
            ByteBuffer checksumAppendedBuffer = ByteBuffer.allocate(contentChunk.remaining() + finalChunkedByteBuffer.remaining() + checksumTrailerByteBuffer.remaining());
            checksumAppendedBuffer.put(contentChunk).put(finalChunkedByteBuffer).put(checksumTrailerByteBuffer);
            checksumAppendedBuffer.flip();
            return checksumAppendedBuffer;
        }

        @Override
        public void onError(Throwable t) {
            this.wrapped.onError(t);
        }

        @Override
        public void onComplete() {
            this.wrapped.onComplete();
        }
    }

    private static final class DefaultBuilder
    implements Builder {
        private AsyncRequestBody asyncRequestBody;
        private Algorithm algorithm;
        private String trailerHeader;

        private DefaultBuilder() {
        }

        @Override
        public ChecksumCalculatingAsyncRequestBody build() {
            return new ChecksumCalculatingAsyncRequestBody(this);
        }

        @Override
        public Builder asyncRequestBody(AsyncRequestBody asyncRequestBody) {
            this.asyncRequestBody = asyncRequestBody;
            return this;
        }

        @Override
        public Builder algorithm(Algorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        @Override
        public Builder trailerHeader(String trailerHeader) {
            this.trailerHeader = trailerHeader;
            return this;
        }
    }

    public static interface Builder
    extends SdkBuilder<Builder, ChecksumCalculatingAsyncRequestBody> {
        public Builder asyncRequestBody(AsyncRequestBody var1);

        public Builder algorithm(Algorithm var1);

        public Builder trailerHeader(String var1);
    }
}

