/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.async;

import org.reactivestreams.Subscription;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class DemandIgnoringSubscription
implements Subscription {
    private final Subscription delegate;

    public DemandIgnoringSubscription(Subscription delegate) {
        this.delegate = delegate;
    }

    @Override
    public void request(long n) {
    }

    @Override
    public void cancel() {
        this.delegate.cancel();
    }
}

