/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.concurrent;

import org.apache.hc.core5.concurrent.FutureCallback;

public abstract class CallbackContribution<T>
implements FutureCallback<T> {
    private final FutureCallback<?> callback;

    public CallbackContribution(FutureCallback<?> callback) {
        this.callback = callback;
    }

    @Override
    public final void failed(Exception ex) {
        if (this.callback != null) {
            this.callback.failed(ex);
        }
    }

    @Override
    public final void cancelled() {
        if (this.callback != null) {
            this.callback.cancelled();
        }
    }
}

