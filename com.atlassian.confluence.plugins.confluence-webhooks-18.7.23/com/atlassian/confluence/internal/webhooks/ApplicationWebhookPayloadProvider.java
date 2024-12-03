/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.WebhookPayloadBuilder
 *  com.atlassian.webhooks.WebhookPayloadProvider
 *  com.google.common.net.MediaType
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.internal.webhooks;

import com.atlassian.confluence.internal.webhooks.ApplicationWebhookEvent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.WebhookPayloadBuilder;
import com.atlassian.webhooks.WebhookPayloadProvider;
import com.google.common.net.MediaType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={WebhookPayloadProvider.class})
public class ApplicationWebhookPayloadProvider
implements WebhookPayloadProvider {
    private static final Logger log = LoggerFactory.getLogger(ApplicationWebhookPayloadProvider.class);
    private static final byte[] EMPTY_BODY = new byte[0];
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public int getWeight() {
        return 100;
    }

    public void setPayload(WebhookInvocation invocation, WebhookPayloadBuilder builder) {
        WebhookEvent event = invocation.getEvent();
        if (event instanceof ApplicationWebhookEvent) {
            byte[] body = invocation.getPayload().map(this::toJson).map(bodyString -> bodyString.getBytes(StandardCharsets.UTF_8)).orElse(EMPTY_BODY);
            builder.body(body, MediaType.JSON_UTF_8.toString());
        }
    }

    public boolean supports(WebhookInvocation invocation) {
        return invocation.getEvent() instanceof ApplicationWebhookEvent;
    }

    private String toJson(Object payload) {
        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        }
        catch (IOException e) {
            log.warn("Unexpected exception while rendering an event object to a JSON string", (Throwable)e);
            return "";
        }
    }
}

