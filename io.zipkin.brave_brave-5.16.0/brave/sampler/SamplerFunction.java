/*
 * Decompiled with CFR 0.152.
 */
package brave.sampler;

import brave.internal.Nullable;

public interface SamplerFunction<T> {
    @Nullable
    public Boolean trySample(@Nullable T var1);
}

