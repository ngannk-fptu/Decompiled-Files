/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.aop;

import org.springframework.lang.Nullable;

public interface TargetClassAware {
    @Nullable
    public Class<?> getTargetClass();
}

