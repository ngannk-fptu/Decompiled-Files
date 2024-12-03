/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.core.util.Message;
import com.atlassian.applinks.ui.RequestException;

public class BadRequestException
extends RequestException {
    public BadRequestException() {
        this((Message)null);
    }

    public BadRequestException(Message message) {
        super(400, message);
    }

    public BadRequestException(Message message, Throwable cause) {
        super(400, message, cause);
    }
}

