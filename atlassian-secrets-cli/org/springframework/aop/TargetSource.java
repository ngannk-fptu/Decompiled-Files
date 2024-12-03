/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop;

import org.springframework.aop.TargetClassAware;
import org.springframework.lang.Nullable;

public interface TargetSource
extends TargetClassAware {
    @Override
    @Nullable
    public Class<?> getTargetClass();

    public boolean isStatic();

    @Nullable
    public Object getTarget() throws Exception;

    public void releaseTarget(Object var1) throws Exception;
}

