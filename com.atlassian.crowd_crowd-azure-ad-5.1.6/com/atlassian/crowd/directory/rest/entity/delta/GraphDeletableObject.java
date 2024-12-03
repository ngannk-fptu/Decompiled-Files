/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rest.entity.delta;

import com.atlassian.crowd.directory.rest.entity.delta.GraphDeltaQueryRemoved;

public interface GraphDeletableObject {
    public static final String REMOVED_PROPERTY_NAME = "@removed";

    public GraphDeltaQueryRemoved getRemoved();
}

