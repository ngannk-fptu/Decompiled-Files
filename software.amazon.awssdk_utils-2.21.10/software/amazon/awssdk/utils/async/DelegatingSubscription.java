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
public class DelegatingSubscription
implements Subscription {
    private final Subscription s;

    protected DelegatingSubscription(Subscription s) {
        this.s = s;
    }

    public void request(long l) {
        this.s.request(l);
    }

    public void cancel() {
        this.s.cancel();
    }
}

