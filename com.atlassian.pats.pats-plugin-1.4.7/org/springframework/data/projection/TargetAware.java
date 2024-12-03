/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnore
 *  org.springframework.aop.RawTargetAccess
 *  org.springframework.aop.TargetClassAware
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.projection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.aop.RawTargetAccess;
import org.springframework.aop.TargetClassAware;
import org.springframework.lang.Nullable;

public interface TargetAware
extends TargetClassAware,
RawTargetAccess {
    @Nullable
    @JsonIgnore
    public Class<?> getTargetClass();

    @JsonIgnore
    public Object getTarget();

    @JsonIgnore
    public Class<?> getDecoratedClass();
}

