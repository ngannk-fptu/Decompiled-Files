/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.audit.event;

import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.AbstractEmojiAuditEvent;

public class AbstractEmojiDeletedAuditEvent
extends AbstractEmojiAuditEvent {
    final String creatorUsername;

    public AbstractEmojiDeletedAuditEvent(String emojiShortcut, String emojiName, String creatorUsername) {
        super(emojiShortcut, emojiName);
        this.creatorUsername = creatorUsername;
    }

    public String getCreatorUsername() {
        return this.creatorUsername;
    }
}

