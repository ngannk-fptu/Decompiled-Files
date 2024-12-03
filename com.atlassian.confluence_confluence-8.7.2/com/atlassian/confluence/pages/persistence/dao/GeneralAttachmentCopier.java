/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.persistence.dao.AbstractAttachmentCopier;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralAttachmentCopier
extends AbstractAttachmentCopier
implements AttachmentDao.AttachmentCopier {
    private static final Logger log = LoggerFactory.getLogger(GeneralAttachmentCopier.class);
    protected AttachmentManager sourceAttachmentManager;
    protected AttachmentManager destinationAttachmentManager;

    public GeneralAttachmentCopier(AttachmentManager sourceAttachmentManager, AttachmentManager destinationAttachmentManager) {
        this.sourceAttachmentManager = sourceAttachmentManager;
        this.destinationAttachmentManager = destinationAttachmentManager;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void copy() {
        if (log.isInfoEnabled()) {
            log.info("Beginning copy from '" + this.sourceAttachmentManager + "' to '" + this.destinationAttachmentManager + "'");
        }
        if (this.progress != null) {
            this.progress.setStatus("Finding Attachments");
        }
        List<Attachment> sourceAttachments = this.sourceAttachmentManager.getAttachmentDao().findAll();
        int totalAttachments = sourceAttachments.size();
        int currentAttachmentIndex = 0;
        if (log.isInfoEnabled()) {
            log.info("Found " + totalAttachments + " to copy");
        }
        if (this.progress != null) {
            this.progress.setTotalObjects(totalAttachments);
            this.progress.setStatus("Copying attachments");
        }
        for (Attachment attachment : sourceAttachments) {
            if (attachment.getContainer() == null) {
                log.error("Attachment '" + attachment + "' does not have a content object. Skipping.");
                ++currentAttachmentIndex;
                continue;
            }
            if (!this.isContentSpaceIncluded(attachment.getContainer())) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping attachment '" + attachment + "' since its parent content '" + attachment.getContainer() + "' is not in the included Space list.");
                }
                ++currentAttachmentIndex;
                continue;
            }
            if (this.isContentExcluded(attachment.getContainer())) {
                if (log.isDebugEnabled()) {
                    log.debug("Skipping attachment '" + attachment + "' since its parent content '" + attachment.getContainer() + "' is in the exclusion list.");
                }
                ++currentAttachmentIndex;
                continue;
            }
            List<Attachment> attachmentVersions = this.sourceAttachmentManager.getAllVersions(attachment);
            attachmentVersions.remove(0);
            if (log.isDebugEnabled()) {
                log.debug("Copying attachment '" + attachment + "' and " + attachmentVersions.size() + " previous versions.");
            }
            attachmentVersions.add(attachment);
            Iterator<Attachment> versionIterator = attachmentVersions.iterator();
            Attachment previousAttachmentVersion = null;
            while (versionIterator.hasNext()) {
                Attachment newVersion;
                Attachment currentSourceAttachment = versionIterator.next();
                Attachment currentVersion = null;
                if (previousAttachmentVersion == null) {
                    newVersion = this.constructFirstAttachmentVersion(currentSourceAttachment);
                } else {
                    newVersion = previousAttachmentVersion;
                    currentVersion = (Attachment)newVersion.clone();
                    this.constructNewAttachmentVersion(newVersion, currentSourceAttachment);
                }
                try {
                    InputStream attachmentData = this.sourceAttachmentManager.getAttachmentData(newVersion);
                    try {
                        if (attachmentData == null) continue;
                        this.destinationAttachmentManager.saveAttachment(newVersion, currentVersion, attachmentData);
                    }
                    finally {
                        if (attachmentData == null) continue;
                        attachmentData.close();
                    }
                }
                catch (IOException e) {
                    log.error("There was a problem saving Attachment '" + newVersion + "' to a new AttachmentManager. Skipping");
                }
                finally {
                    previousAttachmentVersion = newVersion;
                }
            }
            if (this.progress == null) continue;
            this.progress.setCurrentCount(++currentAttachmentIndex);
            this.progress.setStatus("Copied " + currentAttachmentIndex + " out of " + totalAttachments + " attachments.");
        }
        log.info("Copy completed.");
        if (this.progress != null) {
            this.progress.setCurrentCount(totalAttachments);
            this.progress.setStatus("Completed - " + totalAttachments + " attachments transferred.");
        }
    }

    private void constructNewAttachmentVersion(Attachment newAttachment, Attachment currentAttachment) {
        newAttachment.setVersionComment(currentAttachment.getVersionComment());
        newAttachment.setMediaType(currentAttachment.getMediaType());
        newAttachment.setFileName(currentAttachment.getFileName());
        newAttachment.setFileSize(currentAttachment.getFileSize());
        newAttachment.setLastModificationDate(currentAttachment.getLastModificationDate());
        newAttachment.setLastModifier(currentAttachment.getLastModifier());
        ContentEntityObject ceo = currentAttachment.getContainer();
        if (ceo != null) {
            ceo.addAttachment(newAttachment);
        }
    }

    private Attachment constructFirstAttachmentVersion(Attachment attachment) {
        Attachment newVersion = (Attachment)attachment.clone();
        newVersion.setOriginalVersion(null);
        return newVersion;
    }
}

