/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.atlassian.migration.agent.service.check.space;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.space.SpaceAnonymousPermissionContext;
import com.google.common.collect.Sets;
import java.util.Map;

public class SpaceAnonymousPermissionCheckContextProvider
implements CheckContextProvider<SpaceAnonymousPermissionContext> {
    @Override
    public SpaceAnonymousPermissionContext apply(Map<String, Object> parameters) {
        Object[] keys = ContextProviderUtil.getSpaceKeys(parameters);
        return new SpaceAnonymousPermissionContext(Sets.newHashSet((Object[])keys));
    }
}

