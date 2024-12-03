/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.content.attachment;

import com.atlassian.confluence.event.events.content.attachment.ProfilePictureViewEvent;
import com.atlassian.confluence.pages.Attachment;

public class ProfilePictureThumbnailViewEvent
extends ProfilePictureViewEvent {
    private static final long serialVersionUID = 4766603210827326664L;

    public ProfilePictureThumbnailViewEvent(Object src, Attachment attachment) {
        super(src, attachment);
    }
}

