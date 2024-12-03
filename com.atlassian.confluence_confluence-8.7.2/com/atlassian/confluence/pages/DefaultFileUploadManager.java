/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.core.AttachmentResource;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.FileUploadManager;
import com.atlassian.confluence.pages.SavableAttachment;
import com.atlassian.confluence.util.AttachmentMimeTypeTranslator;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFileUploadManager
implements FileUploadManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultFileUploadManager.class);
    public static final String OCTET_STREAM_MIME_TYPE = "application/octet-stream";
    private AttachmentManager attachmentManager;
    private AttachmentMimeTypeTranslator mimeTypeTranslator;

    @Override
    public void storeResource(AttachmentResource resource, ContentEntityObject ceo) {
        this.storeResources(Collections.singletonList(resource), ceo);
    }

    @Override
    public void storeResources(Collection<AttachmentResource> attachmentResources, ContentEntityObject content) {
        ArrayList<SavableAttachment> saveableAttachments = new ArrayList<SavableAttachment>();
        for (AttachmentResource attachmentResource : attachmentResources) {
            InputStream resourceStream;
            String filename = attachmentResource.getFilename();
            Attachment attachment = this.attachmentManager.getAttachment(content, filename);
            Attachment previousVersion = null;
            if (attachment == null) {
                attachment = new Attachment();
            } else {
                previousVersion = (Attachment)attachment.clone();
            }
            attachment.setMediaType(this.mimeTypeTranslator.resolveMimeType(filename, attachmentResource.getContentType()));
            if (previousVersion != null) {
                attachment.setFileName(previousVersion.getFileName());
            } else {
                attachment.setFileName(Objects.requireNonNull(filename));
            }
            attachment.setVersionComment(attachmentResource.getComment());
            attachment.setFileSize(attachmentResource.getContentLength());
            attachment.setMinorEdit(attachmentResource.isMinorEdit());
            attachment.setHidden(attachmentResource.isHidden());
            content.addAttachment(attachment);
            try {
                resourceStream = attachmentResource.getInputStream();
            }
            catch (IOException e) {
                throw new RuntimeException("Error opening input stream from resource: " + attachmentResource, e);
            }
            saveableAttachments.add(new SavableAttachment(attachment, previousVersion, resourceStream));
        }
        try {
            this.attachmentManager.saveAttachments(saveableAttachments);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            for (SavableAttachment attachment : saveableAttachments) {
                InputStream input = attachment.getAttachmentData();
                try {
                    if (input == null) continue;
                    input.close();
                }
                catch (IOException ioe) {
                    log.warn("Failed to close data stream while saving {}", (Object)attachment.getAttachment());
                }
            }
        }
    }

    @Override
    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setMimeTypeTranslator(AttachmentMimeTypeTranslator mimeTypeTranslator) {
        this.mimeTypeTranslator = mimeTypeTranslator;
    }
}

