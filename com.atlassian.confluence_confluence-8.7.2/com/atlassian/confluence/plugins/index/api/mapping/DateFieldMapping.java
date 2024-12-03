/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.StringFieldDescriptor;
import com.atlassian.confluence.plugins.index.api.mapping.AbstractFieldMapping;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import java.util.Date;
import java.util.Objects;

public class DateFieldMapping
extends AbstractFieldMapping {
    private final LuceneUtils.Resolution resolution = LuceneUtils.Resolution.MILLISECOND;

    protected DateFieldMapping(Builder builder) {
        super(builder);
    }

    public FieldDescriptor createField(Date value) {
        return new StringFieldDescriptor(this, LuceneUtils.dateToString(Objects.requireNonNull(value), this.resolution));
    }

    public <T> T accept(FieldMappingVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public LuceneUtils.Resolution getResolution() {
        return this.resolution;
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
        public DateFieldMapping build() {
            return new DateFieldMapping(this);
        }
    }
}

