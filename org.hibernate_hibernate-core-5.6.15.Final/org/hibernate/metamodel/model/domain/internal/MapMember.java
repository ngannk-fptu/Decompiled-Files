/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.domain.internal;

import java.lang.reflect.Member;

public class MapMember
implements Member {
    private String name;
    private final Class<?> type;

    public MapMember(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public Class<?> getType() {
        return this.type;
    }

    @Override
    public int getModifiers() {
        return 1;
    }

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return null;
    }
}

