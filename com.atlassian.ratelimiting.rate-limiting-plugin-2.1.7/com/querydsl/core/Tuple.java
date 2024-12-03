/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core;

import com.querydsl.core.types.Expression;
import javax.annotation.Nullable;

public interface Tuple {
    @Nullable
    public <T> T get(int var1, Class<T> var2);

    @Nullable
    public <T> T get(Expression<T> var1);

    public int size();

    public Object[] toArray();

    public boolean equals(Object var1);

    public int hashCode();
}

