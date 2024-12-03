/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface JsonMapper<T> {
    @Nonnull
    public Class<T> getType();

    @Nullable
    public T parseJson(@Nullable String var1);

    @Nullable
    public String toJson(@Nullable T var1);
}

