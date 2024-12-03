/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.AttachmentViewEvent;
import com.atlassian.confluence.pages.Attachment;

public class ProfilePictureViewEvent
extends AttachmentViewEvent {
    private static final long serialVersionUID = 5968428742059690889L;

    public ProfilePictureViewEvent(Object src, Attachment attachment) {
        super(src, attachment);
    }
}

