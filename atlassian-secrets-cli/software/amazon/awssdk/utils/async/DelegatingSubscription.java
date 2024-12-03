/*
 * Decompiled with CFR 0.152.
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

    @Override
    public void request(long l) {
        this.s.request(l);
    }

    @Override
    public void cancel() {
        this.s.cancel();
    }
}

