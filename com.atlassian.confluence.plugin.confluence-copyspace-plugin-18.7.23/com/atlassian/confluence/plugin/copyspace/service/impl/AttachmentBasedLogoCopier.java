/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.Labelable
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.attachments.AttachmentDataStreamSizeMismatchException
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.atlassian.confluence.spaces.SpaceLogo
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.Labelable;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.attachments.AttachmentDataStreamSizeMismatchException;
import com.atlassian.confluence.plugin.copyspace.exception.CopySpaceException;
import com.atlassian.confluence.plugin.copyspace.service.AttachmentMetadataService;
import com.atlassian.confluence.plugin.copyspace.service.LabelService;
import com.atlassian.confluence.plugin.copyspace.service.LogoCopier;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceLogo;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value="attachmentBasedLogoCopier")
public class AttachmentBasedLogoCopier
implements LogoCopier {
    private final AttachmentManager attachmentManager;
    private final SpaceManager spaceManager;
    private final LabelService labelService;
    private final AttachmentMetadataService attachmentMetadataService;
    private static final Logger log = LoggerFactory.getLogger(AttachmentBasedLogoCopier.class);

    public AttachmentBasedLogoCopier(@ComponentImport AttachmentManager attachmentManager, @ComponentImport SpaceManager spaceManager, LabelService labelService, AttachmentMetadataService attachmentMetadataService) {
        this.attachmentManager = attachmentManager;
        this.spaceManager = spaceManager;
        this.labelService = labelService;
        this.attachmentMetadataService = attachmentMetadataService;
    }

    @Override
    public void copyLogo(Space source, Space destination) {
        SpaceLogo logoForSpace = this.spaceManager.getLogoForSpace(source.getKey());
        if (logoForSpace.isCustomLogo()) {
            SpaceDescription originalDescription = source.getDescription();
            SpaceDescription newDescription = destination.getDescription();
            Attachment sourceLogoAsAttachment = this.obtainLogoAsAttachment(originalDescription);
            this.copyAttachment(sourceLogoAsAttachment, newDescription);
            Attachment targetLogoAttachment = this.attachmentManager.getAttachment((ContentEntityObject)newDescription, source.getKey());
            if (targetLogoAttachment != null) {
                this.attachmentManager.moveAttachment(targetLogoAttachment, destination.getKey(), (ContentEntityObject)newDescription);
            }
        }
    }

    private Attachment obtainLogoAsAttachment(SpaceDescription spaceDescription) {
        List latestVersionsOfAttachments = this.attachmentManager.getLatestVersionsOfAttachments((ContentEntityObject)spaceDescription);
        Optional<Attachment> logoAsAttachment = latestVersionsOfAttachments.stream().filter(attachment -> attachment.getMediaType().startsWith("image")).findFirst();
        return logoAsAttachment.orElseThrow(() -> new CopySpaceException("Could not find the logo attachment!"));
    }

    private void copyAttachment(Attachment attachment, SpaceDescription to) {
        Attachment attachmentCopy = new Attachment();
        attachmentCopy.setFileName(attachment.getFileName());
        attachmentCopy.setFileSize(attachment.getFileSize());
        attachmentCopy.setMediaType(attachment.getMediaType());
        attachmentCopy.setVersionComment(attachment.getVersionComment());
        attachmentCopy.setVersion(1);
        attachmentCopy.setContainer((ContentEntityObject)to);
        try (InputStream data = this.attachmentManager.getAttachmentData(attachment);){
            this.attachmentManager.saveAttachment(attachmentCopy, null, data);
            for (Label label : attachment.getLabels()) {
                this.labelService.addLabel((Labelable)attachmentCopy, label);
            }
            this.attachmentMetadataService.preserveMetadata((ContentEntityObject)attachment, (ContentEntityObject)attachmentCopy);
        }
        catch (AttachmentDataStreamSizeMismatchException e) {
            log.error("The attachment's size property does not match the attachment's data size. Attachment: " + attachment, (Throwable)e);
            throw e;
        }
        catch (Exception e) {
            log.error("An error occurred while copying an attachment.  Destination: " + to + " attachment: " + attachment, (Throwable)e);
            throw new RuntimeException(e);
        }
    }
}

