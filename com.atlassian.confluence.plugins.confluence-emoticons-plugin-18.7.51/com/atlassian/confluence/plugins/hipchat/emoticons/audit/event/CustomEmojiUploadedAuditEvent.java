/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.audit.event;

import com.atlassian.confluence.plugins.hipchat.emoticons.audit.event.AbstractEmojiAuditEvent;

public class CustomEmojiUploadedAuditEvent
extends AbstractEmojiAuditEvent {
    public CustomEmojiUploadedAuditEvent(String emojiShortcut, String emojiName) {
        super(emojiShortcut, emojiName);
    }
}

