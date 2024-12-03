/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.alias;

public interface TypeSystem {
    public boolean isCollectionType(Class<?> var1);

    public boolean isSetType(Class<?> var1);

    public boolean isListType(Class<?> var1);

    public boolean isMapType(Class<?> var1);
}

