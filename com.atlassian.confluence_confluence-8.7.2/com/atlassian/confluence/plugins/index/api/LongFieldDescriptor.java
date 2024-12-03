/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.LongFieldMapping;
import java.util.Objects;

@Deprecated
public final class LongFieldDescriptor
extends FieldDescriptor {
    private final long longValue;

    public LongFieldDescriptor(LongFieldMapping mapping, long value) {
        super(mapping, String.valueOf(value));
        this.longValue = value;
    }

    public LongFieldDescriptor(String name, long value, FieldDescriptor.Store store) {
        this(LongFieldMapping.builder(name).store(Objects.requireNonNull(store).isStored()).build(), value);
    }

    @Override
    public <T> T accept(FieldVisitor<T> fieldVisitor) {
        return fieldVisitor.visit(this);
    }

    public long longValue() {
        return this.longValue;
    }

    @Override
    public Object getRawValue() {
        return this.longValue();
    }
}

