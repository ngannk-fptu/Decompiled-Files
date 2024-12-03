/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webhooks;

public class WebhooksNotInitializedException
extends RuntimeException {
    public WebhooksNotInitializedException() {
    }

    public WebhooksNotInitializedException(String message) {
        super(message);
    }

    public WebhooksNotInitializedException(String message, Throwable cause) {
        super(message, cause);
    }

    public WebhooksNotInitializedException(Throwable cause) {
        super(cause);
    }
}

