/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core.pagination.async;

import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class EmptySubscription
implements Subscription {
    private final AtomicBoolean isTerminated = new AtomicBoolean(false);
    private final Subscriber subscriber;

    public EmptySubscription(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void request(long n) {
        if (this.isTerminated()) {
            return;
        }
        if (n <= 0L) {
            throw new IllegalArgumentException("Non-positive request signals are illegal");
        }
        if (this.terminate()) {
            this.subscriber.onComplete();
        }
    }

    public void cancel() {
        this.terminate();
    }

    private boolean terminate() {
        return this.isTerminated.compareAndSet(false, true);
    }

    private boolean isTerminated() {
        return this.isTerminated.get();
    }
}

