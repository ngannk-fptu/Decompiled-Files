/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 */
package com.benryan.servlet.webdav;

import com.atlassian.confluence.pages.AbstractPage;
import com.benryan.servlet.webdav.PageResource;
import com.benryan.servlet.webdav.ResourceBuilder;

public final class ContentResource
extends PageResource {
    public static final String PATH_PREFIX = "content";

    public ContentResource(ResourceBuilder builder, AbstractPage page) {
        super(builder, page);
    }

    @Override
    public String getDisplayName() {
        return PATH_PREFIX;
    }
}

