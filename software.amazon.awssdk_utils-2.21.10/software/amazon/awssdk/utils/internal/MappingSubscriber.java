/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.utils.internal;

import java.util.function.Function;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class MappingSubscriber<T, U>
implements Subscriber<T> {
    private final Subscriber<? super U> delegateSubscriber;
    private final Function<T, U> mapFunction;
    private boolean isCancelled = false;
    private Subscription subscription = null;

    private MappingSubscriber(Subscriber<? super U> delegateSubscriber, Function<T, U> mapFunction) {
        this.delegateSubscriber = delegateSubscriber;
        this.mapFunction = mapFunction;
    }

    public static <T, U> MappingSubscriber<T, U> create(Subscriber<? super U> subscriber, Function<T, U> mapFunction) {
        return new MappingSubscriber<T, U>(subscriber, mapFunction);
    }

    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        this.delegateSubscriber.onSubscribe(subscription);
    }

    public void onError(Throwable throwable) {
        if (!this.isCancelled) {
            this.delegateSubscriber.onError(throwable);
        }
    }

    public void onComplete() {
        if (!this.isCancelled) {
            this.delegateSubscriber.onComplete();
        }
    }

    public void onNext(T t) {
        if (!this.isCancelled) {
            try {
                this.delegateSubscriber.onNext(this.mapFunction.apply(t));
            }
            catch (RuntimeException e) {
                this.cancelSubscriptions();
                this.delegateSubscriber.onError((Throwable)e);
            }
        }
    }

    private void cancelSubscriptions() {
        this.isCancelled = true;
        if (this.subscription != null) {
            try {
                this.subscription.cancel();
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
    }
}

