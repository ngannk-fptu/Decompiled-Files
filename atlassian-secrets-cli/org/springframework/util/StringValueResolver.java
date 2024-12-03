/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.util;

import org.springframework.lang.Nullable;

@FunctionalInterface
public interface StringValueResolver {
    @Nullable
    public String resolveStringValue(String var1);
}

