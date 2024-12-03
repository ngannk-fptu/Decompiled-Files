/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentRemoveEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AttachmentRemoveEvent
extends GeneralAttachmentRemoveEvent {
    private static final long serialVersionUID = 3826574287209020823L;

    public AttachmentRemoveEvent(Object src, Attachment attachment, @Nullable User remover) {
        this(src, attachment, remover, false);
    }

    public AttachmentRemoveEvent(Object src, Attachment attachment, @Nullable User remover, boolean suppressNotifications) {
        super(src, attachment, remover, suppressNotifications);
    }
}

