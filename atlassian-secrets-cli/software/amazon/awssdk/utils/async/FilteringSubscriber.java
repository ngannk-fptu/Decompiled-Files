/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.async;

import java.util.function.Predicate;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.async.DelegatingSubscriber;

@SdkProtectedApi
public class FilteringSubscriber<T>
extends DelegatingSubscriber<T, T> {
    private final Predicate<T> predicate;
    private Subscription subscription;

    public FilteringSubscriber(Subscriber<? super T> sourceSubscriber, Predicate<T> predicate) {
        super(sourceSubscriber);
        this.predicate = predicate;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        super.onSubscribe(subscription);
    }

    @Override
    public void onNext(T t) {
        try {
            if (this.predicate.test(t)) {
                this.subscriber.onNext(t);
            } else {
                this.subscription.request(1L);
            }
        }
        catch (RuntimeException e) {
            this.subscription.cancel();
            this.onError(e);
        }
    }
}

