/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.validation.ValidationError
 */
package com.atlassian.confluence.api.impl.model.validation;

import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.validation.ValidationError;

public class DefaultValidationError
implements ValidationError {
    private final Message message;

    public DefaultValidationError(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }

    public String toString() {
        return DefaultValidationError.class.getSimpleName() + " " + this.message;
    }
}

