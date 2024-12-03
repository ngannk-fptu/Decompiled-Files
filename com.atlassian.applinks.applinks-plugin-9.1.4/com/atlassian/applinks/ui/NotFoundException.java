/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.core.util.Message;
import com.atlassian.applinks.ui.RequestException;

public class NotFoundException
extends RequestException {
    public NotFoundException() {
        this((Message)null);
    }

    public NotFoundException(Message message) {
        super(400, message);
    }

    public NotFoundException(Message message, Throwable cause) {
        super(404, message, cause);
    }
}

