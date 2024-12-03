/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.async;

import java.util.concurrent.atomic.AtomicInteger;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.async.DelegatingSubscriber;
import software.amazon.awssdk.utils.internal.async.EmptySubscription;

@SdkProtectedApi
public class LimitingSubscriber<T>
extends DelegatingSubscriber<T, T> {
    private final int limit;
    private final AtomicInteger delivered = new AtomicInteger(0);
    private Subscription subscription;

    public LimitingSubscriber(Subscriber<? super T> subscriber, int limit) {
        super(subscriber);
        this.limit = limit;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        if (this.limit == 0) {
            subscription.cancel();
            super.onSubscribe(new EmptySubscription(this.subscriber));
        } else {
            super.onSubscribe(subscription);
        }
    }

    @Override
    public void onNext(T t) {
        int deliveredItems = this.delivered.incrementAndGet();
        if (deliveredItems <= this.limit) {
            this.subscriber.onNext(t);
            if (deliveredItems == this.limit) {
                this.subscription.cancel();
                this.subscriber.onComplete();
            }
        }
    }
}

