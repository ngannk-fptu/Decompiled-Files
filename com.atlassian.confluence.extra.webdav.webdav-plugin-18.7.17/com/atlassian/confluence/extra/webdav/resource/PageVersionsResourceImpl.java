/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.VersionHistorySummary
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.ResourceStates;
import com.atlassian.confluence.extra.webdav.WebdavSettingsManager;
import com.atlassian.confluence.extra.webdav.resource.AbstractVersionsResource;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.lock.LockManager;

public class PageVersionsResourceImpl
extends AbstractVersionsResource {
    private final String spaceKey;
    private final String pageTitle;

    public PageVersionsResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, WebdavSettingsManager webdavSettingsManager, @ComponentImport PageManager pageManager, String spaceKey, String pageTitle) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, webdavSettingsManager, pageManager);
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
    }

    @Override
    public ContentEntityObject getContentEntityObject() {
        return this.getPageManager().getPage(this.spaceKey, this.pageTitle);
    }

    @Override
    public void addMember(DavResource davResource, InputContext inputContext) throws DavException {
        String[] pathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        String resourceName = pathComponents[pathComponents.length - 1];
        ResourceStates resourceStates = ((ConfluenceDavSession)this.getSession()).getResourceStates();
        if (StringUtils.equals((String)"README.txt", (String)resourceName)) {
            resourceStates.unhideContentVersionsReadme(this.getContentEntityObject());
        } else {
            resourceStates.unhideContentVersionText(this.getContentEntityObject(), resourceName);
        }
    }

    @Override
    public void removeMember(DavResource davResource) throws DavException {
        String[] pathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        String resourceName = pathComponents[pathComponents.length - 1];
        ResourceStates resourceStates = ((ConfluenceDavSession)this.getSession()).getResourceStates();
        if (StringUtils.equals((String)"README.txt", (String)resourceName)) {
            resourceStates.hideContentVersionsReadme(this.getContentEntityObject());
        } else {
            resourceStates.hideContentVersionText(this.getContentEntityObject(), resourceName);
        }
    }

    @Override
    protected Collection<DavResource> getMemberResources() {
        try {
            ArrayList<DavResource> members = new ArrayList<DavResource>();
            DavResourceFactory davResourceFactory = this.getFactory();
            DavResourceLocator locator = this.getLocator();
            for (DavResourceLocator versionContentResourceLocator : this.getVersionContentResourceLocators()) {
                members.add(davResourceFactory.createResource(versionContentResourceLocator, this.getSession()));
            }
            members.add(davResourceFactory.createResource(locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), this.getParentResourcePath() + "/@versions/README.txt", false), this.getSession()));
            return members;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }

    private DavResourceLocator[] getVersionContentResourceLocators() {
        DavResourceLocator locator = this.getLocator();
        ContentEntityObject contentEntityObject = this.getContentEntityObject();
        List versionHistorySummaries = this.getPageManager().getVersionHistorySummaries(contentEntityObject);
        StringBuffer contentPathBuffer = new StringBuffer();
        ArrayList<DavResourceLocator> pageResourceLocators = new ArrayList<DavResourceLocator>();
        String parentResourcePath = this.getParentResourcePath();
        for (VersionHistorySummary versionHistorySummary : versionHistorySummaries) {
            contentPathBuffer.setLength(0);
            contentPathBuffer.append(parentResourcePath).append('/').append("@versions").append('/').append("Version ").append(versionHistorySummary.getVersion()).append(".txt");
            DavResourceLocator pageResourceLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
            pageResourceLocators.add(pageResourceLocator);
        }
        return pageResourceLocators.toArray(new DavResourceLocator[pageResourceLocators.size()]);
    }
}

