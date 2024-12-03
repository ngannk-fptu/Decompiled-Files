/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util.concurrent;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface SuccessCallback<T> {
    public void onSuccess(@Nullable T var1);
}

