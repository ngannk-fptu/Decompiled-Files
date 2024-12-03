/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.extension;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MetadataProperty {
    private final String propertyName;
    private final Type propertyType;
    private final List<MetadataProperty> children;

    public MetadataProperty(String propertyName, Type propertyType) {
        this.propertyName = Objects.requireNonNull(propertyName);
        this.propertyType = propertyType;
        this.children = Collections.emptyList();
    }

    public MetadataProperty(String propertyName, List<MetadataProperty> children) {
        this.propertyName = Objects.requireNonNull(propertyName);
        this.propertyType = null;
        this.children = children;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public @Nullable Type getPropertyType() {
        return this.propertyType;
    }

    public List<MetadataProperty> getChildren() {
        return this.children;
    }
}

