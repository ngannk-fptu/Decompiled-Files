/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.utils.CancellableOutputStream
 *  software.amazon.awssdk.utils.async.OutputStreamPublisher
 */
package software.amazon.awssdk.core.async;

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
import software.amazon.awssdk.core.internal.util.NoopSubscription;
import software.amazon.awssdk.utils.CancellableOutputStream;
import software.amazon.awssdk.utils.async.OutputStreamPublisher;

@SdkPublicApi
public final class BlockingOutputStreamAsyncRequestBody
implements AsyncRequestBody {
    private final OutputStreamPublisher delegate = new OutputStreamPublisher();
    private final CountDownLatch subscribedLatch = new CountDownLatch(1);
    private final AtomicBoolean subscribeCalled = new AtomicBoolean(false);
    private final Long contentLength;
    private final Duration subscribeTimeout;

    BlockingOutputStreamAsyncRequestBody(Long contentLength) {
        this(contentLength, Duration.ofSeconds(10L));
    }

    BlockingOutputStreamAsyncRequestBody(Long contentLength, Duration subscribeTimeout) {
        this.contentLength = contentLength;
        this.subscribeTimeout = subscribeTimeout;
    }

    public CancellableOutputStream outputStream() {
        this.waitForSubscriptionIfNeeded();
        return this.delegate;
    }

    @Override
    public Optional<Long> contentLength() {
        return Optional.ofNullable(this.contentLength);
    }

    public void subscribe(Subscriber<? super ByteBuffer> s) {
        if (this.subscribeCalled.compareAndSet(false, true)) {
            this.delegate.subscribe(s);
            this.subscribedLatch.countDown();
        } else {
            s.onSubscribe((Subscription)new NoopSubscription(s));
            s.onError((Throwable)NonRetryableException.create("A retry was attempted, but AsyncRequestBody.forBlockingOutputStream does not support retries."));
        }
    }

    private void waitForSubscriptionIfNeeded() {
        try {
            long timeoutSeconds = this.subscribeTimeout.getSeconds();
            if (!this.subscribedLatch.await(timeoutSeconds, TimeUnit.SECONDS)) {
                throw new IllegalStateException("The service request was not made within " + timeoutSeconds + " seconds of outputStream being invoked. Make sure to invoke the service request BEFORE invoking outputStream if your caller is single-threaded.");
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while waiting for subscription.", e);
        }
    }
}

