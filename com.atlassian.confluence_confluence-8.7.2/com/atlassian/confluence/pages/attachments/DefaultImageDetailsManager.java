/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ImageInfo
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventListener
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataIntegrityViolationException
 */
package com.atlassian.confluence.pages.attachments;

import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentCreateEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentUpdateEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.attachments.ImageDetails;
import com.atlassian.confluence.pages.attachments.ImageDetailsDao;
import com.atlassian.confluence.pages.attachments.ImageDetailsManager;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.core.util.ImageInfo;
import com.atlassian.event.Event;
import com.atlassian.event.EventListener;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

public final class DefaultImageDetailsManager
implements ImageDetailsManager,
EventListener {
    private static final Logger log = LoggerFactory.getLogger(DefaultImageDetailsManager.class);
    private AttachmentManager attachmentManager;
    private ImageDetailsDao imageDetailsDao;
    private ThumbnailManager thumbnailManager;

    @Override
    public ImageDetails getImageDetails(Attachment attachment) {
        if (!this.hasImageContentType(attachment)) {
            return null;
        }
        if (!attachment.isLatestVersion()) {
            return this.buildImageDetails(attachment);
        }
        ImageDetails storedDetails = this.imageDetailsDao.getImageDetails(attachment);
        if (storedDetails != null) {
            return storedDetails;
        }
        return this.createImageDetails(attachment);
    }

    private ImageDetails createImageDetails(Attachment attachment) {
        if (!this.hasImageContentType(attachment)) {
            return null;
        }
        ImageDetails result = this.buildImageDetails(attachment);
        if (result == null) {
            return null;
        }
        this.storeImageDetails(result);
        return result;
    }

    private void storeImageDetails(ImageDetails result) {
        try {
            this.imageDetailsDao.save(result);
        }
        catch (DataIntegrityViolationException e) {
            log.warn("Attempt to store image details failed", (Throwable)e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private ImageDetails buildImageDetails(Attachment attachment) {
        try (InputStream attachmentData = this.attachmentManager.getAttachmentData(attachment);){
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setInput(attachmentData);
            imageInfo.setDetermineImageNumber(true);
            imageInfo.setCollectComments(true);
            if (!imageInfo.check()) {
                ImageDetails imageDetails2 = null;
                return imageDetails2;
            }
            ImageDetails imageDetails = new ImageDetails(attachment, imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getMimeType());
            return imageDetails;
        }
        catch (IOException e) {
            log.warn("Failed to close data stream for {}", (Object)attachment);
            return null;
        }
        catch (Exception e) {
            log.warn("Failed to load attachment: {}", (Object)attachment);
            return null;
        }
    }

    public void handleEvent(Event event) {
        if (event instanceof GeneralAttachmentCreateEvent) {
            GeneralAttachmentCreateEvent attachmentCreateEvent = (GeneralAttachmentCreateEvent)event;
            for (Attachment attachment : attachmentCreateEvent.getAttachments()) {
                this.createImageDetails(attachment);
            }
        } else if (event instanceof GeneralAttachmentUpdateEvent) {
            GeneralAttachmentUpdateEvent attachmentUpdateEvent = (GeneralAttachmentUpdateEvent)event;
            if (this.hasImageContentType(attachmentUpdateEvent.getOld())) {
                this.imageDetailsDao.removeDetailsFor(attachmentUpdateEvent.getNew());
            }
            this.createImageDetails(attachmentUpdateEvent.getNew());
        }
    }

    public Class<?>[] getHandledEventClasses() {
        return new Class[]{GeneralAttachmentUpdateEvent.class, GeneralAttachmentRemoveEvent.class, GeneralAttachmentCreateEvent.class};
    }

    private boolean hasImageContentType(Attachment attachment) {
        return this.thumbnailManager.isThumbnailable(attachment);
    }

    public void setAttachmentManager(AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    public void setImageDetailsDao(ImageDetailsDao imageDetailsDao) {
        this.imageDetailsDao = imageDetailsDao;
    }

    public void setThumbnailManager(ThumbnailManager thumbnailManager) {
        this.thumbnailManager = thumbnailManager;
    }
}

