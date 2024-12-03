/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.DocValuesFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.AbstractFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;
import java.util.Objects;

public class BinaryFieldMapping
extends AbstractFieldMapping {
    public BinaryFieldMapping(String name) {
        super(name, true, false);
    }

    public FieldDescriptor createField(byte[] value) {
        return new DocValuesFieldDescriptor(this, Objects.requireNonNull(value));
    }

    public <T> T accept(FieldMappingVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

