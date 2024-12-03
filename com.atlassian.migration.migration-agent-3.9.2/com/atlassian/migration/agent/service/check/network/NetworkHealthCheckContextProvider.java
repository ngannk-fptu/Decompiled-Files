/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.network;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.network.NetworkHealthContext;
import java.util.Map;

public class NetworkHealthCheckContextProvider
implements CheckContextProvider<NetworkHealthContext> {
    @Override
    public NetworkHealthContext apply(Map<String, Object> parameters) {
        String cloudId = ContextProviderUtil.getCloudId(parameters);
        return new NetworkHealthContext(cloudId);
    }
}

