/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.async.InputStreamConsumingPublisher
 */
package software.amazon.awssdk.core.async;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.exception.NonRetryableException;
import software.amazon.awssdk.core.internal.io.SdkLengthAwareInputStream;
import software.amazon.awssdk.core.internal.util.NoopSubscription;
import software.amazon.awssdk.utils.async.InputStreamConsumingPublisher;

@SdkPublicApi
public final class BlockingInputStreamAsyncRequestBody
implements AsyncRequestBody {
    private final InputStreamConsumingPublisher delegate = new InputStreamConsumingPublisher();
    private final CountDownLatch subscribedLatch = new CountDownLatch(1);
    private final AtomicBoolean subscribeCalled = new AtomicBoolean(false);
    private final Long contentLength;
    private final Duration subscribeTimeout;

    BlockingInputStreamAsyncRequestBody(Long contentLength) {
        this(contentLength, Duration.ofSeconds(10L));
    }

    BlockingInputStreamAsyncRequestBody(Long contentLength, Duration subscribeTimeout) {
        this.contentLength = contentLength;
        this.subscribeTimeout = subscribeTimeout;
    }

    @Override
    public Optional<Long> contentLength() {
        return Optional.ofNullable(this.contentLength);
    }

    public long writeInputStream(InputStream inputStream) {
        try {
            this.waitForSubscriptionIfNeeded();
            if (this.contentLength != null) {
                return this.delegate.doBlockingWrite((InputStream)new SdkLengthAwareInputStream(inputStream, this.contentLength));
            }
            return this.delegate.doBlockingWrite(inputStream);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.delegate.cancel();
            throw new RuntimeException(e);
        }
    }

    public void cancel() {
        this.delegate.cancel();
    }

    public void subscribe(Subscriber<? super ByteBuffer> s) {
        if (this.subscribeCalled.compareAndSet(false, true)) {
            this.delegate.subscribe(s);
            this.subscribedLatch.countDown();
        } else {
            s.onSubscribe((Subscription)new NoopSubscription(s));
            s.onError((Throwable)NonRetryableException.create("A retry was attempted, but AsyncRequestBody.forBlockingInputStream does not support retries. Consider using AsyncRequestBody.fromInputStream with an input stream that supports mark/reset to get retry support."));
        }
    }

    private void waitForSubscriptionIfNeeded() throws InterruptedException {
        long timeoutSeconds = this.subscribeTimeout.getSeconds();
        if (!this.subscribedLatch.await(timeoutSeconds, TimeUnit.SECONDS)) {
            throw new IllegalStateException("The service request was not made within " + timeoutSeconds + " seconds of doBlockingWrite being invoked. Make sure to invoke the service request BEFORE invoking doBlockingWrite if your caller is single-threaded.");
        }
    }
}

