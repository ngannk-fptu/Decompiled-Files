/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.tuple.Attribute;
import org.hibernate.tuple.Property;
import org.hibernate.type.Type;

public abstract class AbstractAttribute
implements Attribute,
Property {
    private final String attributeName;
    private final Type attributeType;

    protected AbstractAttribute(String attributeName, Type attributeType) {
        this.attributeName = attributeName;
        this.attributeType = attributeType;
    }

    @Override
    public String getNode() {
        return null;
    }

    @Override
    public String getName() {
        return this.attributeName;
    }

    @Override
    public Type getType() {
        return this.attributeType;
    }
}

