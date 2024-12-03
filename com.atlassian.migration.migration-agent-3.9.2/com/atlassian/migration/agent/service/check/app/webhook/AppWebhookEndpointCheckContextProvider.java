/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.app.webhook;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.app.webhook.AppWebhookEndpointCheckContext;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppWebhookEndpointCheckContextProvider
implements CheckContextProvider<AppWebhookEndpointCheckContext> {
    @Override
    public AppWebhookEndpointCheckContext apply(Map<String, Object> parameters) {
        return new AppWebhookEndpointCheckContext(ContextProviderUtil.getCloudId(parameters), Arrays.stream(ContextProviderUtil.getAppsKey(parameters)).collect(Collectors.toSet()));
    }
}

