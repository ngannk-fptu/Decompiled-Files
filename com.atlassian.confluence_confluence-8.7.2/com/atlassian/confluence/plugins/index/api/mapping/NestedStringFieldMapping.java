/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.AbstractFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;
import java.util.Collection;
import java.util.Map;

public class NestedStringFieldMapping
extends AbstractFieldMapping {
    private static final String NESTED_FIELD_NAME = "values";

    public NestedStringFieldMapping(Builder builder) {
        super(builder);
    }

    public String getNestedFieldName() {
        return NESTED_FIELD_NAME;
    }

    public String getFullName() {
        return this.getName() + "." + this.getNestedFieldName();
    }

    public FieldDescriptor createField(Collection<String> values) {
        return new FieldDescriptor(this, String.join((CharSequence)"|", values), Map.of(this.getNestedFieldName(), values));
    }

    @Override
    public <R> R accept(FieldMappingVisitor<R> visitor) {
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
        public NestedStringFieldMapping build() {
            return new NestedStringFieldMapping(this);
        }
    }
}

