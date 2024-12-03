/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;

@Deprecated
public final class StoredFieldDescriptor
extends FieldDescriptor {
    public StoredFieldDescriptor(FieldMapping mapping, String value) {
        super(mapping, value);
    }

    public StoredFieldDescriptor(String name, String value) {
        this(StringFieldMapping.builder(name).store(true).index(false).build(), value);
    }

    @Override
    public <T> T accept(FieldVisitor<T> fieldVisitor) {
        return fieldVisitor.visit(this);
    }
}

