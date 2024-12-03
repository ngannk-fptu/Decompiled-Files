/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils.cache;

import java.util.concurrent.atomic.AtomicBoolean;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.utils.cache.CachedSupplier;

@SdkProtectedApi
public class OneCallerBlocks
implements CachedSupplier.PrefetchStrategy {
    private final AtomicBoolean currentlyRefreshing = new AtomicBoolean(false);

    @Override
    public void prefetch(Runnable valueUpdater) {
        if (this.currentlyRefreshing.compareAndSet(false, true)) {
            try {
                valueUpdater.run();
            }
            finally {
                this.currentlyRefreshing.set(false);
            }
        }
    }
}

