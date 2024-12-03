/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webhooks;

public class NoSuchWebhookException
extends RuntimeException {
    static final long serialVersionUID = 1L;

    public NoSuchWebhookException() {
    }

    public NoSuchWebhookException(String message) {
        super(message);
    }
}

