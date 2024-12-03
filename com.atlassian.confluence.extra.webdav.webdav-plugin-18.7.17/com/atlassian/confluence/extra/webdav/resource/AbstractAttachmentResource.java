/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 */
package com.atlassian.confluence.extra.webdav.resource;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.webdav.ConfluenceDavSession;
import com.atlassian.confluence.extra.webdav.resource.AbstractContentResource;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.servlet.download.SafeContentHeaderGuesser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.lock.LockManager;

public abstract class AbstractAttachmentResource
extends AbstractContentResource {
    private final AttachmentManager attachmentManager;
    private final SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser;
    private final String attachmentName;
    private final String userAgent;
    private Attachment attachment;

    public AbstractAttachmentResource(DavResourceLocator davResourceLocator, DavResourceFactory davResourceFactory, LockManager lockManager, ConfluenceDavSession davSession, @ComponentImport SafeContentHeaderGuesser attachmentSafeContentHeaderGuesser, @ComponentImport AttachmentManager attachmentManager, String attachmentName, String userAgent) {
        super(davResourceLocator, davResourceFactory, lockManager, davSession);
        this.attachmentSafeContentHeaderGuesser = attachmentSafeContentHeaderGuesser;
        this.attachmentManager = attachmentManager;
        this.attachmentName = attachmentName;
        this.userAgent = userAgent;
    }

    protected AttachmentManager getAttachmentManager() {
        return this.attachmentManager;
    }

    public static boolean isValidAttachmentName(String resourceName) {
        return !resourceName.contains("&") && !resourceName.contains("+") && !resourceName.contains("?") && !resourceName.contains("|") && !resourceName.contains("=");
    }

    public abstract ContentEntityObject getContentEntityObject();

    @Override
    public boolean exists() {
        return super.exists() && null != this.getAttachment() && !((ConfluenceDavSession)this.getSession()).getResourceStates().isAttachmentHidden(this.getAttachment());
    }

    public Attachment getAttachment() {
        if (null == this.attachment) {
            this.attachment = this.attachmentManager.getAttachment(this.getContentEntityObject(), this.attachmentName);
        }
        return this.attachment;
    }

    @Override
    public long getModificationTime() {
        return this.getAttachment().getLastModificationDate().getTime();
    }

    @Override
    public InputStream getContent() {
        return this.attachmentManager.getAttachmentData(this.getAttachment());
    }

    @Override
    protected long getContentLength() {
        return this.getAttachment().getFileSize();
    }

    @Override
    protected String getContentType() {
        Map<String, String> contentTypeMap = this.getHeaders();
        return contentTypeMap.get("Content-Type");
    }

    @Override
    protected Map<String, String> getHeaders() {
        try {
            return this.attachmentSafeContentHeaderGuesser.computeAttachmentHeaders(this.getAttachment().getMediaType(), (InputStream)new BufferedInputStream(this.attachmentManager.getAttachmentData(this.attachment)), this.getAttachment().getFileName(), this.userAgent, this.getContentLength(), false, Collections.emptyMap());
        }
        catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    protected long getCreationtTime() {
        return this.getAttachment().getCreationDate().getTime();
    }

    @Override
    public String getDisplayName() {
        return this.getAttachment().getFileName();
    }
}

