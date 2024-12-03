/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.webhook.Webhook
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.HttpClients
 *  org.apache.http.util.EntityUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.webhook;

import com.atlassian.crowd.model.webhook.Webhook;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookPinger {
    private static final Logger logger = LoggerFactory.getLogger(WebhookPinger.class);
    private final HttpClient httpClient;

    public WebhookPinger() {
        this((HttpClient)HttpClients.createDefault());
    }

    public WebhookPinger(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void ping(Webhook webhook) throws IOException {
        HttpPost method;
        logger.debug("Pinging Webhook {} at endpoint {}", (Object)webhook.getId(), (Object)webhook.getEndpointUrl());
        if (webhook.getEndpointUrl().isEmpty()) {
            throw new IOException("Unable to ping because the endpoint URL is empty.");
        }
        try {
            method = new HttpPost(webhook.getEndpointUrl());
        }
        catch (IllegalArgumentException e) {
            throw new IOException("Failed to parse webhook endpoint url for ping (endpoint url might be invalid): " + webhook.getEndpointUrl(), e);
        }
        if (webhook.getToken() != null) {
            method.setHeader("Authorization", "Basic " + webhook.getToken());
        }
        HttpResponse response = this.httpClient.execute((HttpUriRequest)method);
        EntityUtils.consumeQuietly((HttpEntity)response.getEntity());
        int statusCode = response.getStatusLine().getStatusCode();
        if (!WebhookPinger.isSuccessfulStatusCode(statusCode)) {
            throw new IOException("Webhook endpoint returned status code " + statusCode);
        }
        logger.debug("Webhook {} successfully pinged at endpoint {}", (Object)webhook.getId(), (Object)webhook.getEndpointUrl());
    }

    private static boolean isSuccessfulStatusCode(int statusCode) {
        return statusCode / 100 == 2;
    }
}

