/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.converter;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface Converter<S, T> {
    @Nullable
    public T convert(S var1);
}

