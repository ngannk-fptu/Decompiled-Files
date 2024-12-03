/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.AttachmentResource
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.InputStreamAttachmentResource
 *  com.atlassian.confluence.extra.webdav.resource.AbstractAttachmentResource
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.FileUploadManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.util.FileTypeUtil
 *  com.atlassian.user.User
 *  org.apache.jackrabbit.webdav.DavException
 *  org.apache.jackrabbit.webdav.io.InputContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.servlet.webdav;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.InputStreamAttachmentResource;
import com.atlassian.confluence.extra.webdav.resource.AbstractAttachmentResource;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.util.FileTypeUtil;
import com.atlassian.user.User;
import com.benryan.servlet.webdav.ResourceBuilder;
import java.io.InputStream;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AttachmentResource
extends AbstractAttachmentResource {
    private static final Logger log = LoggerFactory.getLogger(AttachmentResource.class);
    public static final String PATH_PREFIX = "attachments";
    private final FileUploadManager fileUploadManager;
    private final Attachment attachment;
    private final PermissionManager permissionManager;
    private final ContentEntityObject content;
    private final String attachmentName;

    AttachmentResource(ResourceBuilder builder, @ComponentImport PermissionManager permissionManager, @ComponentImport AttachmentManager attachmentManager, @ComponentImport SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser, ContentEntityObject content, String attachmentName) {
        super(builder.getDavResourceLocator(), builder.getDavResourceFactory(), builder.getLockManager(), builder.getDavSession(), attachmentSafeContentHeaderGuesser, attachmentManager, attachmentName, builder.getDavSession().getUserAgent());
        this.permissionManager = permissionManager;
        this.fileUploadManager = builder.getFileUploadManager();
        this.content = content;
        this.attachmentName = attachmentName;
        this.attachment = this.getAttachment();
    }

    public boolean exists() {
        return this.attachment != null;
    }

    public long getModificationTime() {
        return this.attachment.getLastModificationDate().getTime();
    }

    public InputStream getContent() {
        return this.getAttachmentManager().getAttachmentData(this.attachment);
    }

    protected long getContentLength() {
        return this.attachment.getFileSize();
    }

    protected String getContentType() {
        return this.attachment.getContentType();
    }

    protected long getCreationtTime() {
        return this.attachment.getCreationDate().getTime();
    }

    public String getDisplayName() {
        return this.attachment.getFileName();
    }

    public ContentEntityObject getContentEntityObject() {
        return this.content;
    }

    public void saveData(InputContext inputContext) throws DavException {
        this.checkEditPermission();
        try {
            String contentType = AttachmentResource.chooseContentType(inputContext, this.attachmentName);
            InputStreamAttachmentResource uploaderResource = new InputStreamAttachmentResource(inputContext.getInputStream(), this.attachmentName, contentType, inputContext.getContentLength(), this.attachment.getComment());
            this.fileUploadManager.storeResource((com.atlassian.confluence.core.AttachmentResource)uploaderResource, this.content);
        }
        catch (Exception e) {
            throw new DavException(500, (Throwable)e);
        }
    }

    private static String chooseContentType(InputContext inputContext, String attachmentName) {
        String contentTypeFromRequest = inputContext.getContentType();
        String contentTypeFromExtension = FileTypeUtil.getContentType((String)attachmentName);
        log.debug("Content Type from request: {}, Content Type from file type : {}", (Object)contentTypeFromRequest, (Object)contentTypeFromExtension);
        log.debug("Resulting type: {}", (Object)contentTypeFromExtension);
        return contentTypeFromExtension;
    }

    public void checkEditPermission() throws DavException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.EDIT, (Object)this.attachment)) {
            throw new DavException(401, "You do not have permission to edit the attachment " + this.attachment.getFileName());
        }
    }
}

