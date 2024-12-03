/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentSelector
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.api.model.content.ContentSelector;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;

public interface ContentConvertible {
    public ContentType getContentTypeObject();

    public ContentSelector getSelector();

    public ContentId getContentId();

    public boolean shouldConvertToContent();
}

