/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
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

    public void onSubscribe(Subscription subscription) {
        this.subscriber.onSubscribe(subscription);
    }

    public void onError(Throwable throwable) {
        if (this.complete.compareAndSet(false, true)) {
            this.subscriber.onError(throwable);
        }
    }

    public void onComplete() {
        if (this.complete.compareAndSet(false, true)) {
            this.subscriber.onComplete();
        }
    }
}

