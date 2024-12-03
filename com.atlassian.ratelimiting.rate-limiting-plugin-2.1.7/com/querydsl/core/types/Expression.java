/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Visitor;
import java.io.Serializable;
import javax.annotation.Nullable;

public interface Expression<T>
extends Serializable {
    @Nullable
    public <R, C> R accept(Visitor<R, C> var1, @Nullable C var2);

    public Class<? extends T> getType();
}

