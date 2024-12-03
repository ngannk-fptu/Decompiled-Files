/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentVersionRemoveEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AttachmentVersionRemoveEvent
extends GeneralAttachmentVersionRemoveEvent {
    private static final long serialVersionUID = 200727737880870953L;

    public AttachmentVersionRemoveEvent(Object src, Attachment attachment, @Nullable User remover) {
        super(src, attachment, remover);
    }

    public AttachmentVersionRemoveEvent(Object src, Attachment attachment, @Nullable User remover, boolean shouldSuppressNotifications) {
        super(src, attachment, remover, shouldSuppressNotifications);
    }
}

