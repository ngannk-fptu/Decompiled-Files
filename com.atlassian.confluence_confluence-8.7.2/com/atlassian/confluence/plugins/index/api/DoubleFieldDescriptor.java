/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.DoubleFieldMapping;
import java.util.Objects;

@Deprecated
public final class DoubleFieldDescriptor
extends FieldDescriptor {
    private final double doubleValue;

    public DoubleFieldDescriptor(DoubleFieldMapping mapping, double value) {
        super(mapping, String.valueOf(value));
        this.doubleValue = value;
    }

    public DoubleFieldDescriptor(String name, double value, FieldDescriptor.Store store) {
        this(DoubleFieldMapping.builder(name).store(Objects.requireNonNull(store).isStored()).build(), value);
    }

    @Override
    public <T> T accept(FieldVisitor<T> fieldVisitor) {
        return fieldVisitor.visit(this);
    }

    public double doubleValue() {
        return this.doubleValue;
    }

    @Override
    public Object getRawValue() {
        return this.doubleValue();
    }
}

