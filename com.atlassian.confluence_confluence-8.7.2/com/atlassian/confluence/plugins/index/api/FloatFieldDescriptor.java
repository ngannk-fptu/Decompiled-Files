/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.FloatFieldMapping;
import java.util.Objects;

@Deprecated
public final class FloatFieldDescriptor
extends FieldDescriptor {
    private final float floatValue;

    public FloatFieldDescriptor(FloatFieldMapping mapping, float value) {
        super(mapping, String.valueOf(value));
        this.floatValue = value;
    }

    public FloatFieldDescriptor(String name, float value, FieldDescriptor.Store store) {
        this(FloatFieldMapping.builder(name).store(Objects.requireNonNull(store).isStored()).build(), value);
    }

    @Override
    public <T> T accept(FieldVisitor<T> fieldVisitor) {
        return fieldVisitor.visit(this);
    }

    public float floatValue() {
        return this.floatValue;
    }

    @Override
    public Object getRawValue() {
        return Float.valueOf(this.floatValue());
    }
}

