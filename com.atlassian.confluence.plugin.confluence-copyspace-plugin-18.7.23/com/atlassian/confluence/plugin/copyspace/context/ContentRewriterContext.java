/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugin.copyspace.context;

import com.atlassian.confluence.core.ContentEntityObject;

public class ContentRewriterContext {
    private final ContentEntityObject container;
    private final String newSpaceKey;
    private final String originalSpaceKey;

    public ContentRewriterContext(ContentEntityObject container, String newSpaceKey, String originalSpaceKey) {
        this.container = container;
        this.newSpaceKey = newSpaceKey;
        this.originalSpaceKey = originalSpaceKey;
    }

    public ContentEntityObject getContainer() {
        return this.container;
    }

    public String getNewSpaceKey() {
        return this.newSpaceKey;
    }

    public String getOriginalSpaceKey() {
        return this.originalSpaceKey;
    }
}

