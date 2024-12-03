/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.hc.client5.http.async.methods;

import org.apache.hc.core5.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IgnoreCompleteExceptionFutureCallback<T>
implements FutureCallback<T> {
    private final FutureCallback<T> callback;
    private static final Logger LOG = LoggerFactory.getLogger(IgnoreCompleteExceptionFutureCallback.class);

    public IgnoreCompleteExceptionFutureCallback(FutureCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void completed(T result) {
        if (this.callback != null) {
            try {
                this.callback.completed(result);
            }
            catch (Exception ex) {
                LOG.error(ex.getMessage(), (Throwable)ex);
            }
        }
    }

    @Override
    public void failed(Exception ex) {
        if (this.callback != null) {
            this.callback.failed(ex);
        }
    }

    @Override
    public void cancelled() {
        if (this.callback != null) {
            this.callback.cancelled();
        }
    }
}

