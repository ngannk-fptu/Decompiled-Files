/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.AppWebhookEndpointCheckRequest
 *  com.atlassian.migration.app.dto.AppWebhookEndpointCheckResponse
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.agent.service.check.app.webhook;

import com.atlassian.migration.agent.okhttp.HttpException;
import com.atlassian.migration.app.dto.AppWebhookEndpointCheckRequest;
import com.atlassian.migration.app.dto.AppWebhookEndpointCheckResponse;
import org.jetbrains.annotations.NotNull;

public interface AppWebhookEndpointCheckServiceClient {
    public AppWebhookEndpointCheckResponse retrieveRegisteredWebhooks(@NotNull String var1, @NotNull AppWebhookEndpointCheckRequest var2) throws HttpException;
}

