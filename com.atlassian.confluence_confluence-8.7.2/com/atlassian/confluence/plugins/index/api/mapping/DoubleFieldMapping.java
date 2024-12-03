/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.DoubleFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.AbstractFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;

public class DoubleFieldMapping
extends AbstractFieldMapping {
    protected DoubleFieldMapping(Builder builder) {
        super(builder);
    }

    public FieldDescriptor createField(double value) {
        return new DoubleFieldDescriptor(this, value);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public <T> T accept(FieldMappingVisitor<T> visitor) {
        return visitor.visit(this);
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
        public DoubleFieldMapping build() {
            return new DoubleFieldMapping(this);
        }
    }
}

