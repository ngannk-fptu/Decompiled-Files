/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.reactivestreams.Subscription
 *  software.amazon.awssdk.annotations.SdkProtectedApi
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

    public void request(long n) {
    }

    public void cancel() {
        this.delegate.cancel();
    }
}

