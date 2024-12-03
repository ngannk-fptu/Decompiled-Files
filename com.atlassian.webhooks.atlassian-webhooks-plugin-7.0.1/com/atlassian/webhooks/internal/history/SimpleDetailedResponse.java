/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.history.DetailedInvocationResponse
 *  com.atlassian.webhooks.history.InvocationOutcome
 *  com.atlassian.webhooks.request.WebhookHttpResponse
 *  com.atlassian.webhooks.request.WebhookResponseBody
 *  com.google.common.io.CharStreams
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.history.DetailedInvocationResponse;
import com.atlassian.webhooks.history.InvocationOutcome;
import com.atlassian.webhooks.internal.history.BodyUtils;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import com.atlassian.webhooks.request.WebhookResponseBody;
import com.google.common.io.CharStreams;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDetailedResponse
implements DetailedInvocationResponse {
    private static final Logger log = LoggerFactory.getLogger(SimpleDetailedResponse.class);
    private final String body;
    private final String description;
    private final Map<String, String> headers;
    private final InvocationOutcome outcome;
    private final int statusCode;

    public SimpleDetailedResponse(@Nonnull InvocationOutcome outcome, @Nonnull WebhookInvocation invocation, @Nonnull WebhookHttpResponse response) {
        String body;
        this.outcome = Objects.requireNonNull(outcome, "outcome");
        this.description = this.describe(outcome, Objects.requireNonNull(response, "response"));
        this.headers = response.getHeaders().getHeaders();
        try {
            body = this.loadBody(response);
        }
        catch (IOException e) {
            log.debug("Failed to load response body for webhook invocation {}", (Object)invocation.getId());
            body = null;
        }
        this.body = body;
        this.statusCode = response.getStatusCode();
    }

    public SimpleDetailedResponse(String body, @Nonnull String description, @Nonnull Map<String, String> headers, @Nonnull InvocationOutcome outcome, int statusCode) {
        this.body = body;
        this.description = description;
        this.headers = headers;
        this.outcome = outcome;
        this.statusCode = statusCode;
    }

    @Nonnull
    public Optional<String> getBody() {
        return Optional.ofNullable(this.body);
    }

    @Nonnull
    public String getDescription() {
        return this.description;
    }

    @Nonnull
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Nonnull
    public InvocationOutcome getOutcome() {
        return this.outcome;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    private String describe(InvocationOutcome resultKind, WebhookHttpResponse response) {
        switch (resultKind) {
            case FAILURE: 
            case SUCCESS: {
                return Integer.toString(response.getStatusCode());
            }
        }
        return "Unknown";
    }

    private String loadBody(WebhookHttpResponse response) throws IOException {
        WebhookResponseBody responseBody = response.getBody();
        String contentType = responseBody.getContentType().orElse(null);
        if (!BodyUtils.isTextContent(contentType)) {
            return "<binary data>";
        }
        return CharStreams.toString((Readable)new BufferedReader(new InputStreamReader(responseBody.getContent(), BodyUtils.getCharset(contentType))));
    }
}

