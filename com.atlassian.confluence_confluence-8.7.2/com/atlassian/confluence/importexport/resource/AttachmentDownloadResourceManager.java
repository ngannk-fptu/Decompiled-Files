/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.importexport.resource.AttachmentDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourcePrefixEnum;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.PartialAttachmentDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.PartialDownloadResourceManager;
import com.atlassian.confluence.importexport.resource.PartialDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.download.AttachmentUrlParser;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.persistence.dao.ConfluenceUserDao;
import com.atlassian.confluence.web.rangerequest.RangeNotSatisfiableException;
import com.atlassian.confluence.web.rangerequest.RangeRequest;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.core.io.InputStreamSource;

public class AttachmentDownloadResourceManager
implements PartialDownloadResourceManager {
    private AttachmentUrlParser attachmentUrlParser;
    private AttachmentManager attachmentManager;
    private PermissionManager permissionManager;
    private ConfluenceUserDao confluenceUserDao;
    private ContextPathHolder contextPathHolder;

    public AttachmentDownloadResourceManager(PermissionManager permissionManager, AttachmentManager attachmentManager, ConfluenceUserDao confluenceUserDao, AttachmentUrlParser attachmentUrlParser, ContextPathHolder contextPathHolder) {
        this.setPermissionManager(permissionManager);
        this.setAttachmentManager(attachmentManager);
        this.setConfluenceUserDao(confluenceUserDao);
        this.setAttachmentUrlParser(attachmentUrlParser);
        this.setContextPathHolder(contextPathHolder);
    }

    @Override
    public boolean matches(String resourcePath) {
        return resourcePath.startsWith(this.getContextPathHolder().getContextPath() + DownloadResourcePrefixEnum.ATTACHMENT_DOWNLOAD_RESOURCE_PREFIX.getPrefix()) || resourcePath.startsWith(this.getContextPathHolder().getContextPath() + DownloadResourcePrefixEnum.TOKEN_AUTH_ATTACHMENT_DOWNLOAD_RESOURCE_PREFIX.getPrefix());
    }

    @Override
    public DownloadResourceReader getResourceReader(String userName, String resourcePath, Map parameters) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException {
        Attachment attachment = this.getAttachment(userName, resourcePath, parameters, "attachments");
        return new AttachmentDownloadResourceReader(attachment, new AttachmentInputStreamSource(attachment));
    }

    protected Attachment getAttachment(String userName, String resourcePath, Map parameters, String urlPrefix) throws DownloadResourceNotFoundException, UnauthorizedDownloadResourceException {
        ContentEntityObject entity = this.attachmentUrlParser.getEntity(resourcePath, urlPrefix);
        if (entity == null || entity.isDeleted()) {
            throw new DownloadResourceNotFoundException();
        }
        if (!this.hasUserPrivilegeForDownload(userName, entity)) {
            throw new UnauthorizedDownloadResourceException("User is unauthorised to download attachment");
        }
        Attachment attachment = this.attachmentUrlParser.getAttachment(resourcePath, urlPrefix, parameters);
        if (attachment == null || attachment.isDeleted()) {
            throw new DownloadResourceNotFoundException();
        }
        return attachment;
    }

    public void setAttachmentUrlParser(AttachmentUrlParser attachmentUrlParser) {
        this.attachmentUrlParser = attachmentUrlParser;
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setConfluenceUserDao(ConfluenceUserDao confluenceUserDao) {
        this.confluenceUserDao = confluenceUserDao;
    }

    public ContextPathHolder getContextPathHolder() {
        return this.contextPathHolder;
    }

    public void setContextPathHolder(ContextPathHolder contextPathHolder) {
        this.contextPathHolder = contextPathHolder;
    }

    @Override
    public PartialDownloadResourceReader getPartialResourceReader(String userName, String resourcePath, Map parameters, String requestRange) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException, RangeNotSatisfiableException {
        Attachment attachment = this.getAttachment(userName, resourcePath, parameters, "attachments");
        RangeRequest rangeRequest = RangeRequest.parse(Objects.requireNonNull(requestRange), attachment.getFileSize());
        return new PartialAttachmentDownloadResourceReader(attachment, new PartialAttachmentInputStreamSource(attachment, rangeRequest), rangeRequest);
    }

    private boolean hasUserPrivilegeForDownload(String userName, ContentEntityObject entity) {
        if (entity instanceof GlobalDescription) {
            return true;
        }
        ConfluenceUser user = this.confluenceUserDao.findByUsername(userName);
        return this.permissionManager.hasPermission((User)user, Permission.VIEW, entity);
    }

    private class PartialAttachmentInputStreamSource
    extends AttachmentInputStreamSource {
        private RangeRequest rangeRequest;

        public PartialAttachmentInputStreamSource(Attachment attachment, RangeRequest range) {
            super(attachment);
            this.rangeRequest = Objects.requireNonNull(range);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return AttachmentDownloadResourceManager.this.attachmentManager.getAttachmentData(this.attachment, Optional.of(this.rangeRequest));
        }
    }

    private class AttachmentInputStreamSource
    implements InputStreamSource {
        protected Attachment attachment;

        public AttachmentInputStreamSource(Attachment attachment) {
            this.attachment = attachment;
        }

        public InputStream getInputStream() throws IOException {
            return AttachmentDownloadResourceManager.this.attachmentManager.getAttachmentData(this.attachment);
        }
    }
}

