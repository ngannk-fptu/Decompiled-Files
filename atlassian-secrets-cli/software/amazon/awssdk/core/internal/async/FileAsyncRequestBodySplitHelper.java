/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.async;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncRequestBodySplitConfiguration;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.async.FileAsyncRequestBody;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.async.SimplePublisher;

@SdkInternalApi
public final class FileAsyncRequestBodySplitHelper {
    private static final Logger log = Logger.loggerFor(FileAsyncRequestBodySplitHelper.class);
    private final AtomicBoolean isSendingRequestBody = new AtomicBoolean(false);
    private final AtomicLong remainingBytes;
    private final long totalContentLength;
    private final Path path;
    private final int bufferPerAsyncRequestBody;
    private final long totalBufferSize;
    private final long chunkSize;
    private volatile boolean isDone = false;
    private AtomicInteger numAsyncRequestBodiesInFlight = new AtomicInteger(0);
    private AtomicInteger chunkIndex = new AtomicInteger(0);

    public FileAsyncRequestBodySplitHelper(FileAsyncRequestBody asyncRequestBody, AsyncRequestBodySplitConfiguration splitConfiguration) {
        Validate.notNull(asyncRequestBody, "asyncRequestBody", new Object[0]);
        Validate.notNull(splitConfiguration, "splitConfiguration", new Object[0]);
        Validate.isTrue(asyncRequestBody.contentLength().isPresent(), "Content length must be present", asyncRequestBody);
        this.totalContentLength = asyncRequestBody.contentLength().get();
        this.remainingBytes = new AtomicLong(this.totalContentLength);
        this.path = asyncRequestBody.path();
        this.chunkSize = splitConfiguration.chunkSizeInBytes() == null ? AsyncRequestBodySplitConfiguration.defaultConfiguration().chunkSizeInBytes() : splitConfiguration.chunkSizeInBytes();
        this.totalBufferSize = splitConfiguration.bufferSizeInBytes() == null ? AsyncRequestBodySplitConfiguration.defaultConfiguration().bufferSizeInBytes() : splitConfiguration.bufferSizeInBytes();
        this.bufferPerAsyncRequestBody = asyncRequestBody.chunkSizeInBytes();
    }

    public SdkPublisher<AsyncRequestBody> split() {
        SimplePublisher<AsyncRequestBody> simplePublisher = new SimplePublisher<AsyncRequestBody>();
        try {
            this.sendAsyncRequestBody(simplePublisher);
        }
        catch (Throwable throwable) {
            simplePublisher.error(throwable);
        }
        return SdkPublisher.adapt(simplePublisher);
    }

    private void sendAsyncRequestBody(SimplePublisher<AsyncRequestBody> simplePublisher) {
        do {
            if (!this.isSendingRequestBody.compareAndSet(false, true)) {
                return;
            }
            try {
                this.doSendAsyncRequestBody(simplePublisher);
            }
            finally {
                this.isSendingRequestBody.set(false);
            }
        } while (this.shouldSendMore());
    }

    private void doSendAsyncRequestBody(SimplePublisher<AsyncRequestBody> simplePublisher) {
        while (this.shouldSendMore()) {
            AsyncRequestBody currentAsyncRequestBody = this.newFileAsyncRequestBody(simplePublisher);
            simplePublisher.send(currentAsyncRequestBody);
            this.numAsyncRequestBodiesInFlight.incrementAndGet();
            this.checkCompletion(simplePublisher, currentAsyncRequestBody);
        }
    }

    private void checkCompletion(SimplePublisher<AsyncRequestBody> simplePublisher, AsyncRequestBody currentAsyncRequestBody) {
        long remaining = this.remainingBytes.addAndGet(-currentAsyncRequestBody.contentLength().get().longValue());
        if (remaining == 0L) {
            this.isDone = true;
            simplePublisher.complete();
        } else if (remaining < 0L) {
            this.isDone = true;
            simplePublisher.error(SdkClientException.create("Unexpected error occurred. Remaining data is negative: " + remaining));
        }
    }

    private void startNextRequestBody(SimplePublisher<AsyncRequestBody> simplePublisher) {
        this.numAsyncRequestBodiesInFlight.decrementAndGet();
        this.sendAsyncRequestBody(simplePublisher);
    }

    private AsyncRequestBody newFileAsyncRequestBody(SimplePublisher<AsyncRequestBody> simplePublisher) {
        long position = this.chunkSize * (long)this.chunkIndex.getAndIncrement();
        long numBytesToReadForThisChunk = Math.min(this.totalContentLength - position, this.chunkSize);
        FileAsyncRequestBody fileAsyncRequestBody = (FileAsyncRequestBody)FileAsyncRequestBody.builder().path(this.path).position(position).numBytesToRead(numBytesToReadForThisChunk).build();
        return new FileAsyncRequestBodyWrapper(fileAsyncRequestBody, simplePublisher);
    }

    private boolean shouldSendMore() {
        if (this.isDone) {
            return false;
        }
        long currentUsedBuffer = (long)this.numAsyncRequestBodiesInFlight.get() * (long)this.bufferPerAsyncRequestBody;
        return currentUsedBuffer + (long)this.bufferPerAsyncRequestBody <= this.totalBufferSize;
    }

    @SdkTestInternalApi
    AtomicInteger numAsyncRequestBodiesInFlight() {
        return this.numAsyncRequestBodiesInFlight;
    }

    private final class FileAsyncRequestBodyWrapper
    implements AsyncRequestBody {
        private final FileAsyncRequestBody fileAsyncRequestBody;
        private final SimplePublisher<AsyncRequestBody> simplePublisher;

        FileAsyncRequestBodyWrapper(FileAsyncRequestBody fileAsyncRequestBody, SimplePublisher<AsyncRequestBody> simplePublisher) {
            this.fileAsyncRequestBody = fileAsyncRequestBody;
            this.simplePublisher = simplePublisher;
        }

        @Override
        public void subscribe(Subscriber<? super ByteBuffer> s) {
            this.fileAsyncRequestBody.doAfterOnComplete(() -> FileAsyncRequestBodySplitHelper.this.startNextRequestBody(this.simplePublisher)).doAfterOnCancel(() -> FileAsyncRequestBodySplitHelper.this.startNextRequestBody(this.simplePublisher)).subscribe(s);
        }

        @Override
        public Optional<Long> contentLength() {
            return this.fileAsyncRequestBody.contentLength();
        }
    }
}

