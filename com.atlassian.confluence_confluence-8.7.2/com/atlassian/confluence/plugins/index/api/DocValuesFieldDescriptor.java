/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.BinaryFieldMapping;

@Deprecated
public final class DocValuesFieldDescriptor
extends FieldDescriptor {
    private final byte[] bytesValue;

    public DocValuesFieldDescriptor(BinaryFieldMapping mapping, byte[] value) {
        super(mapping, null);
        this.bytesValue = value;
    }

    @Deprecated
    public DocValuesFieldDescriptor(String name, byte[] value) {
        this(new BinaryFieldMapping(name), value);
    }

    @Override
    public FieldDescriptor.Store getStore() {
        return FieldDescriptor.Store.DOC_VALUES;
    }

    @Override
    public <T> T accept(FieldVisitor<T> fieldVisitor) {
        return fieldVisitor.visit(this);
    }

    public byte[] bytesValue() {
        return this.bytesValue;
    }

    @Override
    public Object getRawValue() {
        return this.bytesValue();
    }
}

