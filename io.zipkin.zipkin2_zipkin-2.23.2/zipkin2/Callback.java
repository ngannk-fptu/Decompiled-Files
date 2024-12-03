/*
 * Decompiled with CFR 0.152.
 */
package zipkin2;

import zipkin2.internal.Nullable;

public interface Callback<V> {
    public void onSuccess(@Nullable V var1);

    public void onError(Throwable var1);
}

