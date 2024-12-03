/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

@ParametersAreNonnullByDefault
public abstract class AbstractFieldMapping
implements FieldMapping {
    private final String name;
    private final boolean stored;
    private final boolean indexed;

    protected AbstractFieldMapping(Builder builder) {
        this(builder.name, builder.stored, builder.indexed);
    }

    protected AbstractFieldMapping(String name, boolean stored, boolean indexed) {
        if (StringUtils.isBlank((CharSequence)name)) {
            throw new IllegalArgumentException("Name is required");
        }
        this.name = name;
        this.stored = stored;
        this.indexed = indexed;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isStored() {
        return this.stored;
    }

    @Override
    public boolean isIndexed() {
        return this.indexed;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{name='" + this.name + "', stored=" + this.stored + ", indexed=" + this.indexed + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractFieldMapping that = (AbstractFieldMapping)o;
        return this.stored == that.stored && this.indexed == that.indexed && this.name.equals(that.name);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.stored, this.indexed);
    }

    protected static abstract class Builder {
        private final String name;
        private boolean stored;
        private boolean indexed = true;

        protected Builder(String name) {
            if (StringUtils.isBlank((CharSequence)name)) {
                throw new IllegalArgumentException("name is required");
            }
            this.name = name;
        }

        protected Builder store(boolean stored) {
            this.stored = stored;
            return this;
        }

        protected Builder index(boolean indexed) {
            this.indexed = indexed;
            return this;
        }

        public abstract AbstractFieldMapping build();
    }
}

