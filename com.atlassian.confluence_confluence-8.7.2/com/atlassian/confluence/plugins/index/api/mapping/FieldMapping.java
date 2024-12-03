/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.index.api.mapping;

import com.atlassian.confluence.plugins.index.api.mapping.FieldMappingVisitor;

public interface FieldMapping {
    public String getName();

    public boolean isStored();

    public boolean isIndexed();

    public <R> R accept(FieldMappingVisitor<R> var1);
}

