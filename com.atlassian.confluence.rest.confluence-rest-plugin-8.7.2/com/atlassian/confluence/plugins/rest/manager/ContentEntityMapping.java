/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;

public class ContentEntityMapping {
    private final ContentEntity contentEntity;
    private final ContentEntityObject confluenceObject;

    public ContentEntityMapping(ContentEntity contentEntity, ContentEntityObject confluenceObject) {
        this.contentEntity = contentEntity;
        this.confluenceObject = confluenceObject;
    }

    public ContentEntity getContentEntity() {
        return this.contentEntity;
    }

    public ContentEntityObject getConfluenceObject() {
        return this.confluenceObject;
    }
}

