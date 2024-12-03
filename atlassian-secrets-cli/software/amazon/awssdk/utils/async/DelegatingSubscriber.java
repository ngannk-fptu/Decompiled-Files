/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.async;

import java.util.concurrent.atomic.AtomicBoolean;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public abstract class DelegatingSubscriber<T, U>
implements Subscriber<T> {
    protected final Subscriber<? super U> subscriber;
    private final AtomicBoolean complete = new AtomicBoolean(false);

    protected DelegatingSubscriber(Subscriber<? super U> subscriber) {
        this.subscriber = subscriber;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscriber.onSubscribe(subscription);
    }

    @Override
    public void onError(Throwable throwable) {
        if (this.complete.compareAndSet(false, true)) {
            this.subscriber.onError(throwable);
        }
    }

    @Override
    public void onComplete() {
        if (this.complete.compareAndSet(false, true)) {
            this.subscriber.onComplete();
        }
    }
}

