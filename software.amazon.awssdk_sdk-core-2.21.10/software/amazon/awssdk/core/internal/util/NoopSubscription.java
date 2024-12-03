/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscriber
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal.util;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class NoopSubscription
implements Subscription {
    private final Subscriber<?> subscriber;

    public NoopSubscription(Subscriber<?> subscriber) {
        this.subscriber = subscriber;
    }

    public void request(long l) {
        if (l < 1L) {
            this.subscriber.onError((Throwable)new IllegalArgumentException("Demand must be positive!"));
        }
    }

    public void cancel() {
    }
}

