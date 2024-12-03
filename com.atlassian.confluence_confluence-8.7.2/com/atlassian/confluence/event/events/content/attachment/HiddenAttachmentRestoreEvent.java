/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.GeneralAttachmentRestoreEvent;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.user.ConfluenceUser;
import org.checkerframework.checker.nullness.qual.Nullable;

public class HiddenAttachmentRestoreEvent
extends GeneralAttachmentRestoreEvent {
    private static final long serialVersionUID = 5674764045712823865L;

    public HiddenAttachmentRestoreEvent(Object source, Attachment attachment, @Nullable ConfluenceUser originatingUser) {
        super(source, attachment, originatingUser, true);
    }
}

