/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.diagnostics;

import com.atlassian.webhooks.request.WebhookHttpRequest;
import com.atlassian.webhooks.request.WebhookHttpResponse;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WebhookDiagnosticsResult {
    private WebhookHttpRequest request;
    private WebhookHttpResponse response;
    private Throwable error;

    private WebhookDiagnosticsResult() {
    }

    @Nonnull
    public static WebhookDiagnosticsResult build(WebhookHttpRequest request, WebhookHttpResponse response) {
        WebhookDiagnosticsResult result = new WebhookDiagnosticsResult();
        result.request = request;
        result.response = response;
        return result;
    }

    @Nonnull
    public static WebhookDiagnosticsResult build(WebhookHttpRequest request, Throwable error) {
        WebhookDiagnosticsResult result = new WebhookDiagnosticsResult();
        result.request = request;
        result.error = error;
        return result;
    }

    @Nullable
    public Throwable getError() {
        return this.error;
    }

    @Nonnull
    public WebhookHttpRequest getRequest() {
        return this.request;
    }

    @Nullable
    public WebhookHttpResponse getResponse() {
        return this.response;
    }

    public boolean isError() {
        return this.error != null;
    }
}

