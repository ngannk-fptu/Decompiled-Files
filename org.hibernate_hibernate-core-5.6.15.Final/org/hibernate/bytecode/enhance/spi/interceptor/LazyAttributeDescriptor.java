/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import org.hibernate.mapping.Property;
import org.hibernate.type.Type;

public class LazyAttributeDescriptor {
    private final int attributeIndex;
    private final int lazyIndex;
    private final String name;
    private final Type type;
    private final String fetchGroupName;

    public static LazyAttributeDescriptor from(Property property, int attributeIndex, int lazyIndex) {
        String fetchGroupName = property.getLazyGroup();
        if (fetchGroupName == null) {
            fetchGroupName = property.getType().isCollectionType() ? property.getName() : "DEFAULT";
        }
        return new LazyAttributeDescriptor(attributeIndex, lazyIndex, property.getName(), property.getType(), fetchGroupName);
    }

    private LazyAttributeDescriptor(int attributeIndex, int lazyIndex, String name, Type type, String fetchGroupName) {
        assert (attributeIndex >= lazyIndex);
        this.attributeIndex = attributeIndex;
        this.lazyIndex = lazyIndex;
        this.name = name;
        this.type = type;
        this.fetchGroupName = fetchGroupName;
    }

    public int getAttributeIndex() {
        return this.attributeIndex;
    }

    public int getLazyIndex() {
        return this.lazyIndex;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public String getFetchGroupName() {
        return this.fetchGroupName;
    }
}

