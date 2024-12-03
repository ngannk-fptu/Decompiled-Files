/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

@FunctionalInterface
interface ValueExtractor {
    @Nullable
    public Object extract(Method var1, @Nullable Object var2);
}

