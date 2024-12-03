/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StoredFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.AbstractFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;

public class StringFieldMapping
extends AbstractFieldMapping {
    protected StringFieldMapping(Builder builder) {
        super(builder);
    }

    public FieldDescriptor createField(String value) {
        return this.isIndexed() ? new StringFieldDescriptor(this, value) : new StoredFieldDescriptor(this, value);
    }

    public <T> T accept(FieldMappingVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder
    extends AbstractFieldMapping.Builder {
        public Builder(String name) {
            super(name);
        }

        @Override
        public Builder store(boolean stored) {
            super.store(stored);
            return this;
        }

        @Override
        public Builder index(boolean indexed) {
            super.index(indexed);
            return this;
        }

        @Override
        public StringFieldMapping build() {
            return new StringFieldMapping(this);
        }
    }
}

