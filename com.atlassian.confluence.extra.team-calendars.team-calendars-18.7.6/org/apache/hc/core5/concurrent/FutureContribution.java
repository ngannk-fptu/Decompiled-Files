/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.concurrent;

import org.apache.hc.core5.concurrent.BasicFuture;
import org.apache.hc.core5.concurrent.FutureCallback;

public abstract class FutureContribution<T>
implements FutureCallback<T> {
    private final BasicFuture<?> future;

    public FutureContribution(BasicFuture<?> future) {
        this.future = future;
    }

    @Override
    public final void failed(Exception ex) {
        if (this.future != null) {
            this.future.failed(ex);
        }
    }

    @Override
    public final void cancelled() {
        if (this.future != null) {
            this.future.cancel();
        }
    }
}

