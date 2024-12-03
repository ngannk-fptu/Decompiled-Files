/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractTextContentResource;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.UnsupportedEncodingException;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class PageContentResourceImpl
extends AbstractTextContentResource {
    public static final String DISPLAY_NAME_SUFFIX = ".txt";
    private final PageManager pageManager;
    private final String spaceKey;
    private final String pageTitle;

    public PageContentResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SettingsManager settingsManager, @ComponentImport PageManager pageManager, String spaceKey, String pageTitle) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, settingsManager);
        this.pageManager = pageManager;
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
    }

    public Page getPage() {
        return this.pageManager.getPage(this.spaceKey, this.pageTitle);
    }

    @Override
    public boolean exists() {
        return super.exists() && !((ConfluenceDavSession)this.getSession()).getResourceStates().isContentMarkupHidden((ContentEntityObject)this.getPage());
    }

    @Override
    protected byte[] getTextContentAsBytes(String encoding) throws UnsupportedEncodingException {
        return this.getPage().getBodyContent().getBody().getBytes(encoding);
    }

    @Override
    public long getModificationTime() {
        return this.getPage().getLastModificationDate().getTime();
    }

    @Override
    protected long getCreationtTime() {
        return this.getPage().getCreationDate().getTime();
    }

    @Override
    public String getDisplayName() {
        return this.getPage().getTitle() + DISPLAY_NAME_SUFFIX;
    }
}

