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
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.ArrayUtils
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.ResourceStates;
import com.atlassian.confluence.extra.webdav.job.ContentJobQueue;
import com.atlassian.confluence.extra.webdav.job.impl.AttachmentRemovalJob;
import com.atlassian.confluence.extra.webdav.resource.AbstractAttachmentResource;
import com.atlassian.confluence.extra.webdav.resource.AbstractCollectionResource;
import com.atlassian.confluence.extra.webdav.util.ResourceHelper;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
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
import java.util.Date;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.DavSession;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageResourceImpl
extends AbstractCollectionResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageResourceImpl.class);
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final AttachmentManager attachmentManager;
    private final ContentJobQueue contentJobQueue;
    private final String spaceKey;
    private final String pageTitle;
    private Page page;

    public PageResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport PermissionManager permissionManager, @ComponentImport SpaceManager spaceManager, @ComponentImport PageManager pageManager, @ComponentImport AttachmentManager attachmentManager, ContentJobQueue contentJobQueue, String spaceKey, String pageTitle) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.attachmentManager = attachmentManager;
        this.contentJobQueue = contentJobQueue;
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
    }

    public static boolean isPageTitleValid(String pageTitle) {
        return AbstractPage.isValidPageTitle((String)pageTitle);
    }

    public Page getPage() {
        if (null == this.page) {
            this.page = this.pageManager.getPage(this.spaceKey, this.pageTitle);
        }
        return this.page;
    }

    @Override
    protected long getCreationtTime() {
        return this.getPage().getCreationDate().getTime();
    }

    @Override
    public boolean exists() {
        Page thisPage = this.getPage();
        LOGGER.debug("String describing page \"" + this.pageTitle + "\" in space " + this.spaceKey + ": " + this.page);
        if (null != thisPage) {
            User currentUser = AuthenticatedUserThreadLocal.getUser();
            boolean hasViewPermissionOnpage = this.permissionManager.hasPermission(currentUser, Permission.VIEW, (Object)thisPage);
            boolean isHidden = ((ConfluenceDavSession)this.getSession()).getResourceStates().isContentHidden((ContentEntityObject)thisPage);
            LOGGER.debug("Current user: " + currentUser.getName());
            LOGGER.debug("Does current user have view privilege on page? " + hasViewPermissionOnpage);
            LOGGER.debug("Does the WebDAV plugin consider the page hiddden: " + isHidden);
            return hasViewPermissionOnpage && !isHidden;
        }
        return false;
    }

    @Override
    public String getDisplayName() {
        return this.pageTitle;
    }

    private void createPage(String newPageTitle) throws DavException {
        Page thisPage = this.getPage();
        Page newPage = new Page();
        newPage.setSpace(thisPage.getSpace());
        if (PageResourceImpl.isPageTitleValid(newPageTitle)) {
            ConfluenceDavSession confluenceDavSession = (ConfluenceDavSession)this.getSession();
            if (this.isTextEditCreatingTempFolder(newPageTitle, confluenceDavSession)) {
                throw new DavException(403, "This plugin does not allow creation of page with the title \"(A Document Being Saved By TextEdit)\". See http://developer.atlassian.com/jira/browse/WBDV-143 for more information.");
            }
            newPage.setTitle(newPageTitle);
            if (null != this.pageManager.getPage(newPage.getSpaceKey(), newPage.getTitle())) {
                throw new DavException(403, "Page creation denied. Page " + newPageTitle + " is not unique in space " + newPage.getSpaceKey());
            }
        } else {
            throw new DavException(403, "Page creation denied. New page name has invalid characters in the title: " + newPageTitle);
        }
        newPage.setParentPage(thisPage);
        newPage.setCreatorName(AuthenticatedUserThreadLocal.getUser().getName());
        thisPage.addChild(newPage);
        this.pageManager.saveContentEntity((ContentEntityObject)newPage, null);
    }

    private boolean isPageContentAttachment(Page aPage, String resourceName) {
        return StringUtils.equals((String)resourceName, (String)(aPage.getTitle() + ".txt"));
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

    private void updatePageContent(Page pageToUpdate, InputContext inputContext) throws IOException, CloneNotSupportedException {
        String xhtml = this.getInputContentAsString(inputContext);
        ContentEntityObject previousVersionOfPage = (ContentEntityObject)pageToUpdate.clone();
        User user = AuthenticatedUserThreadLocal.getUser();
        pageToUpdate.setBodyAsString(xhtml);
        pageToUpdate.setLastModificationDate(new Date());
        pageToUpdate.setLastModifierName(user.getName());
        this.pageManager.saveContentEntity((ContentEntityObject)pageToUpdate, previousVersionOfPage, null);
    }

    private void cancelPendingAttachmentRemovalJobs(Page thisPage, String resourceName) {
        AttachmentRemovalJob attachmentRemovalJob = new AttachmentRemovalJob(this.pageManager, this.attachmentManager, thisPage.getId(), resourceName);
        this.contentJobQueue.remove(attachmentRemovalJob);
    }

    private boolean isAttachmentRepresentingTempVersionOfPageContent(String resourceName) {
        return StringUtils.equals((String)("._" + this.getPage().getTitle() + ".txt"), (String)resourceName);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void addMember(DavResource davResource, InputContext inputContext) throws DavException {
        boolean isPermitted;
        User user = AuthenticatedUserThreadLocal.getUser();
        Page thisPage = this.getPage();
        Object[] davResourcePathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        String resourceName = davResourcePathComponents[davResourcePathComponents.length - 1];
        if (!inputContext.hasStream()) {
            if (!this.permissionManager.hasCreatePermission(user, (Object)thisPage.getSpace(), Page.class)) throw new DavException(403, "Permission denied for creating a child page " + resourceName + " on " + thisPage);
            ResourceStates resourceStates = ((ConfluenceDavSession)this.getSession()).getResourceStates();
            if (StringUtils.equals((String)"@exports", (String)resourceName)) {
                resourceStates.unhideContentExports((ContentEntityObject)thisPage);
                return;
            } else if (StringUtils.equals((String)"@versions", (String)resourceName)) {
                resourceStates.unhideContentVersions((ContentEntityObject)thisPage);
                return;
            } else {
                this.createPage(resourceName);
            }
            return;
        }
        boolean isPageContentAttachment = this.isPageContentAttachment(thisPage, resourceName);
        boolean bl = isPermitted = isPageContentAttachment ? this.permissionManager.hasPermission(user, Permission.EDIT, (Object)thisPage) : this.permissionManager.hasCreatePermission(user, (Object)thisPage, Attachment.class);
        if (!isPermitted) {
            throw new DavException(403, "Permission denied for creating or updating attachment " + resourceName + " on " + thisPage);
        }
        try {
            ResourceStates resourceStates = ((ConfluenceDavSession)this.getSession()).getResourceStates();
            if (isPageContentAttachment) {
                resourceStates.unhideContentMarkup((ContentEntityObject)thisPage);
                this.updatePageContent(thisPage, inputContext);
                return;
            } else if (StringUtils.equals((String)resourceName, (String)(thisPage.getTitle() + ".url"))) {
                resourceStates.unhideContentUrl((ContentEntityObject)thisPage);
                return;
            } else {
                if (StringUtils.equals((String)resourceName, (String)".DS_Store")) return;
                if (!AbstractAttachmentResource.isValidAttachmentName(resourceName) && !this.isAttachmentRepresentingTempVersionOfPageContent(resourceName)) throw new DavException(403, "Attachment creation denied. File name contains invalid characters ['&', '+', '?', '|', '=']: " + resourceName);
                this.cancelPendingAttachmentRemovalJobs(thisPage, resourceName);
                ResourceHelper.addOrUpdateAttachment(this.attachmentManager, (ContentEntityObject)thisPage, resourceName, inputContext);
                resourceStates.unhideAttachment(this.attachmentManager.getAttachment((ContentEntityObject)thisPage, resourceName));
                return;
            }
        }
        catch (DavException de) {
            throw de;
        }
        catch (Exception e) {
            String errorMessage = "Unable to add/update attachment " + StringUtils.join((Object[])davResourcePathComponents, (String)"/") + " on page " + thisPage;
            LOGGER.error(errorMessage, (Throwable)e);
            throw new DavException(500, (Throwable)e);
        }
    }

    @Override
    public void removeMember(DavResource davResource) throws DavException {
        String[] displayName = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        String resourceName = displayName[displayName.length - 1];
        Page thisPage = this.getPage();
        ResourceStates resourceStates = ((ConfluenceDavSession)this.getSession()).getResourceStates();
        if (StringUtils.equals((String)resourceName, (String)(thisPage.getTitle() + ".txt"))) {
            resourceStates.hideContentMarkup((ContentEntityObject)thisPage);
        } else if (StringUtils.equals((String)resourceName, (String)(thisPage.getTitle() + ".url"))) {
            resourceStates.hideContentUrl((ContentEntityObject)thisPage);
        } else if (StringUtils.equals((String)resourceName, (String)"@versions")) {
            resourceStates.hideContentVersions((ContentEntityObject)thisPage);
        } else if (StringUtils.equals((String)resourceName, (String)"@exports")) {
            resourceStates.hideContentExports((ContentEntityObject)thisPage);
        } else {
            ArrayList childPages = new ArrayList(thisPage.getChildren());
            ArrayList attachments = new ArrayList(thisPage.getLatestVersionsOfAttachments());
            User user = AuthenticatedUserThreadLocal.getUser();
            for (Page childPage : childPages) {
                if (!StringUtils.equals((String)resourceName, (String)childPage.getTitle())) continue;
                if (this.permissionManager.hasPermission(user, Permission.REMOVE, (Object)childPage)) {
                    this.pageManager.trashPage((AbstractPage)childPage);
                    resourceStates.hideContent((ContentEntityObject)childPage);
                    continue;
                }
                throw new DavException(403, "Permission denied to remove " + childPage);
            }
            for (Attachment attachment : attachments) {
                if (!StringUtils.equals((String)resourceName, (String)attachment.getFileName())) continue;
                if (this.permissionManager.hasPermission(user, Permission.REMOVE, (Object)attachment)) {
                    AttachmentRemovalJob attachmentRemovalJob = new AttachmentRemovalJob(this.pageManager, this.attachmentManager, attachment.getContainer().getId(), attachment.getFileName());
                    resourceStates.hideAttachment(attachment);
                    this.contentJobQueue.enque(attachmentRemovalJob);
                    continue;
                }
                throw new DavException(403, "Permission denied to remove " + attachment);
            }
        }
    }

    private boolean isDestinationPathValid(String[] destinationResourcePathComponents) {
        return destinationResourcePathComponents.length > 3 && !ArrayUtils.contains((Object[])destinationResourcePathComponents, (Object)"@news") && !ArrayUtils.contains((Object[])destinationResourcePathComponents, (Object)"@versions") && !ArrayUtils.contains((Object[])destinationResourcePathComponents, (Object)"@exports") && AbstractPage.isValidPageTitle((String)destinationResourcePathComponents[destinationResourcePathComponents.length - 1]);
    }

    @Override
    public void move(DavResource davResource) throws DavException {
        Page thisPage = this.getPage();
        Object[] destinationResourcePathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        User user = AuthenticatedUserThreadLocal.getUser();
        if (!this.isDestinationPathValid((String[])destinationResourcePathComponents)) {
            throw new DavException(403, "Cannot move " + this.getResourcePath() + " to " + StringUtils.join((Object[])destinationResourcePathComponents, (char)'/'));
        }
        String destinationSpaceKey = destinationResourcePathComponents[2];
        Object destinationPageTitle = destinationResourcePathComponents[destinationResourcePathComponents.length - 1];
        if (!this.permissionManager.hasPermission(user, Permission.EDIT, (Object)thisPage)) {
            throw new DavException(403, "Permission denied to move " + thisPage);
        }
        if (!StringUtils.equals((String)thisPage.getTitle(), (String)destinationPageTitle)) {
            if (null != this.pageManager.getPage(destinationSpaceKey, (String)destinationPageTitle)) {
                throw new DavException(403, StringUtils.join((Object[])destinationResourcePathComponents, (char)'/') + " points to an existing page.");
            }
            this.pageManager.renamePage((AbstractPage)thisPage, (String)destinationPageTitle);
        } else {
            Object destinationPageParentTitle = destinationResourcePathComponents.length > 4 ? destinationResourcePathComponents[destinationResourcePathComponents.length - 2] : null;
            Page destinationPageParent = null == destinationPageParentTitle ? null : this.pageManager.getPage(destinationSpaceKey, (String)destinationPageParentTitle);
            Space destinationSpace = this.spaceManager.getSpace(destinationSpaceKey);
            this.pageManager.movePageAsChild(thisPage, destinationPageParent);
        }
    }

    protected String generateUniquePageTitle(String spaceKey, String title) {
        Object newTitle = title;
        if (this.pageManager.getPage(spaceKey, (String)newTitle) != null) {
            newTitle = "Copy of " + title;
            while (this.pageManager.getPage(spaceKey, (String)newTitle) != null) {
                newTitle = "Copy of " + (String)newTitle;
            }
        }
        return newTitle;
    }

    @Override
    public void copy(DavResource davResource, boolean shallow) throws DavException {
        Page newPage;
        String[] displayName = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        User user = AuthenticatedUserThreadLocal.getUser();
        if (this.permissionManager.hasCreatePermission(AuthenticatedUserThreadLocal.getUser(), (Object)this.spaceManager.getSpace(this.spaceKey), Page.class)) {
            Space targetSpace = this.spaceManager.getSpace(displayName[2]);
            Page parentPage = displayName.length <= 4 ? null : this.pageManager.getPage(targetSpace.getKey(), displayName[displayName.length - 2]);
            String uniqueName = displayName[displayName.length - 1];
            if (this.getPage().getTitle().equals(displayName[displayName.length - 1])) {
                uniqueName = this.generateUniquePageTitle(this.getPage().getSpaceKey(), this.getPage().getTitle());
            }
            newPage = new Page();
            newPage.setTitle(uniqueName);
            newPage.setSpace(targetSpace);
            newPage.setBodyContent(this.getPage().getBodyContent());
            newPage.setParentPage(parentPage);
            newPage.setCreatorName(user.getName());
            for (Attachment attachment : this.getPage().getAttachments()) {
                Attachment newAttachment = new Attachment();
                newPage.addAttachment(newAttachment);
                newAttachment.setFileName(attachment.getFileName());
                newAttachment.setContentType(attachment.getContentType());
                newAttachment.setFileSize(attachment.getFileSize());
                newAttachment.setCreatorName(user.getName());
                try {
                    InputStream in = this.attachmentManager.getAttachmentData(attachment);
                    this.attachmentManager.saveAttachment(newAttachment, null, in);
                    in.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (parentPage != null) {
                parentPage.addChild(newPage);
            }
            if (this.getPage().hasChildren()) {
                List childPages = this.getPage().getChildren();
                this.addChild(childPages, newPage);
            }
        } else {
            throw new DavException(403, "Forbidden to copy " + this.getHref());
        }
        this.pageManager.saveContentEntity((ContentEntityObject)newPage, null);
    }

    protected void addChild(List<Page> childPages, Page parent) {
        User user = AuthenticatedUserThreadLocal.getUser();
        for (Page childPage : childPages) {
            Page newChildPage = new Page();
            newChildPage.setTitle(this.generateUniquePageTitle(childPage.getSpaceKey(), childPage.getTitle()));
            newChildPage.setSpace(childPage.getSpace());
            newChildPage.setBodyContent(childPage.getBodyContent());
            newChildPage.setParentPage(parent);
            newChildPage.setCreatorName(user.getName());
            for (Attachment attachment : childPage.getAttachments()) {
                Attachment newAttachment = new Attachment();
                newChildPage.addAttachment(newAttachment);
                newAttachment.setFileName(attachment.getFileName());
                newAttachment.setContentType(attachment.getContentType());
                newAttachment.setFileSize(attachment.getFileSize());
                newAttachment.setCreatorName(AuthenticatedUserThreadLocal.getUser().getName());
                try {
                    InputStream in = attachment.getContentsAsStream();
                    this.attachmentManager.saveAttachment(newAttachment, null, in);
                    in.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (parent != null) {
                parent.addChild(newChildPage);
            }
            if (childPage.hasChildren()) {
                List childPageChildren = childPage.getChildren();
                this.addChild(childPageChildren, newChildPage);
            }
            this.pageManager.saveContentEntity((ContentEntityObject)newChildPage, null);
        }
    }

    private DavResourceLocator getPageContentResourceLocator() {
        StringBuffer contentPathBuffer = new StringBuffer(this.getParentResourcePath());
        DavResourceLocator locator = this.getLocator();
        contentPathBuffer.append('/').append(this.pageTitle).append('/').append(this.pageTitle).append(".txt");
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
    }

    protected DavResource getPageContentResource() throws DavException {
        return this.getFactory().createResource(this.getPageContentResourceLocator(), this.getSession());
    }

    private DavResourceLocator getPageUrlResourceLocator() {
        StringBuffer contentPathBuffer = new StringBuffer(this.getParentResourcePath());
        DavResourceLocator locator = this.getLocator();
        contentPathBuffer.append('/').append(this.pageTitle).append('/').append(this.pageTitle).append(".url");
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
    }

    protected DavResource getPageUrlResource() throws DavException {
        return this.getFactory().createResource(this.getPageUrlResourceLocator(), this.getSession());
    }

    private DavResourceLocator[] getPageAttachmentResourceLocators() {
        DavResourceLocator locator = this.getLocator();
        List attachments = this.attachmentManager.getLatestVersionsOfAttachments((ContentEntityObject)this.getPage());
        StringBuffer contentPathBuffer = new StringBuffer();
        ArrayList<DavResourceLocator> pageAttachmentResourceLocators = new ArrayList<DavResourceLocator>();
        String parentResourcePath = this.getParentResourcePath();
        for (Attachment attachment : attachments) {
            contentPathBuffer.setLength(0);
            contentPathBuffer.append(parentResourcePath).append('/').append(this.pageTitle).append('/').append(attachment.getFileName());
            DavResourceLocator pageAttachmentResourceLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
            pageAttachmentResourceLocators.add(pageAttachmentResourceLocator);
        }
        return pageAttachmentResourceLocators.toArray(new DavResourceLocator[pageAttachmentResourceLocators.size()]);
    }

    protected List<DavResource> getPageAttachmentResources() throws DavException {
        DavResourceLocator[] pageAttachmentResourceLocators = this.getPageAttachmentResourceLocators();
        ArrayList<DavResource> attachmentResources = new ArrayList<DavResource>(pageAttachmentResourceLocators.length);
        DavResourceFactory davResourceFactory = this.getFactory();
        DavSession session = this.getSession();
        for (DavResourceLocator pageAttachmentResourceLocator : pageAttachmentResourceLocators) {
            attachmentResources.add(davResourceFactory.createResource(pageAttachmentResourceLocator, session));
        }
        return attachmentResources;
    }

    private DavResourceLocator getPageExportResourceLocator() {
        StringBuffer contentPathBuffer = new StringBuffer(this.getParentResourcePath());
        DavResourceLocator locator = this.getLocator();
        contentPathBuffer.append('/').append(this.pageTitle).append('/').append("@exports");
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
    }

    protected DavResource getPageExportsResource() throws DavException {
        return this.getFactory().createResource(this.getPageExportResourceLocator(), this.getSession());
    }

    private DavResourceLocator getPageVersionResourceLocator() {
        StringBuffer contentPathBuffer = new StringBuffer(this.getParentResourcePath());
        DavResourceLocator locator = this.getLocator();
        contentPathBuffer.append('/').append(this.pageTitle).append('/').append("@versions");
        return locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
    }

    protected DavResource getPageVersionsResource() throws DavException {
        return this.getFactory().createResource(this.getPageVersionResourceLocator(), this.getSession());
    }

    private DavResourceLocator[] getChildPageResourceLocators() {
        DavResourceLocator locator = this.getLocator();
        Page page = this.getPage();
        List childrenPages = this.permissionManager.getPermittedEntities(AuthenticatedUserThreadLocal.getUser(), Permission.VIEW, page.getChildren());
        StringBuffer contentPathBuffer = new StringBuffer();
        ArrayList<DavResourceLocator> pageResourceLocators = new ArrayList<DavResourceLocator>();
        String parentResourcePath = this.getParentResourcePath();
        LOGGER.debug("Found " + (null == childrenPages ? 0 : childrenPages.size()) + " child pages of " + page + ".");
        for (Page childPage : childrenPages) {
            contentPathBuffer.setLength(0);
            contentPathBuffer.append(parentResourcePath).append('/').append(page.getTitle()).append('/').append(childPage.getTitle());
            LOGGER.debug("Found child page of \"" + page + "\", \"" + childPage + "\"");
            DavResourceLocator pageResourceLocator = locator.getFactory().createResourceLocator(locator.getPrefix(), locator.getWorkspacePath(), contentPathBuffer.toString(), false);
            LOGGER.debug("Created a " + pageResourceLocator.getClass() + " with the following details:\"\n\nPrefix: " + locator.getPrefix() + "\nWorkspace path: " + locator.getWorkspacePath() + "\nResource path: " + contentPathBuffer.toString());
            pageResourceLocators.add(pageResourceLocator);
        }
        return pageResourceLocators.toArray(new DavResourceLocator[pageResourceLocators.size()]);
    }

    protected List<DavResource> getChildPageResources() throws DavException {
        DavResourceLocator[] pageResourceLocators = this.getChildPageResourceLocators();
        ArrayList<DavResource> childPageResources = new ArrayList<DavResource>(pageResourceLocators.length);
        DavResourceFactory davResourceFactory = this.getFactory();
        DavSession session = this.getSession();
        for (DavResourceLocator pageResourceLocator : pageResourceLocators) {
            DavResource davResource = davResourceFactory.createResource(pageResourceLocator, session);
            LOGGER.debug("Does child page resource of \n" + this.getPage() + "\", \"" + davResource.getDisplayName() + "\" exist? " + davResource.exists());
            childPageResources.add(davResource);
        }
        return childPageResources;
    }

    @Override
    protected Collection<DavResource> getMemberResources() {
        try {
            ArrayList<DavResource> memberResources = new ArrayList<DavResource>();
            memberResources.add(this.getPageContentResource());
            memberResources.add(this.getPageUrlResource());
            memberResources.addAll(this.getPageAttachmentResources());
            memberResources.add(this.getPageExportsResource());
            memberResources.add(this.getPageVersionsResource());
            memberResources.addAll(this.getChildPageResources());
            return memberResources;
        }
        catch (DavException de) {
            throw new RuntimeException(de);
        }
    }
}

