/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.IntFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.AbstractFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;

public class IntFieldMapping
extends AbstractFieldMapping {
    protected IntFieldMapping(Builder builder) {
        super(builder);
    }

    public FieldDescriptor createField(int value) {
        return new IntFieldDescriptor(this, value);
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
        public IntFieldMapping build() {
            return new IntFieldMapping(this);
        }
    }
}

