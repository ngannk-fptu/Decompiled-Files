/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.LongFieldMapping;

@Deprecated
public final class NumericDocValuesFieldDescriptor
extends FieldDescriptor {
    private final long longValue;

    public NumericDocValuesFieldDescriptor(LongFieldMapping mapping, long longValue) {
        super(mapping, null);
        this.longValue = longValue;
    }

    public NumericDocValuesFieldDescriptor(String name, long longValue) {
        this(LongFieldMapping.builder(name).store(true).index(false).build(), longValue);
    }

    @Override
    public FieldDescriptor.Store getStore() {
        return FieldDescriptor.Store.DOC_VALUES;
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

