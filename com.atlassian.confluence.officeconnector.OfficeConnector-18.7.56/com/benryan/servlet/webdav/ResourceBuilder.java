/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.extra.webdav.ConfluenceDavSession
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.jackrabbit.webdav.DavException
 *  org.apache.jackrabbit.webdav.DavResource
 *  org.apache.jackrabbit.webdav.DavResourceFactory
 *  org.apache.jackrabbit.webdav.DavResourceLocator
 *  org.apache.jackrabbit.webdav.DavSession
 *  org.apache.jackrabbit.webdav.lock.LockManager
 */
package com.benryan.servlet.webdav;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.benryan.servlet.webdav.AttachmentResource;
import com.benryan.servlet.webdav.AttachmentsResource;
import com.benryan.servlet.webdav.ContentResource;
import com.benryan.servlet.webdav.EditInWordResourceFactory;
import com.benryan.servlet.webdav.PageResource;
import com.benryan.servlet.webdav.RootConfluenceResource;
import com.benryan.servlet.webdav.SpaceResource;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.lock.LockManager;

class ResourceBuilder {
    private final EditInWordResourceFactory editInWordResourceFactory;
    private final DavResourceLocator davResourceLocator;
    private final ConfluenceDavSession confluenceDavSession;
    private final LockManager lockManager;
    private final SpaceManager spaceManager;
    private final PermissionManager permissionManager;
    private final PageManager pageManager;
    private AbstractPage page;

    public static ResourceBuilder initializeBuilder(EditInWordResourceFactory davResourceFactory, DavResourceLocator locator, DavSession session) {
        return new ResourceBuilder(davResourceFactory, locator, session);
    }

    public ResourceBuilder(EditInWordResourceFactory davResourceFactory, DavResourceLocator davResourceLocator, DavSession davSession) {
        this.editInWordResourceFactory = davResourceFactory;
        this.davResourceLocator = davResourceLocator;
        this.confluenceDavSession = (ConfluenceDavSession)davSession;
        this.lockManager = this.confluenceDavSession.getLockManager();
        this.spaceManager = this.editInWordResourceFactory.getSpaceManager();
        this.permissionManager = this.editInWordResourceFactory.getPermissionManager();
        this.pageManager = this.editInWordResourceFactory.getPageManager();
    }

    public RootConfluenceResource buildRootResource() {
        return new RootConfluenceResource(this.davResourceLocator, this.editInWordResourceFactory, this.lockManager, this.confluenceDavSession);
    }

    public void pageId(String pageId) throws DavException {
        long id;
        try {
            id = Long.parseLong(pageId);
        }
        catch (NumberFormatException e) {
            throw new DavException(204, "Invalid resource identifier:" + pageId);
        }
        AbstractPage page = this.pageManager.getAbstractPage(id);
        if (page == null) {
            throw new DavException(404);
        }
        this.page = page;
    }

    public SpaceResource buildSpaceResource(String spaceKey) throws DavException {
        User user = AuthenticatedUserThreadLocal.getUser();
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new DavException(404);
        }
        if (!this.permissionManager.hasPermission(user, Permission.VIEW, (Object)space)) {
            throw new DavException(401, "You do not have permission to view the " + space.getName() + " space.");
        }
        return new SpaceResource(this.davResourceLocator, this.editInWordResourceFactory, this.lockManager, this.confluenceDavSession, space);
    }

    public AttachmentsResource buildAttachmentsResource() {
        return new AttachmentsResource(this, this.page);
    }

    public PageResource buildPageResource() throws DavException {
        return new PageResource(this, this.page);
    }

    public ContentResource buildContentResource() {
        return new ContentResource(this, this.page);
    }

    public DavResource buildAttachmentResource(@ComponentImport AttachmentManager attachmentManager, @ComponentImport SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser, String attachmentName) throws DavException {
        AttachmentResource resource = new AttachmentResource(this, this.permissionManager, attachmentManager, attachmentSafeContentHeaderGuesser, (ContentEntityObject)this.page, attachmentName);
        resource.checkEditPermission();
        return resource;
    }

    DavResourceLocator getDavResourceLocator() {
        return this.davResourceLocator;
    }

    DavResourceFactory getDavResourceFactory() {
        return this.editInWordResourceFactory;
    }

    LockManager getLockManager() {
        return this.lockManager;
    }

    ConfluenceDavSession getDavSession() {
        return this.confluenceDavSession;
    }

    FileUploadManager getFileUploadManager() {
        return this.editInWordResourceFactory.getFileUploadManager();
    }
}

