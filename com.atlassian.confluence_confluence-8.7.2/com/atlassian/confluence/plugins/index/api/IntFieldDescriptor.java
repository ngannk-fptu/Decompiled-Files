/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.IntFieldMapping;
import java.util.Objects;

@Deprecated
public final class IntFieldDescriptor
extends FieldDescriptor {
    private final int intValue;

    public IntFieldDescriptor(IntFieldMapping mapping, int value) {
        super(mapping, String.valueOf(value));
        this.intValue = value;
    }

    public IntFieldDescriptor(String name, int value, FieldDescriptor.Store store) {
        this(IntFieldMapping.builder(name).store(Objects.requireNonNull(store).isStored()).build(), value);
    }

    @Override
    public <T> T accept(FieldVisitor<T> fieldVisitor) {
        return fieldVisitor.visit(this);
    }

    public int intValue() {
        return this.intValue;
    }

    @Override
    public Object getRawValue() {
        return this.intValue();
    }
}

