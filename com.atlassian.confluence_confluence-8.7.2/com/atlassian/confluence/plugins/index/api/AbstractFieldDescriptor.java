/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.plugins.index.api;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.plugins.index.api.FieldVisitor;
import com.atlassian.confluence.plugins.index.api.mapping.FieldMapping;
import java.util.Objects;

@Internal
abstract class AbstractFieldDescriptor {
    protected final FieldMapping mapping;
    protected final String value;

    protected AbstractFieldDescriptor(FieldMapping mapping, String value) {
        if (mapping == null) {
            throw new IllegalArgumentException("mapping is required.");
        }
        this.mapping = mapping;
        this.value = value;
    }

    public FieldMapping getMapping() {
        return this.mapping;
    }

    public String getName() {
        return this.mapping.getName();
    }

    public String getValue() {
        return this.value;
    }

    public FieldDescriptor.Index getIndex() {
        return this.mapping.isIndexed() ? FieldDescriptor.Index.NOT_ANALYZED : FieldDescriptor.Index.NO;
    }

    public FieldDescriptor.Store getStore() {
        return this.mapping.isStored() ? FieldDescriptor.Store.YES : FieldDescriptor.Store.NO;
    }

    public abstract <T> T accept(FieldVisitor<T> var1);

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractFieldDescriptor that = (AbstractFieldDescriptor)o;
        return Objects.equals(this.value, that.value) && Objects.equals(this.mapping, that.mapping);
    }

    public int hashCode() {
        return Objects.hash(this.value, this.mapping);
    }
}

