/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.AnalyzerDescriptorProvider;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.TextFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.AbstractFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;
import java.util.Objects;

public class TextFieldMapping
extends AbstractFieldMapping {
    private final AnalyzerDescriptorProvider analyzer;
    private final AnalyzerDescriptorProvider searchAnalyzer;

    protected TextFieldMapping(Builder builder) {
        super(builder);
        this.analyzer = Objects.requireNonNull(builder.analyzer);
        this.searchAnalyzer = Objects.requireNonNull(builder.searchAnalyzer);
    }

    public AnalyzerDescriptorProvider getAnalyzer() {
        return this.analyzer;
    }

    public AnalyzerDescriptorProvider getSearchAnalyzer() {
        return this.searchAnalyzer;
    }

    public FieldDescriptor createField(String value) {
        return new TextFieldDescriptor(this, value);
    }

    public <T> T accept(FieldMappingVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{name='" + this.getName() + "', stored=" + this.isStored() + ", indexed=" + this.isIndexed() + ", analyzer=" + this.analyzer + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TextFieldMapping that = (TextFieldMapping)o;
        return Objects.equals(this.analyzer, that.analyzer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.analyzer);
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder
    extends AbstractFieldMapping.Builder {
        private AnalyzerDescriptorProvider analyzer = AnalyzerDescriptorProvider.EMPTY;
        private AnalyzerDescriptorProvider searchAnalyzer = AnalyzerDescriptorProvider.EMPTY;

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

        public Builder analyzer(AnalyzerDescriptorProvider analyzer) {
            this.analyzer = Objects.requireNonNull(analyzer);
            return this;
        }

        public Builder searchAnalyzer(AnalyzerDescriptorProvider searchAnalyzer) {
            this.searchAnalyzer = Objects.requireNonNull(searchAnalyzer);
            return this;
        }

        @Override
        public TextFieldMapping build() {
            return new TextFieldMapping(this);
        }
    }
}

