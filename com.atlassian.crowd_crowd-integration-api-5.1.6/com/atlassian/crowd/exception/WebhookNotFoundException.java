/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.ObjectNotFoundException
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.ObjectNotFoundException;
import com.atlassian.crowd.model.webhook.Webhook;

public class WebhookNotFoundException
extends ObjectNotFoundException {
    public WebhookNotFoundException(long webhookId) {
        super(Webhook.class, (Object)("Webhook <" + webhookId + "> not found"));
    }

    public WebhookNotFoundException(long applicationId, String endpointUrl) {
        super(Webhook.class, (Object)("applicationId=" + applicationId + ",endpointUrl=" + endpointUrl));
    }

    public WebhookNotFoundException(String message) {
        super(message);
    }
}

