/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.webhook;

public class InvalidWebhookEndpointException
extends Exception {
    public InvalidWebhookEndpointException(String endpointUrl, String reason) {
        super("Webhook endpoint url of '" + endpointUrl + "' is invalid " + reason);
    }

    public InvalidWebhookEndpointException(String endpointUrl, Throwable cause) {
        super("Webhook endpoint url of '" + endpointUrl + "' is invalid", cause);
    }
}

