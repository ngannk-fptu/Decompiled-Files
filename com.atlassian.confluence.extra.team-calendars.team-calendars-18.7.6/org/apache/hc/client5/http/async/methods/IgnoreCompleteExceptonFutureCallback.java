/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.async.methods;

import org.apache.hc.client5.http.async.methods.IgnoreCompleteExceptionFutureCallback;
import org.apache.hc.core5.concurrent.FutureCallback;

@Deprecated
public class IgnoreCompleteExceptonFutureCallback<T>
extends IgnoreCompleteExceptionFutureCallback<T> {
    public IgnoreCompleteExceptonFutureCallback(FutureCallback<T> callback) {
        super(callback);
    }
}

