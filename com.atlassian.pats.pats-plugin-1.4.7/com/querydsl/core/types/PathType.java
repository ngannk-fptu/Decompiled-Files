/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Operator;

public enum PathType implements Operator
{
    ARRAYVALUE,
    ARRAYVALUE_CONSTANT,
    COLLECTION_ANY,
    DELEGATE,
    LISTVALUE,
    LISTVALUE_CONSTANT,
    MAPVALUE,
    MAPVALUE_CONSTANT,
    PROPERTY,
    VARIABLE;


    @Override
    public Class<?> getType() {
        return Object.class;
    }
}

