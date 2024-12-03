/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.ui;

import com.atlassian.applinks.core.util.Message;

public class RequestException
extends RuntimeException {
    private final int status;
    private final Message message;
    protected String template;

    public RequestException(int status, Message message, Throwable cause) {
        super(cause);
        this.message = message;
        this.status = status;
    }

    public RequestException(int status, Message message) {
        this.message = message;
        this.status = status;
    }

    public RequestException(int status) {
        this(status, null);
    }

    public int getStatus() {
        return this.status;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    @Override
    public String getMessage() {
        return this.message == null ? null : this.message.toString();
    }
}

