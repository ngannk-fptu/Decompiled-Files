/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FloatFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.AbstractFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;

public class FloatFieldMapping
extends AbstractFieldMapping {
    protected FloatFieldMapping(Builder builder) {
        super(builder);
    }

    public FieldDescriptor createField(float value) {
        return new FloatFieldDescriptor(this, value);
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
        public FloatFieldMapping build() {
            return new FloatFieldMapping(this);
        }
    }
}

