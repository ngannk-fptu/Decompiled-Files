/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json;

import com.atlassian.confluence.util.i18n.Message;

public class SingleErrorJSONResult {
    private final String messageKey;
    private final Object[] arguments;

    public SingleErrorJSONResult(String messageKey) {
        this(messageKey, new Object[0]);
    }

    public SingleErrorJSONResult(String messageKey, Object[] arguments) {
        this.messageKey = messageKey;
        this.arguments = arguments;
    }

    public Message getErrorMessage() {
        return Message.getInstance(this.messageKey, this.arguments);
    }
}

