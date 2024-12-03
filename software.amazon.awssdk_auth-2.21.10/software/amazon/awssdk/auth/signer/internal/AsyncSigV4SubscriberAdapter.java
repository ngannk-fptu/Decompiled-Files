/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.auth.signer.internal;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
final class AsyncSigV4SubscriberAdapter
implements Subscriber<ByteBuffer> {
    private final AtomicBoolean upstreamDone = new AtomicBoolean(false);
    private final AtomicLong downstreamDemand = new AtomicLong();
    private final Object lock = new Object();
    private volatile boolean sentTrailingFrame = false;
    private Subscriber<? super ByteBuffer> delegate;

    AsyncSigV4SubscriberAdapter(Subscriber<? super ByteBuffer> actual) {
        this.delegate = actual;
    }

    public void onSubscribe(final Subscription s) {
        this.delegate.onSubscribe(new Subscription(){

            public void request(long n) {
                if (n <= 0L) {
                    throw new IllegalArgumentException("n > 0 required but it was " + n);
                }
                AsyncSigV4SubscriberAdapter.this.downstreamDemand.getAndAdd(n);
                if (AsyncSigV4SubscriberAdapter.this.upstreamDone.get()) {
                    AsyncSigV4SubscriberAdapter.this.sendTrailingEmptyFrame();
                } else {
                    s.request(n);
                }
            }

            public void cancel() {
                s.cancel();
            }
        });
    }

    public void onNext(ByteBuffer byteBuffer) {
        this.downstreamDemand.decrementAndGet();
        this.delegate.onNext((Object)byteBuffer);
    }

    public void onError(Throwable t) {
        this.upstreamDone.compareAndSet(false, true);
        this.delegate.onError(t);
    }

    public void onComplete() {
        this.upstreamDone.compareAndSet(false, true);
        if (this.downstreamDemand.get() > 0L) {
            this.sendTrailingEmptyFrame();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void sendTrailingEmptyFrame() {
        Object object = this.lock;
        synchronized (object) {
            if (!this.sentTrailingFrame) {
                this.sentTrailingFrame = true;
                this.delegate.onNext((Object)ByteBuffer.wrap(new byte[0]));
                this.delegate.onComplete();
            }
        }
    }
}

