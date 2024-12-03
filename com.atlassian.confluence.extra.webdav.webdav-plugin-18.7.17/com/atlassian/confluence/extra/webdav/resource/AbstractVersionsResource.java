/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.WebdavSettingsManager;
import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public abstract class AbstractVersionsResource
extends AbstractCollectionResource {
    public static final String DISPLAY_NAME = "@versions";
    private final WebdavSettingsManager webdavSettingsManager;
    private final PageManager pageManager;

    public AbstractVersionsResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, WebdavSettingsManager webdavSettingsManager, @ComponentImport PageManager pageManager) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.webdavSettingsManager = webdavSettingsManager;
        this.pageManager = pageManager;
    }

    public PageManager getPageManager() {
        return this.pageManager;
    }

    public abstract ContentEntityObject getContentEntityObject();

    @Override
    protected long getCreationtTime() {
        return this.getContentEntityObject().getCreationDate().getTime();
    }

    @Override
    public boolean exists() {
        return super.exists() && !((ConfluenceDavSession)this.getSession()).getResourceStates().isContentVersionsHidden(this.getContentEntityObject()) && this.webdavSettingsManager.isContentVersionsResourceEnabled();
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }
}

