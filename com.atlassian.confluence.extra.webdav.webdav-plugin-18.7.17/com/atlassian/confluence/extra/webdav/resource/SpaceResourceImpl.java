/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import com.atlassian.confluence.extra.webdav.resource.PageResourceImpl;
import com.atlassian.confluence.extra.webdav.util.ResourceHelper;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceResourceImpl
extends AbstractCollectionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpaceResourceImpl.class);
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final AttachmentManager attachmentManager;
    private final String spaceKey;
    private Space space;

    public SpaceResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, @ComponentImport AttachmentManager attachmentManager, String spaceKey) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.attachmentManager = attachmentManager;
        this.spaceKey = spaceKey;
    }

    private Space getSpace() {
        if (null == this.space) {
            this.space = this.spaceManager.getSpace(this.spaceKey);
        }
        return this.space;
    }

    @Override
    protected long getCreationtTime() {
        return this.getSpace().getCreationDate().getTime();
    }

    @Override
    public boolean exists() {
        return super.exists() && null != this.getSpace() && this.permissionManager.hasPermission(AuthenticatedUserThreadLocal.getUser(), Permission.VIEW, (Object)this.getSpace());
    }

    @Override
    public String getDisplayName() {
        return this.spaceKey;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private String getInputContentAsString(InputContext inputContext) throws IOException {
        if (inputContext.hasStream()) {
            StringWriter wikiMarkupBuffer = new StringWriter();
            InputStreamReader pageContentReader = new InputStreamReader((InputStream)new BufferedInputStream(inputContext.getInputStream()), "UTF-8");
            try {
                IOUtils.copy((Reader)pageContentReader, (Writer)wikiMarkupBuffer);
                String string = ((Object)wikiMarkupBuffer).toString();
                return string;
            }
            finally {
                IOUtils.closeQuietly((Reader)pageContentReader);
                IOUtils.closeQuietly((Writer)wikiMarkupBuffer);
            }
        }
        return "";
    }

    private boolean isResourceSpaceDescription(Space space, String resourceName) {
        return StringUtils.equals((String)resourceName, (String)(space.getName() + ".txt"));
    }

    private void createPage(String newPageTitle) throws DavException {
        Space thisSpace = this.getSpace();
        Page newPage = new Page();
        newPage.setSpace(thisSpace);
        if (PageResourceImpl.isPageTitleValid(newPageTitle)) {
            ConfluenceDavSession confluenceDavSession = (ConfluenceDavSession)this.getSession();
            if (this.isTextEditCreatingTempFolder(newPageTitle, confluenceDavSession)) {
                throw new DavException(403, "This plugin does not allow creation of page with the title \"(A Document Being Saved By TextEdit)\". See http://developer.atlassian.com/jira/browse/WBDV-143 for more information.");
            }
            newPage.setTitle(newPageTitle);
            if (null != this.pageManager.getPage(newPage.getSpaceKey(), newPage.getTitle())) {
                throw new DavException(400, "Page creation denied. Page " + newPageTitle + " is not unique in space " + newPage.getSpaceKey());
            }
        } else {
            throw new DavException(403, "Page creation denied. New page name has invalid characters in the title: " + newPageTitle);
        }
        newPage.setCreatorName(AuthenticatedUserThreadLocal.getUser().getName());
        this.pageManager.saveContentEntity((ContentEntityObject)newPage, null);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void addMember(DavResource davResource, InputContext inputContext) throws DavException {
        boolean isPermitted;
        User user = AuthenticatedUserThreadLocal.getUser();
        Space thisSpace = this.getSpace();
        Object[] davResourcePathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        String resourceName = davResourcePathComponents[davResourcePathComponents.length - 1];
        if (!inputContext.hasStream()) {
            if (!this.permissionManager.hasCreatePermission(user, (Object)thisSpace, Page.class)) throw new DavException(403, "Permission denied for creating a top level page " + resourceName + " in " + thisSpace);
            this.createPage(resourceName);
            return;
        }
        SpaceDescription spaceDescription = thisSpace.getDescription();
        boolean isSpaceDescription = this.isResourceSpaceDescription(thisSpace, resourceName);
        boolean bl = isPermitted = isSpaceDescription ? this.permissionManager.hasPermission(user, Permission.EDIT, (Object)thisSpace) : this.permissionManager.hasCreatePermission(user, (Object)spaceDescription, Attachment.class);
        if (!isPermitted) {
            throw new DavException(403, "Permission denied for creating or updating attachment " + resourceName + " on " + (ContentEntityObject)spaceDescription);
        }
        try {
            if (this.isResourceSpaceDescription(thisSpace, resourceName)) {
                spaceDescription.setBodyAsString(this.getInputContentAsString(inputContext));
                this.spaceManager.saveSpace(thisSpace);
                ((ConfluenceDavSession)this.getSession()).getResourceStates().unhideSpaceDescription(thisSpace);
                return;
            } else {
                ResourceHelper.addOrUpdateAttachment(this.attachmentManager, (ContentEntityObject)spaceDescription, resourceName, inputContext);
            }
            return;
        }
        catch (Exception e) {
            String errorMessage = "Unable to add/update attachment " + StringUtils.join((Object[])davResourcePathComponents, (String)"/") + " on space " + thisSpace;
            LOGGER.error(errorMessage, (Throwable)e);
            throw new DavException(500, (Throwable)e);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void removeMember(DavResource davResource) throws DavException {
        String[] pathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        String resourceName = pathComponents[pathComponents.length - 1];
        Space thisSpace = this.getSpace();
        User user = AuthenticatedUserThreadLocal.getUser();
        if (this.isResourceSpaceDescription(thisSpace, resourceName)) {
            if (!this.permissionManager.hasPermission(user, Permission.EDIT, (Object)thisSpace)) throw new DavException(403, "No permission to edit " + thisSpace);
            ((ConfluenceDavSession)this.getSession()).getResourceStates().hideSpaceDescription(thisSpace);
            return;
        } else {
            Page pageToRemove = this.pageManager.getPage(thisSpace.getKey(), resourceName);
            if (null == pageToRemove) {
                SpaceDescription spaceDesc = this.getSpace().getDescription();
                Attachment attachmentToRemove = this.attachmentManager.getAttachment((ContentEntityObject)spaceDesc, resourceName);
                if (null == attachmentToRemove) return;
                this.attachmentManager.removeAttachmentFromServer(attachmentToRemove);
                return;
            } else {
                if (!this.permissionManager.hasPermission(AuthenticatedUserThreadLocal.getUser(), Permission.REMOVE, (Object)pageToRemove)) throw new DavException(403, "Forbidden to delete " + pageToRemove);
                this.pageManager.trashPage((AbstractPage)pageToRemove);
            }
        }
    }

    @Override
    protected Collection<DavResource> getMemberResources() {
        try {
            ArrayList<DavResource> memberResources = new ArrayList<DavResource>();
            memberResources.add(this.getSpaceContentResource());
            memberResources.add(this.getBlogPostsResource());
            memberResources.addAll(this.getPageResources());
            memberResources.addAll(this.getSpaceAttachmentResources());
            return memberResources;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }

    protected DavResource getSpaceContentResource() throws DavException {
        return this.getFactory().createResource(this.getSpaceContentResourceLocator(), this.getSession());
    }

    private DavResourceLocator getSpaceContentResourceLocator() {
        DavResourceLocator locator = this.getLocator();
        Space space = this.getSpace();
        StringBuffer contentPathBuffer = new StringBuffer(this.getParentResourcePath());
        contentPathBuffer.append('/').append(space.getKey()).append('/').append(space.getName()).append(".txt");
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
    }

    protected DavResource getBlogPostsResource() throws DavException {
        return this.getFactory().createResource(this.getBlogPostsResourceLocator(), this.getSession());
    }

    private DavResourceLocator getBlogPostsResourceLocator() {
        DavResourceLocator locator = this.getLocator();
        StringBuffer contentPathBuffer = new StringBuffer(this.getParentResourcePath());
        contentPathBuffer.append('/').append(this.spaceKey).append('/').append("@news");
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
    }

    protected List<DavResource> getPageResources() throws DavException {
        DavResourceLocator[] pageResourceLocators = this.getPageResourceLocators();
        ArrayList<DavResource> pageResources = new ArrayList<DavResource>(pageResourceLocators.length);
        for (DavResourceLocator pageResourceLocator : pageResourceLocators) {
            pageResources.add(this.getFactory().createResource(pageResourceLocator, this.getSession()));
        }
        return pageResources;
    }

    private DavResourceLocator[] getPageResourceLocators() {
        DavResourceLocator locator = this.getLocator();
        Space space = this.getSpace();
        List topLevelPages = this.permissionManager.getPermittedEntities(AuthenticatedUserThreadLocal.getUser(), Permission.VIEW, this.pageManager.getTopLevelPages(space));
        StringBuffer contentPathBuffer = new StringBuffer();
        ArrayList<DavResourceLocator> pageResourceLocators = new ArrayList<DavResourceLocator>();
        String parentResourcePath = this.getParentResourcePath();
        for (Page topLevelPage : topLevelPages) {
            contentPathBuffer.setLength(0);
            contentPathBuffer.append(parentResourcePath).append('/').append(this.spaceKey).append('/').append(topLevelPage.getTitle());
            DavResourceLocator pageResourceLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
            pageResourceLocators.add(pageResourceLocator);
        }
        return pageResourceLocators.toArray(new DavResourceLocator[pageResourceLocators.size()]);
    }

    protected List<DavResource> getSpaceAttachmentResources() throws DavException {
        DavResourceLocator[] spaceAttachmentResourceLocators = this.getSpaceAttachmentResourceImpl();
        ArrayList<DavResource> spaceAttachmentResources = new ArrayList<DavResource>(spaceAttachmentResourceLocators.length);
        for (DavResourceLocator spaceAttachmentResourceLocator : spaceAttachmentResourceLocators) {
            spaceAttachmentResources.add(this.getFactory().createResource(spaceAttachmentResourceLocator, this.getSession()));
        }
        return spaceAttachmentResources;
    }

    private DavResourceLocator[] getSpaceAttachmentResourceImpl() {
        DavResourceLocator locator = this.getLocator();
        Space thisSpace = this.getSpace();
        SpaceDescription spaceDesc = thisSpace.getDescription();
        List attachments = this.attachmentManager.getLatestVersionsOfAttachments((ContentEntityObject)spaceDesc);
        StringBuffer contentPathBuffer = new StringBuffer();
        ArrayList<DavResourceLocator> resourceLocators = new ArrayList<DavResourceLocator>();
        String parentResourcePath = this.getParentResourcePath();
        for (Attachment attachment : attachments) {
            contentPathBuffer.setLength(0);
            contentPathBuffer.append(parentResourcePath).append('/').append(this.spaceKey).append('/').append(attachment.getFileName());
            DavResourceLocator resourceLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
            resourceLocators.add(resourceLocator);
        }
        return resourceLocators.toArray(new DavResourceLocator[resourceLocators.size()]);
    }
}

