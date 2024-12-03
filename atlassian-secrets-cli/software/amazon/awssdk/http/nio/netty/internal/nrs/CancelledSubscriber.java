/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class CancelledSubscriber<T>
implements Subscriber<T> {
    @Override
    public void onSubscribe(Subscription subscription) {
        if (subscription == null) {
            throw new NullPointerException("Null subscription");
        }
        subscription.cancel();
    }

    @Override
    public void onNext(T t) {
    }

    @Override
    public void onError(Throwable error) {
        if (error == null) {
            throw new NullPointerException("Null error published");
        }
    }

    @Override
    public void onComplete() {
    }
}

