/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;
import org.apache.commons.lang3.StringUtils;

@Deprecated
public final class StringFieldDescriptor
extends FieldDescriptor {
    public StringFieldDescriptor(FieldMapping mapping, String value) {
        super(mapping, value);
    }

    public StringFieldDescriptor(String name, String value, FieldDescriptor.Store store) {
        this(StringFieldDescriptor.createMapping(name, store), value);
    }

    private static StringFieldMapping createMapping(String name, FieldDescriptor.Store store) {
        if (StringUtils.isBlank((CharSequence)name)) {
            throw new IllegalArgumentException("name is required.");
        }
        if (store == null) {
            throw new IllegalArgumentException("store is required.");
        }
        return StringFieldMapping.builder(name).store(store.isStored()).build();
    }

    @Override
    public FieldDescriptor.Index getIndex() {
        return FieldDescriptor.Index.NOT_ANALYZED;
    }

    @Override
    public <T> T accept(FieldVisitor<T> fieldVisitor) {
        return fieldVisitor.visit(this);
    }
}

