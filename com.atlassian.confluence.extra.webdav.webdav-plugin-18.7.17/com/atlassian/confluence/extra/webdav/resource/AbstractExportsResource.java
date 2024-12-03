/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public abstract class AbstractExportsResource
extends AbstractCollectionResource {
    public static final String DISPLAY_NAME = "@exports";

    public AbstractExportsResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    public abstract ContentEntityObject getContentEntityObject();
}

