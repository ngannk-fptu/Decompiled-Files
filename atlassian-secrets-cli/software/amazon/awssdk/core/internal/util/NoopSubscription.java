/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void request(long l) {
        if (l < 1L) {
            this.subscriber.onError(new IllegalArgumentException("Demand must be positive!"));
        }
    }

    @Override
    public void cancel() {
    }
}

