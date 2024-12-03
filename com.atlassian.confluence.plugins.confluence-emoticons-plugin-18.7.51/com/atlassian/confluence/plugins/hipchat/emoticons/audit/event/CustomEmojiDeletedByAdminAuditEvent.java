/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.audit.event;

import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.AbstractEmojiDeletedAuditEvent;

public class CustomEmojiDeletedByAdminAuditEvent
extends AbstractEmojiDeletedAuditEvent {
    public CustomEmojiDeletedByAdminAuditEvent(String emojiShortcut, String emojiName, String creatorUserName) {
        super(emojiShortcut, emojiName, creatorUserName);
    }
}

