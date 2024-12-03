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
import com.atlassian.confluence.extra.webdav.WebdavSettingsManager;
import com.atlassian.confluence.extra.webdav.resource.AbstractTextContentResource;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.UnsupportedEncodingException;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class PageUrlResourceImpl
extends AbstractTextContentResource {
    public static final String CONTENT_TYPE = "text/url";
    public static final String DISPLAY_NAME_SUFFIX = ".url";
    private final WebdavSettingsManager webdavSettingsManager;
    private final PageManager pageManager;
    private final String spaceKey;
    private final String pageTitle;

    public PageUrlResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SettingsManager settingsManager, WebdavSettingsManager webdavSettingsManager, @ComponentImport PageManager pageManager, String spaceKey, String pageTitle) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, settingsManager);
        this.webdavSettingsManager = webdavSettingsManager;
        this.pageManager = pageManager;
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
    }

    public Page getPage() {
        return this.pageManager.getPage(this.spaceKey, this.pageTitle);
    }

    @Override
    public boolean exists() {
        return super.exists() && !((ConfluenceDavSession)this.getSession()).getResourceStates().isContentUrlHidden((ContentEntityObject)this.getPage()) && this.webdavSettingsManager.isContentUrlResourceEnabled();
    }

    @Override
    protected byte[] getTextContentAsBytes(String encoding) throws UnsupportedEncodingException {
        return new StringBuffer("[InternetShortcut]\r\n").append("URL=").append(this.getSettingsManager().getGlobalSettings().getBaseUrl()).append(this.getPage().getUrlPath()).append("\r\n").toString().getBytes(encoding);
    }

    @Override
    protected String getContentTypeBase() {
        return CONTENT_TYPE;
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

