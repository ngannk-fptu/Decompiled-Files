/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.ResourceStates;
import com.atlassian.confluence.extra.webdav.WebdavSettingsManager;
import com.atlassian.confluence.extra.webdav.resource.AbstractExportsResource;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class PageExportsResourceImpl
extends AbstractExportsResource {
    private final WebdavSettingsManager webdavSettingsManager;
    private final PageManager pageManager;
    private final String spaceKey;
    private final String pageTitle;
    private Page page;

    public PageExportsResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, WebdavSettingsManager webdavSettingsManager, @ComponentImport PageManager pageManager, String spaceKey, String pageTitle) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.webdavSettingsManager = webdavSettingsManager;
        this.pageManager = pageManager;
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
    }

    @Override
    public ContentEntityObject getContentEntityObject() {
        if (null == this.page) {
            this.page = this.pageManager.getPage(this.spaceKey, this.pageTitle);
        }
        return this.page;
    }

    @Override
    public boolean exists() {
        return super.exists() && !((ConfluenceDavSession)this.getSession()).getResourceStates().isContentExportsHidden(this.getContentEntityObject()) && this.webdavSettingsManager.isContentExportsResourceEnabled();
    }

    @Override
    public void addMember(DavResource davResource, InputContext inputContext) throws DavException {
        String[] pathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        String resourceName = pathComponents[pathComponents.length - 1];
        ResourceStates resourceStates = ((ConfluenceDavSession)this.getSession()).getResourceStates();
        ContentEntityObject contentEntityObject = this.getContentEntityObject();
        if (StringUtils.equals((String)(this.pageTitle + ".pdf"), (String)resourceName)) {
            resourceStates.unhideContentPdfExport(contentEntityObject);
        }
        if (StringUtils.equals((String)(this.pageTitle + ".doc"), (String)resourceName)) {
            resourceStates.unhideContentWordExport(contentEntityObject);
        }
        if (StringUtils.equals((String)"README.txt", (String)resourceName)) {
            resourceStates.unhideContentExportsReadme(contentEntityObject);
        }
    }

    @Override
    public void removeMember(DavResource davResource) throws DavException {
        String[] pathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        String resourceName = pathComponents[pathComponents.length - 1];
        ResourceStates resourceStates = ((ConfluenceDavSession)this.getSession()).getResourceStates();
        ContentEntityObject contentEntityObject = this.getContentEntityObject();
        if (StringUtils.equals((String)(this.pageTitle + ".pdf"), (String)resourceName)) {
            resourceStates.hideContentPdfExport(contentEntityObject);
        }
        if (StringUtils.equals((String)(this.pageTitle + ".doc"), (String)resourceName)) {
            resourceStates.hideContentWordExport(contentEntityObject);
        }
        if (StringUtils.equals((String)"README.txt", (String)resourceName)) {
            resourceStates.hideContentExportsReadme(contentEntityObject);
        }
    }

    @Override
    protected long getCreationtTime() {
        return this.getContentEntityObject().getCreationDate().getTime();
    }

    @Override
    public long getModificationTime() {
        return this.getContentEntityObject().getLastModificationDate().getTime();
    }

    private DavResourceLocator getPageWordExportContentResourceLocator() {
        DavResourceLocator locator = this.getLocator();
        StringBuffer contentPathBuffer = new StringBuffer(this.getParentResourcePath());
        contentPathBuffer.append('/').append("@exports").append('/').append(this.pageTitle).append(".doc");
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
    }

    private DavResourceLocator getPagePdfExportContentResourceLocator() {
        DavResourceLocator locator = this.getLocator();
        StringBuffer contentPathBuffer = new StringBuffer(this.getParentResourcePath());
        contentPathBuffer.append('/').append("@exports").append('/').append(this.pageTitle).append(".pdf");
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
    }

    private DavResourceLocator getPageExportsReadmeResourceLocator() {
        DavResourceLocator locator = this.getLocator();
        StringBuffer contentPathBuffer = new StringBuffer(this.getParentResourcePath());
        contentPathBuffer.append('/').append("@exports").append('/').append("README.txt");
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
    }

    @Override
    protected Collection<DavResource> getMemberResources() {
        try {
            ArrayList<DavResource> memberResources = new ArrayList<DavResource>();
            DavResourceFactory davResourceFactory = this.getFactory();
            memberResources.add(davResourceFactory.createResource(this.getPagePdfExportContentResourceLocator(), this.getSession()));
            memberResources.add(davResourceFactory.createResource(this.getPageWordExportContentResourceLocator(), this.getSession()));
            memberResources.add(davResourceFactory.createResource(this.getPageExportsReadmeResourceLocator(), this.getSession()));
            return memberResources;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

