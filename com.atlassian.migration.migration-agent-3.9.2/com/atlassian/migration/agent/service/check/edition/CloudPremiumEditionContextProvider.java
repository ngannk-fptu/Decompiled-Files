/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.edition;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.edition.CloudPremiumEditionContext;
import java.util.Map;

public class CloudPremiumEditionContextProvider
implements CheckContextProvider<CloudPremiumEditionContext> {
    @Override
    public CloudPremiumEditionContext apply(Map<String, Object> parameters) {
        String cloudId = ContextProviderUtil.getCloudId(parameters);
        return new CloudPremiumEditionContext(cloudId);
    }
}

