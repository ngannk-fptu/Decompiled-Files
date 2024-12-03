/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.app.license;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.ContextProviderUtil;
import com.atlassian.migration.agent.service.check.app.license.AppLicenseContext;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class AppLicenseContextProvider
implements CheckContextProvider<AppLicenseContext> {
    @Override
    public AppLicenseContext apply(Map<String, Object> parameters) {
        return new AppLicenseContext(ContextProviderUtil.getCloudId(parameters), Arrays.stream(ContextProviderUtil.getAppsKey(parameters)).collect(Collectors.toList()));
    }
}

