/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal.nrs;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class CancelledSubscriber<T>
implements Subscriber<T> {
    public void onSubscribe(Subscription subscription) {
        if (subscription == null) {
            throw new NullPointerException("Null subscription");
        }
        subscription.cancel();
    }

    public void onNext(T t) {
    }

    public void onError(Throwable error) {
        if (error == null) {
            throw new NullPointerException("Null error published");
        }
    }

    public void onComplete() {
    }
}

