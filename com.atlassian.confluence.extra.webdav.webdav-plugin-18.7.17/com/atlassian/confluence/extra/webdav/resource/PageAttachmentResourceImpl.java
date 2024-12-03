/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.base.Throwables
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.ArrayUtils
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractAttachmentResource;
import com.atlassian.confluence.extra.webdav.resource.PageContentResourceImpl;
import com.atlassian.confluence.extra.webdav.resource.SpaceContentResourceImpl;
import com.atlassian.confluence.extra.webdav.util.WebdavConstants;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.lock.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageAttachmentResourceImpl
extends AbstractAttachmentResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageAttachmentResourceImpl.class);
    private final PermissionManager permissionManager;
    private final PageManager pageManager;
    private final String spaceKey;
    private final String pageTitle;

    public PageAttachmentResourceImpl(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport PermissionManager permissionManager, @ComponentImport PageManager pageManager, @ComponentImport SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser, @ComponentImport AttachmentManager attachmentManager, String spaceKey, String pageTitle, String attachmentName, String userAgent) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession, attachmentSafeContentHeaderGuesser, attachmentManager, attachmentName, userAgent);
        this.permissionManager = permissionManager;
        this.pageManager = pageManager;
        this.spaceKey = spaceKey;
        this.pageTitle = pageTitle;
    }

    @Override
    public ContentEntityObject getContentEntityObject() {
        return this.pageManager.getPage(this.spaceKey, this.pageTitle);
    }

    private boolean isDestinationPathValid(String[] resourcePathComponents) {
        for (String resourcePathComponent : resourcePathComponents) {
            if (ArrayUtils.indexOf((Object[])WebdavConstants.SPECIAL_DIRECTORY_NAMES.toArray(), (Object)resourcePathComponent) < 0) continue;
            return false;
        }
        return resourcePathComponents.length > 4 && PageAttachmentResourceImpl.isValidAttachmentName(resourcePathComponents[resourcePathComponents.length - 1]);
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void copy(DavResource davResource, boolean shallow) throws DavException {
        Attachment attachment = this.getAttachment();
        Object[] destinationResourcePathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
        if (!this.isDestinationPathValid((String[])destinationResourcePathComponents)) {
            throw new DavException(403, "Cannot move " + this.getResourcePath() + " to " + StringUtils.join((Object[])destinationResourcePathComponents, (char)'/'));
        }
        String destinationSpaceKey = destinationResourcePathComponents[2];
        Object destinationPageName = destinationResourcePathComponents[destinationResourcePathComponents.length - 2];
        Object destinationAttachmentName = destinationResourcePathComponents[destinationResourcePathComponents.length - 1];
        Page destinationPage = this.pageManager.getPage(destinationSpaceKey, (String)destinationPageName);
        if (!this.permissionManager.hasCreatePermission(AuthenticatedUserThreadLocal.getUser(), (Object)destinationPage, Attachment.class)) return;
        InputStream attachmentInput = null;
        try {
            Attachment attachmentCopy = (Attachment)attachment.clone();
            attachmentCopy.setId(0L);
            attachmentCopy.setVersion(1);
            attachmentCopy.setContainer((ContentEntityObject)destinationPage);
            attachmentCopy.setFileName((String)destinationAttachmentName);
            destinationPage.addAttachment(attachmentCopy);
            attachmentInput = this.getAttachmentManager().getAttachmentData(attachment);
            this.getAttachmentManager().saveAttachment(attachmentCopy, null, attachmentInput);
        }
        catch (IOException e) {
            try {
                LOGGER.error("Error copying " + attachment + " to " + StringUtils.join((Object[])destinationResourcePathComponents, (char)'/'));
                throw new DavException(500, (Throwable)e);
                catch (Exception e2) {
                    throw Throwables.propagate((Throwable)e2);
                }
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(attachmentInput);
                throw throwable;
            }
        }
        IOUtils.closeQuietly((InputStream)attachmentInput);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void move(DavResource davResource) throws DavException {
        block6: {
            User user;
            Object[] destinationResourcePathComponents;
            Attachment attachment;
            block5: {
                attachment = this.getAttachment();
                destinationResourcePathComponents = StringUtils.split((String)davResource.getResourcePath(), (char)'/');
                user = AuthenticatedUserThreadLocal.getUser();
                if (!(davResource instanceof PageContentResourceImpl) && !(davResource instanceof SpaceContentResourceImpl) && !(davResource instanceof SpaceContentResourceImpl)) break block5;
                InputStream attachmentInput = null;
                try {
                    attachmentInput = this.getAttachmentManager().getAttachmentData(attachment);
                    davResource.getCollection().addMember(davResource, new PageContentInputContext(attachmentInput));
                }
                catch (Throwable throwable) {
                    IOUtils.closeQuietly(attachmentInput);
                    this.getAttachmentManager().removeAttachmentFromServer(attachment);
                    throw throwable;
                }
                IOUtils.closeQuietly((InputStream)attachmentInput);
                this.getAttachmentManager().removeAttachmentFromServer(attachment);
                break block6;
            }
            if (!this.isDestinationPathValid((String[])destinationResourcePathComponents)) {
                throw new DavException(403, "Cannot move " + this.getResourcePath() + " to " + StringUtils.join((Object[])destinationResourcePathComponents, (char)'/'));
            }
            String destinationSpaceKey = destinationResourcePathComponents[2];
            Object destinationPageName = destinationResourcePathComponents[destinationResourcePathComponents.length - 2];
            Object destinationAttachmentName = destinationResourcePathComponents[destinationResourcePathComponents.length - 1];
            Page destinationPage = this.pageManager.getPage(destinationSpaceKey, (String)destinationPageName);
            if (this.permissionManager.hasCreatePermission(user, (Object)destinationPage, Attachment.class) && this.permissionManager.hasPermission(user, Permission.REMOVE, (Object)attachment)) {
                this.getAttachmentManager().moveAttachment(attachment, (String)destinationAttachmentName, (ContentEntityObject)destinationPage);
            } else {
                throw new DavException(403, "No permission to create attachment in page " + StringUtils.join((Object[])destinationResourcePathComponents, (char)'/') + " or no permission to remove " + attachment);
            }
        }
    }

    private static class PageContentInputContext
    implements InputContext {
        private final InputStream in;
        private final long modificationTime;

        public PageContentInputContext(InputStream in) {
            this.in = in;
            this.modificationTime = System.currentTimeMillis();
        }

        @Override
        public boolean hasStream() {
            return true;
        }

        @Override
        public InputStream getInputStream() {
            return this.in;
        }

        @Override
        public long getModificationTime() {
            return this.modificationTime;
        }

        @Override
        public String getContentLanguage() {
            return null;
        }

        @Override
        public long getContentLength() {
            return -1L;
        }

        @Override
        public String getContentType() {
            return "text/plain";
        }

        @Override
        public String getProperty(String s) {
            return null;
        }
    }
}

