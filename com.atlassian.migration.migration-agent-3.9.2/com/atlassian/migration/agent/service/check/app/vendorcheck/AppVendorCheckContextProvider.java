/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.app.vendorcheck;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.app.vendorcheck.AppVendorCheckContext;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class AppVendorCheckContextProvider
implements CheckContextProvider<AppVendorCheckContext> {
    @Override
    public AppVendorCheckContext apply(Map<String, Object> parameters) {
        return new AppVendorCheckContext(ContextProviderUtil.getPlanId(parameters), ContextProviderUtil.getPlanName(parameters), ContextProviderUtil.getPlanMigrationTag(parameters), ContextProviderUtil.getCloudId(parameters), Arrays.stream(ContextProviderUtil.getSpaceKeys(parameters)).collect(Collectors.toSet()), Arrays.stream(ContextProviderUtil.getAppsKey(parameters)).collect(Collectors.toSet()));
    }
}

