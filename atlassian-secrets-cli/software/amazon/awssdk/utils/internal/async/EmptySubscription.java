/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.internal.async;

import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class EmptySubscription
implements Subscription {
    private final AtomicBoolean isTerminated = new AtomicBoolean(false);
    private final Subscriber<?> subscriber;

    public EmptySubscription(Subscriber<?> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
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

    @Override
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

