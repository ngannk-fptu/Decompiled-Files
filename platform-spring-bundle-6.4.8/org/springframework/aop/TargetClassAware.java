/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import org.springframework.lang.Nullable;

public interface TargetClassAware {
    @Nullable
    public Class<?> getTargetClass();
}

