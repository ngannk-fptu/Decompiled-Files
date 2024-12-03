/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        this.delegateSubscriber.onSubscribe(subscription);
    }

    @Override
    public void onError(Throwable throwable) {
        if (!this.isCancelled) {
            this.delegateSubscriber.onError(throwable);
        }
    }

    @Override
    public void onComplete() {
        if (!this.isCancelled) {
            this.delegateSubscriber.onComplete();
        }
    }

    @Override
    public void onNext(T t) {
        if (!this.isCancelled) {
            try {
                this.delegateSubscriber.onNext(this.mapFunction.apply(t));
            }
            catch (RuntimeException e) {
                this.cancelSubscriptions();
                this.delegateSubscriber.onError(e);
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

