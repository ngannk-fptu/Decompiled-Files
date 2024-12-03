/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.attachment;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.index.attachment.AttachmentStatus;
import com.atlassian.confluence.internal.index.attachment.AttachmentStatusManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.persistence.dao.AttachmentDao;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultAttachmentStatusManager
implements AttachmentStatusManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultAttachmentStatusManager.class);
    private final AttachmentDao attachmentDao;

    public DefaultAttachmentStatusManager(AttachmentDao attachmentDao) {
        this.attachmentDao = attachmentDao;
    }

    @Override
    public void updateAttachmentStatus(long id, AttachmentStatus status) {
        Optional<AttachmentStatus> attachmentStatus;
        Attachment attachment = this.attachmentDao.getById(id);
        if (!(attachment == null || (attachmentStatus = this.getAttachmentStatus(attachment)).isPresent() && attachmentStatus.get().getPriority() >= status.getPriority())) {
            attachment.getProperties().setStringProperty("_atl_AttachmentStatus", status.name());
        }
    }

    @Override
    public Optional<AttachmentStatus> getAttachmentStatus(long id) {
        Attachment attachment = this.attachmentDao.getById(id);
        return attachment == null ? Optional.empty() : this.getAttachmentStatus(attachment);
    }

    private Optional<AttachmentStatus> getAttachmentStatus(@NonNull Attachment attachment) {
        String value = attachment.getProperties().getStringProperty("_atl_AttachmentStatus");
        return AttachmentStatus.ofNullable(value == null ? null : value.toString());
    }
}

