/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.Logger
 *  software.amazon.awssdk.utils.Validate
 *  software.amazon.awssdk.utils.async.SimplePublisher
 */
package software.amazon.awssdk.core.internal.async;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncRequestBodySplitConfiguration;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.exception.NonRetryableException;
import software.amazon.awssdk.core.internal.util.NoopSubscription;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.async.SimplePublisher;

@SdkInternalApi
public class SplittingPublisher
implements SdkPublisher<AsyncRequestBody> {
    private static final Logger log = Logger.loggerFor(SplittingPublisher.class);
    private final AsyncRequestBody upstreamPublisher;
    private final SplittingSubscriber splittingSubscriber;
    private final SimplePublisher<AsyncRequestBody> downstreamPublisher = new SimplePublisher();
    private final long chunkSizeInBytes;
    private final long bufferSizeInBytes;

    public SplittingPublisher(AsyncRequestBody asyncRequestBody, AsyncRequestBodySplitConfiguration splitConfiguration) {
        this.upstreamPublisher = (AsyncRequestBody)Validate.paramNotNull((Object)asyncRequestBody, (String)"asyncRequestBody");
        Validate.notNull((Object)splitConfiguration, (String)"splitConfiguration", (Object[])new Object[0]);
        this.chunkSizeInBytes = splitConfiguration.chunkSizeInBytes() == null ? AsyncRequestBodySplitConfiguration.defaultConfiguration().chunkSizeInBytes() : splitConfiguration.chunkSizeInBytes();
        this.bufferSizeInBytes = splitConfiguration.bufferSizeInBytes() == null ? AsyncRequestBodySplitConfiguration.defaultConfiguration().bufferSizeInBytes() : splitConfiguration.bufferSizeInBytes();
        this.splittingSubscriber = new SplittingSubscriber(this.upstreamPublisher.contentLength().orElse(null));
        if (!this.upstreamPublisher.contentLength().isPresent()) {
            Validate.isTrue((this.bufferSizeInBytes >= this.chunkSizeInBytes ? 1 : 0) != 0, (String)"bufferSizeInBytes must be larger than or equal to chunkSizeInBytes if the content length is unknown", (Object[])new Object[0]);
        }
    }

    public void subscribe(Subscriber<? super AsyncRequestBody> downstreamSubscriber) {
        this.downstreamPublisher.subscribe(downstreamSubscriber);
        this.upstreamPublisher.subscribe(this.splittingSubscriber);
    }

    private class SplittingSubscriber
    implements Subscriber<ByteBuffer> {
        private Subscription upstreamSubscription;
        private final Long upstreamSize;
        private final AtomicInteger chunkNumber = new AtomicInteger(0);
        private volatile DownstreamBody currentBody;
        private final AtomicBoolean hasOpenUpstreamDemand = new AtomicBoolean(false);
        private final AtomicLong dataBuffered = new AtomicLong(0L);
        private int byteBufferSizeHint;
        private volatile boolean upstreamComplete;

        SplittingSubscriber(Long upstreamSize) {
            this.upstreamSize = upstreamSize;
        }

        public void onSubscribe(Subscription s) {
            this.upstreamSubscription = s;
            this.currentBody = this.initializeNextDownstreamBody(this.upstreamSize != null, this.calculateChunkSize(this.upstreamSize), this.chunkNumber.get());
            this.upstreamSubscription.request(1L);
        }

        private DownstreamBody initializeNextDownstreamBody(boolean contentLengthKnown, long chunkSize, int chunkNumber) {
            DownstreamBody body = new DownstreamBody(contentLengthKnown, chunkSize, chunkNumber);
            if (contentLengthKnown) {
                this.sendCurrentBody(body);
            }
            return body;
        }

        public void onNext(ByteBuffer byteBuffer) {
            this.hasOpenUpstreamDemand.set(false);
            this.byteBufferSizeHint = byteBuffer.remaining();
            while (byteBuffer.hasRemaining()) {
                int amountRemainingInChunk = this.amountRemainingInChunk();
                if (amountRemainingInChunk == 0) {
                    this.completeCurrentBodyAndCreateNewIfNeeded(byteBuffer);
                    amountRemainingInChunk = this.amountRemainingInChunk();
                }
                if (amountRemainingInChunk > byteBuffer.remaining()) {
                    this.currentBody.send(byteBuffer.duplicate());
                    break;
                }
                if (amountRemainingInChunk == byteBuffer.remaining()) {
                    this.currentBody.send(byteBuffer.duplicate());
                    this.completeCurrentBodyAndCreateNewIfNeeded(byteBuffer);
                    break;
                }
                ByteBuffer firstHalf = byteBuffer.duplicate();
                int newLimit = firstHalf.position() + amountRemainingInChunk;
                firstHalf.limit(newLimit);
                byteBuffer.position(newLimit);
                this.currentBody.send(firstHalf);
            }
            this.maybeRequestMoreUpstreamData();
        }

        private void completeCurrentBodyAndCreateNewIfNeeded(ByteBuffer byteBuffer) {
            boolean shouldCreateNewDownstreamRequestBody;
            this.completeCurrentBody();
            int currentChunk = this.chunkNumber.incrementAndGet();
            Long dataRemaining = this.totalDataRemaining();
            if (this.upstreamSize == null) {
                shouldCreateNewDownstreamRequestBody = !this.upstreamComplete || byteBuffer.hasRemaining();
            } else {
                boolean bl = shouldCreateNewDownstreamRequestBody = dataRemaining != null && dataRemaining > 0L;
            }
            if (shouldCreateNewDownstreamRequestBody) {
                long chunkSize = this.calculateChunkSize(dataRemaining);
                this.currentBody = this.initializeNextDownstreamBody(this.upstreamSize != null, chunkSize, currentChunk);
            }
        }

        private int amountRemainingInChunk() {
            return Math.toIntExact(this.currentBody.maxLength - this.currentBody.transferredLength);
        }

        private void completeCurrentBody() {
            log.debug(() -> "completeCurrentBody for chunk " + this.chunkNumber.get());
            this.currentBody.complete();
            if (this.upstreamSize == null) {
                this.sendCurrentBody(this.currentBody);
            }
        }

        public void onComplete() {
            this.upstreamComplete = true;
            log.trace(() -> "Received onComplete()");
            this.completeCurrentBody();
            SplittingPublisher.this.downstreamPublisher.complete();
        }

        public void onError(Throwable t) {
            log.trace(() -> "Received onError()", t);
            SplittingPublisher.this.downstreamPublisher.error(t);
        }

        private void sendCurrentBody(AsyncRequestBody body) {
            SplittingPublisher.this.downstreamPublisher.send((Object)body).exceptionally(t -> {
                SplittingPublisher.this.downstreamPublisher.error(t);
                return null;
            });
        }

        private long calculateChunkSize(Long dataRemaining) {
            if (dataRemaining == null) {
                return SplittingPublisher.this.chunkSizeInBytes;
            }
            return Math.min(SplittingPublisher.this.chunkSizeInBytes, dataRemaining);
        }

        private void maybeRequestMoreUpstreamData() {
            long buffered = this.dataBuffered.get();
            if (this.shouldRequestMoreData(buffered) && this.hasOpenUpstreamDemand.compareAndSet(false, true)) {
                log.trace(() -> "Requesting more data, current data buffered: " + buffered);
                this.upstreamSubscription.request(1L);
            }
        }

        private boolean shouldRequestMoreData(long buffered) {
            return buffered == 0L || buffered + (long)this.byteBufferSizeHint <= SplittingPublisher.this.bufferSizeInBytes;
        }

        private Long totalDataRemaining() {
            if (this.upstreamSize == null) {
                return null;
            }
            return this.upstreamSize - (long)this.chunkNumber.get() * SplittingPublisher.this.chunkSizeInBytes;
        }

        private final class DownstreamBody
        implements AsyncRequestBody {
            private final long maxLength;
            private final Long totalLength;
            private final SimplePublisher<ByteBuffer> delegate = new SimplePublisher();
            private final int chunkNumber;
            private final AtomicBoolean subscribeCalled = new AtomicBoolean(false);
            private volatile long transferredLength = 0L;

            private DownstreamBody(boolean contentLengthKnown, long maxLength, int chunkNumber) {
                this.totalLength = contentLengthKnown ? Long.valueOf(maxLength) : null;
                this.maxLength = maxLength;
                this.chunkNumber = chunkNumber;
            }

            @Override
            public Optional<Long> contentLength() {
                return this.totalLength != null ? Optional.of(this.totalLength) : Optional.of(this.transferredLength);
            }

            public void send(ByteBuffer data) {
                log.trace(() -> String.format("Sending bytebuffer %s to chunk %d", data, this.chunkNumber));
                int length = data.remaining();
                this.transferredLength += (long)length;
                this.addDataBuffered(length);
                this.delegate.send((Object)data).whenComplete((r, t) -> {
                    this.addDataBuffered(-length);
                    if (t != null) {
                        this.error((Throwable)t);
                    }
                });
            }

            public void complete() {
                log.debug(() -> "Received complete() for chunk number: " + this.chunkNumber + " length " + this.transferredLength);
                this.delegate.complete().whenComplete((r, t) -> {
                    if (t != null) {
                        this.error((Throwable)t);
                    }
                });
            }

            public void error(Throwable error) {
                this.delegate.error(error);
            }

            public void subscribe(Subscriber<? super ByteBuffer> s) {
                if (this.subscribeCalled.compareAndSet(false, true)) {
                    this.delegate.subscribe(s);
                } else {
                    s.onSubscribe((Subscription)new NoopSubscription(s));
                    s.onError((Throwable)NonRetryableException.create("A retry was attempted, but AsyncRequestBody.split does not support retries."));
                }
            }

            private void addDataBuffered(int length) {
                SplittingSubscriber.this.dataBuffered.addAndGet(length);
                if (length < 0) {
                    SplittingSubscriber.this.maybeRequestMoreUpstreamData();
                }
            }
        }
    }
}

