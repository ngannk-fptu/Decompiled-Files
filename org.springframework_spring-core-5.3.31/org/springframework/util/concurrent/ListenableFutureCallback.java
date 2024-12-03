/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util.concurrent;

import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

public interface ListenableFutureCallback<T>
extends SuccessCallback<T>,
FailureCallback {
}

