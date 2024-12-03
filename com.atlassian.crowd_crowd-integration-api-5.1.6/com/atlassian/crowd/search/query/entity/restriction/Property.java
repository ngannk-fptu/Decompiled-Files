/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.search.query.entity.restriction;

public interface Property<V> {
    public String getPropertyName();

    public Class<V> getPropertyType();
}

