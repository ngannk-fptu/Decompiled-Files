/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.VersionHistorySummary
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractTextContentResource;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.UnsupportedEncodingException;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public abstract class AbstractVersionContentResource
extends AbstractTextContentResource {
    public static final String DISPLAY_NAME_PREFIX = "Version ";
    public static final String DISPLAY_NAME_SUFFIX = ".txt";
    private final PageManager pageManager;
    protected final int versionNumber;
    private AbstractPage versionedPage;

    public AbstractVersionContentResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SettingsManager settingsManager, @ComponentImport PageManager pageManager, int versionNumber) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, settingsManager);
        this.pageManager = pageManager;
        this.versionNumber = versionNumber;
    }

    protected PageManager getPageManager() {
        return this.pageManager;
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public abstract AbstractPage getAbstractPage();

    public AbstractPage getVersionedPage() {
        if (null == this.versionedPage) {
            this.versionedPage = this.pageManager.getPageByVersion(this.getAbstractPage(), this.versionNumber);
        }
        return this.versionedPage;
    }

    @Override
    protected byte[] getTextContentAsBytes(String encoding) throws UnsupportedEncodingException {
        return this.getVersionedPage().getBodyContent().getBody().getBytes(encoding);
    }

    @Override
    public long getModificationTime() {
        return this.getCreationtTime();
    }

    @Override
    protected long getCreationtTime() {
        return this.getVersionedPage().getCreationDate().getTime();
    }

    private boolean versionExists() {
        for (VersionHistorySummary versionHistorySummary : this.pageManager.getVersionHistorySummaries((ContentEntityObject)this.getAbstractPage())) {
            if (versionHistorySummary.getVersion() != this.versionNumber) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean exists() {
        return super.exists() && this.versionExists() && !((ConfluenceDavSession)this.getSession()).getResourceStates().isContentVersionTextHidden((ContentEntityObject)this.getAbstractPage(), DISPLAY_NAME_PREFIX + this.versionNumber + DISPLAY_NAME_SUFFIX);
    }

    @Override
    public String getDisplayName() {
        return new StringBuffer(DISPLAY_NAME_PREFIX).append(this.versionNumber).append(DISPLAY_NAME_SUFFIX).toString();
    }
}

