/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.audit.event;

import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.AbstractEmojiDeletedAuditEvent;

public class CustomEmojiDeletedByUserAuditEvent
extends AbstractEmojiDeletedAuditEvent {
    public CustomEmojiDeletedByUserAuditEvent(String emojiShortcut, String emojiName, String creatorUsername) {
        super(emojiShortcut, emojiName, creatorUsername);
    }
}

