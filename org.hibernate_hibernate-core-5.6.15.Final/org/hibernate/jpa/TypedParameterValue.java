/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa;

import org.hibernate.type.Type;

public class TypedParameterValue {
    private final Type type;
    private final Object value;

    public TypedParameterValue(Type type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public Type getType() {
        return this.type;
    }
}

