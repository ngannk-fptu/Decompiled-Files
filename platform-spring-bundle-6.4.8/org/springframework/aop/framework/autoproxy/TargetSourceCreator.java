/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.autoproxy;

import org.springframework.aop.TargetSource;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface TargetSourceCreator {
    @Nullable
    public TargetSource getTargetSource(Class<?> var1, String var2);
}

