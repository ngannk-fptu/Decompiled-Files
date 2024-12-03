/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.audit.event;

public abstract class AbstractEmojiAuditEvent {
    final String emojiShortcut;
    final String emojiName;

    public AbstractEmojiAuditEvent(String emojiShortcut, String emojiName) {
        this.emojiShortcut = emojiShortcut;
        this.emojiName = emojiName;
    }

    public String getEmojiShorcut() {
        return this.emojiShortcut;
    }

    public String getEmojiName() {
        return this.emojiName;
    }
}

