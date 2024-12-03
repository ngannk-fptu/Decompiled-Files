/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.Message
 */
package com.atlassian.confluence.plugins.email.medium;

import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.Message;
import java.util.Optional;

public interface ReplyToFieldProvider {
    public static final String REPLY_TO_ADDRESS = "replyToAddress";

    @Deprecated
    public Option<String> getReplyToField(Message var1);

    default public Optional<String> optionalReplyToField(Message message) {
        return Optional.ofNullable((String)this.getReplyToField(message).getOrNull());
    }
}

