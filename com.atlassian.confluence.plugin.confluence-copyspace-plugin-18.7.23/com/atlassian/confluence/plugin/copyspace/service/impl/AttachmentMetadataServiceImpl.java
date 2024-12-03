/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceEntityObject
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugin.copyspace.service.AttachmentMetadataService;
import com.atlassian.confluence.plugin.copyspace.util.Constants;
import com.atlassian.confluence.plugin.copyspace.util.MetadataCopier;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="attachmentMetadataServiceImpl")
public class AttachmentMetadataServiceImpl
implements AttachmentMetadataService {
    private static final Logger log = LoggerFactory.getLogger(AttachmentMetadataServiceImpl.class);
    private final AttachmentManager attachmentManager;

    @Autowired
    public AttachmentMetadataServiceImpl(@ComponentImport AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    @Override
    public void preserveMetadata(ContentEntityObject originalContent, ContentEntityObject targetContent) {
        this.preserveMetadata(originalContent.getAttachments(), targetContent);
    }

    @Override
    public void preserveMetadata(List<Attachment> originalAttachments, ContentEntityObject targetContent) {
        for (Attachment originalAttachment : originalAttachments) {
            Attachment targetAttachment = targetContent.getAttachmentNamed(originalAttachment.getFileName());
            if (targetAttachment == null) continue;
            MetadataCopier.copyEntityMetadata((ConfluenceEntityObject)originalAttachment, (ConfluenceEntityObject)targetAttachment);
            AuthenticatedUserThreadLocal.asUser((ConfluenceUser)originalAttachment.getCreator(), () -> {
                try (InputStream attachmentData = this.attachmentManager.getAttachmentData(targetAttachment);){
                    this.attachmentManager.saveAttachment(targetAttachment, null, attachmentData, Constants.SUPPRESS_EVENT_KEEP_LAST_MODIFIER);
                }
                catch (IOException e) {
                    log.warn("Unable to update metadata for attachment {}", (Object)targetAttachment.getFileName());
                }
            });
        }
    }
}

