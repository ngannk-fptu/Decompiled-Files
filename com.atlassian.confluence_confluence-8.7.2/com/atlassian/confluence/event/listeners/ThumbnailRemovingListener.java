/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.event.listeners;

import com.atlassian.confluence.event.events.content.attachment.AttachmentRemoveEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentUpdateEvent;
import com.atlassian.confluence.event.events.content.attachment.AttachmentVersionRemoveEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;

public class ThumbnailRemovingListener
implements InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ThumbnailRemovingListener.class);
    private final ThumbnailManager thumbnailManager;
    private final AttachmentManager attachmentManager;
    private final EventPublisher eventPublisher;

    public ThumbnailRemovingListener(ThumbnailManager thumbnailManager, @Qualifier(value="attachmentManager") AttachmentManager attachmentManager, EventPublisher eventPublisher) {
        this.thumbnailManager = thumbnailManager;
        this.attachmentManager = attachmentManager;
        this.eventPublisher = eventPublisher;
    }

    @EventListener
    public void handleAttachmentUpdateEvent(AttachmentUpdateEvent event) {
        this.removeThumbnailsOfAllVersions(event.getNew());
    }

    @EventListener
    public void handleAttachmentRemoveEvent(AttachmentRemoveEvent event) {
        this.removeThumbnailsOfAllVersions(event.getAttachment());
    }

    @EventListener
    public void handleAttachmentVersionRemoveEvent(AttachmentVersionRemoveEvent event) {
        this.removeThumbnailsOfAttachment(event.getAttachment());
    }

    private void removeThumbnailsOfAllVersions(Attachment attachment) {
        List<Attachment> allVersions = this.attachmentManager.getAllVersions(attachment);
        if (allVersions != null) {
            for (Attachment attachmentVersion : allVersions) {
                this.removeThumbnailsOfAttachment(attachmentVersion);
            }
        }
    }

    private void removeThumbnailsOfAttachment(Attachment attachment) {
        boolean success = this.thumbnailManager.removeThumbnail(attachment);
        if (log.isDebugEnabled()) {
            log.debug("Thumbnail for attachment: " + attachment.getFileName() + " version=" + attachment.getVersion() + " " + (success ? " successfully removed." : " was not found or could not be removed."));
        }
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }
}

