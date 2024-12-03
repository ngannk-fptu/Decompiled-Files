/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.plugins.index.api.AbstractFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.StringFieldMapping;
import org.apache.commons.lang3.StringUtils;

@Internal
public class FieldDescriptor
extends AbstractFieldDescriptor {
    protected Object rawValue;

    public FieldDescriptor(FieldMapping mapping, String value, Object rawValue) {
        super(mapping, value);
        this.rawValue = rawValue;
    }

    public FieldDescriptor(FieldMapping mapping, String value) {
        this(mapping, value, value);
    }

    @Deprecated
    public FieldDescriptor(String name, String value, Store store, Index index) {
        this(FieldDescriptor.createMapping(name, store, index), value);
    }

    private static StringFieldMapping createMapping(String name, Store store, Index index) {
        if (StringUtils.isBlank((CharSequence)name)) {
            throw new IllegalArgumentException("name is required.");
        }
        if (store == null) {
            throw new IllegalArgumentException("store is required.");
        }
        if (index == null) {
            throw new IllegalArgumentException("index is required.");
        }
        return StringFieldMapping.builder(name).store(store.isStored()).index(index != Index.NO).build();
    }

    @Override
    public <T> T accept(FieldVisitor<T> fieldVisitor) {
        return fieldVisitor.visit(this);
    }

    public Object getRawValue() {
        return this.rawValue;
    }

    public static enum Store {
        NO,
        YES,
        DOC_VALUES,
        SORTED_DOC_VALUES;


        public boolean isStored() {
            return this == YES;
        }
    }

    @Deprecated
    public static enum Index {
        NO,
        ANALYZED,
        NOT_ANALYZED;

    }
}

