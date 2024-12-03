/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.Type;

public abstract class AbstractType<T>
implements Type<T> {
    private final int type;

    public AbstractType(int type) {
        this.type = type;
    }

    @Override
    public final int[] getSQLTypes() {
        return new int[]{this.type};
    }

    @Override
    public String getLiteral(T value) {
        return value.toString();
    }
}

