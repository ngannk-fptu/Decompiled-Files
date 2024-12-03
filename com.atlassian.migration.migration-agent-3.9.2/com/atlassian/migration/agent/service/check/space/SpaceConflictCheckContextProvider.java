/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.space;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.space.SpaceConflictContext;
import com.google.common.collect.Sets;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class SpaceConflictCheckContextProvider
implements CheckContextProvider<SpaceConflictContext> {
    @Override
    public SpaceConflictContext apply(Map<String, Object> parameters) {
        String cloudId = ContextProviderUtil.getCloudId(parameters);
        Object[] keys = ContextProviderUtil.getSpaceKeys(parameters);
        return new SpaceConflictContext(cloudId, Sets.newHashSet((Object[])keys));
    }
}

