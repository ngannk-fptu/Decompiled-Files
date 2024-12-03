/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.rest.manager;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.rest.entities.ContentEntity;

public interface RestContentManager {
    public ContentEntity getContentEntity(Long var1, boolean var2);

    public ContentEntity convertToContentEntity(ContentEntityObject var1);

    public ContentEntity expand(ContentEntity var1);
}

