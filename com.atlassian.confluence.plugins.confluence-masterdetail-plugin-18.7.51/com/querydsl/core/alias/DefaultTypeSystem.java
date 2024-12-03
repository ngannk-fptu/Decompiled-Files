/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.alias;

import com.querydsl.core.alias.TypeSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultTypeSystem
implements TypeSystem {
    @Override
    public boolean isCollectionType(Class<?> cl) {
        return Collection.class.isAssignableFrom(cl);
    }

    @Override
    public boolean isListType(Class<?> cl) {
        return List.class.isAssignableFrom(cl);
    }

    @Override
    public boolean isSetType(Class<?> cl) {
        return Set.class.isAssignableFrom(cl);
    }

    @Override
    public boolean isMapType(Class<?> cl) {
        return Map.class.isAssignableFrom(cl);
    }
}

