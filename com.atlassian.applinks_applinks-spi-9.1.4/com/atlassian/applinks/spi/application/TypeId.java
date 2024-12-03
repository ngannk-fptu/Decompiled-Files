/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.EntityType
 */
package com.atlassian.applinks.spi.application;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.spi.application.IdentifiableType;
import java.util.Objects;

public class TypeId
implements Comparable<TypeId> {
    private final String id;

    public TypeId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String get() {
        return this.id;
    }

    public static TypeId getTypeId(EntityType type) {
        return TypeId.get(type);
    }

    public static TypeId getTypeId(ApplicationType type) {
        return TypeId.get(type);
    }

    private static TypeId get(Object type) {
        try {
            return ((IdentifiableType)type).getId();
        }
        catch (ClassCastException e) {
            throw new IllegalStateException(type.getClass() + " should implement " + IdentifiableType.class);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TypeId typeId = (TypeId)o;
        return !(this.id != null ? !this.id.equals(typeId.id) : typeId.id != null);
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public String toString() {
        return this.id;
    }

    @Override
    public int compareTo(TypeId o) {
        return this.id.compareTo(o.get());
    }
}

