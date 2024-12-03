/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.ParametricNullness;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface FutureCallback<V> {
    public void onSuccess(@ParametricNullness V var1);

    public void onFailure(Throwable var1);
}

