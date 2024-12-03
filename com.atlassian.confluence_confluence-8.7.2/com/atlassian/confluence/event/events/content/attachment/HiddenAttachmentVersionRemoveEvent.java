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

public class HiddenAttachmentVersionRemoveEvent
extends GeneralAttachmentVersionRemoveEvent {
    private static final long serialVersionUID = 4280100798175936296L;

    public HiddenAttachmentVersionRemoveEvent(Object src, Attachment attachment, @Nullable User remover) {
        super(src, attachment, remover, true);
    }
}

