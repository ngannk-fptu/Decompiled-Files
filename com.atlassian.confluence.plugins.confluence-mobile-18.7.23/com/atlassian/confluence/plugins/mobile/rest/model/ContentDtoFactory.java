/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.mobile.rest.model;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.mobile.rest.model.ContentDto;

public interface ContentDtoFactory {
    public ContentDto getContentDto(ContentEntityObject var1);
}

