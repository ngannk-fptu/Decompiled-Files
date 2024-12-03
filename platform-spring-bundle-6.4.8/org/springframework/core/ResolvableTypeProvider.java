/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core;

import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public interface ResolvableTypeProvider {
    @Nullable
    public ResolvableType getResolvableType();
}

