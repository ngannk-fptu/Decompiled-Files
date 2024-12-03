/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.pages.attachments.filesystem;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostMovedEvent;
import com.atlassian.confluence.event.events.content.page.PageMoveEvent;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.impl.pages.attachments.filesystem.model.Refs;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentDataStorageType;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.persistence.dao.filesystem.AttachmentDataFileSystemException;
import com.atlassian.confluence.pages.persistence.dao.filesystem.UpdateAttachmentsOnFileSystemException;
import com.atlassian.confluence.spaces.Space;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateAttachmentsOnFilesystemOnPageMoveHandler {
    private static final Logger log = LoggerFactory.getLogger(UpdateAttachmentsOnFilesystemOnPageMoveHandler.class);
    private AttachmentMover attachmentMover;
    private AttachmentManager attachmentManager;

    public UpdateAttachmentsOnFilesystemOnPageMoveHandler(AttachmentDataFileSystem attachmentDataFileSystem, AttachmentManager attachmentManager) {
        this.attachmentManager = attachmentManager;
        this.setAttachmentMover((ceo, s1, s2) -> attachmentDataFileSystem.moveAttachments(Refs.ref(ceo), Refs.ref(s1), Refs.ref(s2)));
    }

    public void handleBlogPostMovedEvent(BlogPostMovedEvent blogPostMovedEvent) {
        if (!this.isUpdateRequired(blogPostMovedEvent)) {
            return;
        }
        this.moveAttachments(blogPostMovedEvent.getBlogPost(), blogPostMovedEvent.getOriginalSpace(), blogPostMovedEvent.getCurrentSpace());
    }

    public void handlePageMovedEvent(PageMoveEvent pageMoveEvent) {
        if (!this.isUpdateRequired(pageMoveEvent)) {
            return;
        }
        List<Page> movedPageList = pageMoveEvent.getMovedPageList();
        if (movedPageList == null) {
            return;
        }
        for (Page page : movedPageList) {
            this.moveAttachments(page, pageMoveEvent.getOldSpace(), pageMoveEvent.getPage().getSpace());
        }
    }

    private void moveAttachments(ContentEntityObject contentEntityObject, Space oldSpace, Space newSpace) {
        log.debug("contentEntityObject: {}, old space {}", (Object)contentEntityObject, (Object)oldSpace);
        List<Attachment> attachments = this.attachmentManager.getAllVersionsOfAttachments(contentEntityObject);
        if (attachments == null || attachments.isEmpty()) {
            return;
        }
        try {
            this.attachmentMover.moveAttachments(contentEntityObject, oldSpace, newSpace);
        }
        catch (AttachmentDataFileSystemException e) {
            throw new UpdateAttachmentsOnFileSystemException(e);
        }
    }

    private boolean isUpdateRequired(BlogPostMovedEvent event) {
        if (this.attachmentManager.getBackingStorageType() == null || !this.attachmentManager.getBackingStorageType().equals((Object)AttachmentDataStorageType.FILE_SYSTEM)) {
            return false;
        }
        return !event.getOriginalSpace().equals(event.getCurrentSpace());
    }

    private boolean isUpdateRequired(PageMoveEvent event) {
        if (this.attachmentManager.getBackingStorageType() == null || !this.attachmentManager.getBackingStorageType().equals((Object)AttachmentDataStorageType.FILE_SYSTEM)) {
            return false;
        }
        return !event.getOldSpace().equals(event.getPage().getSpace());
    }

    void setAttachmentMover(AttachmentMover attachmentMover) {
        this.attachmentMover = attachmentMover;
    }

    @FunctionalInterface
    static interface AttachmentMover {
        public void moveAttachments(ContentEntityObject var1, Space var2, Space var3);
    }
}

