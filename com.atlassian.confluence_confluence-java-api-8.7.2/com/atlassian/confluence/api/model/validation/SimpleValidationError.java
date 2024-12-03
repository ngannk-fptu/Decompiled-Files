/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.validation;

import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.validation.ValidationError;

public class SimpleValidationError
implements ValidationError {
    private final Message message;

    public SimpleValidationError(String key, Object ... args) {
        this.message = SimpleMessage.withKeyAndArgs(key, args);
    }

    public SimpleValidationError(Message message) {
        this.message = message;
    }

    @Override
    public Message getMessage() {
        return this.message;
    }

    public String toString() {
        return "SimpleValidationError{message=" + this.message + '}';
    }
}

