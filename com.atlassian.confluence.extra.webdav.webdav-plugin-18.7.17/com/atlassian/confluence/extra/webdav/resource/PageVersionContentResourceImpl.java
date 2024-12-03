/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractVersionContentResource;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class PageVersionContentResourceImpl
extends AbstractVersionContentResource {
    private final String spaceKey;
    private final String pageTitle;
    private Page page;

    public PageVersionContentResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SettingsManager settingsManager, @ComponentImport PageManager pageManager, String spaceKey, String pageTitle, int versionNumber) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, settingsManager, pageManager, versionNumber);
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
    }

    @Override
    public AbstractPage getAbstractPage() {
        if (null == this.page) {
            this.page = this.getPageManager().getPage(this.spaceKey, this.pageTitle);
        }
        return this.page;
    }
}

