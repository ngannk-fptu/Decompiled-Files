/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent
 *  com.atlassian.confluence.event.events.content.attachment.AttachmentVersionRemoveEvent
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.event.api.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.conversion.listener;

import com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentVersionRemoveEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.conversion.impl.FileSystemConversionState;
import com.atlassian.event.api.EventListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConversionCacheListener {
    private static final Logger log = LoggerFactory.getLogger(ConversionCacheListener.class);
    private final AttachmentManager attachmentManager;

    public ConversionCacheListener(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    @EventListener
    public void handleAttachmentDeletion(AttachmentRemoveEvent event) {
        for (Attachment attachment : event.getAttachments()) {
            List allVersions = this.attachmentManager.getAllVersions(attachment);
            this.clearAttachmentConversionsCache(allVersions);
        }
    }

    @EventListener
    public void handleAttachmentVersionDeletion(AttachmentVersionRemoveEvent event) {
        this.clearAttachmentConversionsCache(event.getAttachments());
    }

    private void clearAttachmentConversionsCache(List<Attachment> attachmentList) {
        for (Attachment attachment : attachmentList) {
            for (ConversionType conversionType : ConversionType.values()) {
                File[] conversionFiles;
                log.debug("Removing conversion cache files for {} of type {}", (Object)attachment, (Object)conversionType);
                File conversionTypeStorageFolder = FileSystemConversionState.getStorageFolder(attachment, conversionType);
                FileFilter fileFilter = FileSystemConversionState.getStatusFileFilter(attachment);
                for (File conversionFile : conversionFiles = conversionTypeStorageFolder.listFiles(fileFilter)) {
                    boolean deleted = conversionFile.delete();
                    log.debug("Deleting file {}. Result: {}", (Object)conversionFile, (Object)deleted);
                }
                if (conversionTypeStorageFolder.list().length != 0) continue;
                boolean deleted = conversionTypeStorageFolder.delete();
                log.debug("Deleting empty parent directory {}. Result: {}", (Object)conversionTypeStorageFolder, (Object)deleted);
            }
        }
    }

    @EventListener
    public void handleAttachmentUpdateOnDraftSave(AttachmentUpdateEvent event) {
        if (!event.getOld().getContainer().isDraft()) {
            return;
        }
        for (ConversionType conversionType : ConversionType.values()) {
            FileFilter oldFileFilter;
            Attachment oldAttachment = event.getOld();
            File oldStorageFolder = FileSystemConversionState.getStorageFolder(oldAttachment, conversionType);
            File[] convertedFiles = oldStorageFolder.listFiles(oldFileFilter = FileSystemConversionState.getStatusFileFilter(oldAttachment));
            if (convertedFiles.length <= 0) continue;
            File newStorageFolder = FileSystemConversionState.getStorageFolder(event.getNew(), conversionType);
            FileSystemConversionState newAttachmentState = new FileSystemConversionState(event.getNew(), conversionType);
            if (newAttachmentState.isConverted()) {
                log.debug("File has already been converted in {}", (Object)newStorageFolder.getAbsolutePath());
                continue;
            }
            for (File conversionFile : convertedFiles) {
                try {
                    if (log.isDebugEnabled()) {
                        log.debug("Moving {} to {}", (Object)conversionFile.toPath(), (Object)new File(newStorageFolder.getPath() + File.separator + conversionFile.getName()).toPath());
                    }
                    Files.move(conversionFile.toPath(), new File(newStorageFolder.getPath() + File.separator + conversionFile.getName()).toPath(), StandardCopyOption.ATOMIC_MOVE);
                }
                catch (Exception e) {
                    log.error("Cannot move conversion file {} to {}.", new Object[]{conversionFile.getName(), newStorageFolder.getAbsolutePath(), e});
                }
            }
            if (oldStorageFolder.list().length != 0) continue;
            boolean deleted = oldStorageFolder.delete();
            log.debug("Deleting empty parent directory {}. Result: {}", (Object)oldStorageFolder, (Object)deleted);
            File parent = oldStorageFolder.getParentFile();
            File grandparent = parent.getParentFile();
            try {
                log.debug("Deleting empty parent directory {}.", (Object)parent);
                Files.delete(parent.toPath());
                log.debug("Deleting empty grandparent directory {}.", (Object)grandparent);
                Files.delete(grandparent.toPath());
            }
            catch (DirectoryNotEmptyException e) {
                log.debug("Parent directories are not empty: {}", (Object)e.getFile());
            }
            catch (IOException e) {
                log.warn("Error deleting parent directories", (Throwable)e);
            }
        }
    }
}

