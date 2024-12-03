/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.service;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.service.AbstractSingleEntityLocator;

public class IdContentLocator
extends AbstractSingleEntityLocator {
    private final ContentEntityManager contentEntityManager;
    private final long contentId;

    public IdContentLocator(ContentEntityManager contentEntityManager, long contentId) {
        this.contentEntityManager = contentEntityManager;
        this.contentId = contentId;
    }

    @Override
    public ConfluenceEntityObject getEntity() {
        return this.contentEntityManager.getById(this.contentId);
    }

    public long getContentId() {
        return this.contentId;
    }
}

