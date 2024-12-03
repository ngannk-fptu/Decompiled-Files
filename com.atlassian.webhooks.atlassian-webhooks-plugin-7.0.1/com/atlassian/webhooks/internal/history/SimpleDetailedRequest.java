/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookInvocation
 *  com.atlassian.webhooks.history.DetailedInvocationRequest
 *  com.atlassian.webhooks.request.Method
 *  com.atlassian.webhooks.request.WebhookHttpRequest
 *  com.google.common.io.CharStreams
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal.history;

import com.atlassian.webhooks.WebhookInvocation;
import com.atlassian.webhooks.history.DetailedInvocationRequest;
import com.atlassian.webhooks.internal.history.BodyUtils;
import com.atlassian.webhooks.request.Method;
import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.google.common.io.CharStreams;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleDetailedRequest
implements DetailedInvocationRequest {
    private static final Logger log = LoggerFactory.getLogger(SimpleDetailedRequest.class);
    private final String body;
    private final Map<String, String> headers;
    private final Method method;
    private final String url;

    public SimpleDetailedRequest(@Nonnull WebhookInvocation invocation, @Nonnull WebhookHttpRequest request) {
        String body;
        try {
            body = this.loadBody(Objects.requireNonNull(request, "request"));
        }
        catch (IOException e) {
            log.debug("Failed to load request body for webhook invocation {}", (Object)invocation.getId());
            body = null;
        }
        this.body = body;
        this.headers = request.getHeaders();
        this.method = request.getMethod();
        this.url = request.getUrl();
    }

    public SimpleDetailedRequest(String body, @Nonnull Map<String, String> headers, @Nonnull Method method, @Nonnull String url) {
        this.body = body;
        this.headers = headers;
        this.method = method;
        this.url = url;
    }

    @Nonnull
    public Optional<String> getBody() {
        return Optional.ofNullable(this.body);
    }

    @Nonnull
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @Nonnull
    public Method getMethod() {
        return this.method;
    }

    @Nonnull
    public String getUrl() {
        return this.url;
    }

    private String loadBody(WebhookHttpRequest request) throws IOException {
        String contentType = request.getContentType().orElse(null);
        if (!BodyUtils.isTextContent(contentType)) {
            throw new IllegalArgumentException(String.format("Request payload with content type \"%s\" is not text", contentType));
        }
        byte[] body = request.getContent();
        if (body == null || body.length == 0) {
            return null;
        }
        return CharStreams.toString((Readable)new BufferedReader(new InputStreamReader((InputStream)new ByteArrayInputStream(body), BodyUtils.getCharset(contentType))));
    }
}

