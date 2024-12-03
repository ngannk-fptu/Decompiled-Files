/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.ContainerV1;

public class ConfluenceSpaceContainerV1
extends ContainerV1 {
    private final String sourceId;
    private final String key;

    public ConfluenceSpaceContainerV1(String sourceId, String key) {
        this.sourceId = sourceId;
        this.key = key;
    }

    public String getSourceId() {
        return this.sourceId;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public ContainerType getType() {
        return ContainerType.ConfluenceSpace;
    }
}

