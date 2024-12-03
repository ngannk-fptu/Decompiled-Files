/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.atlassian.migration.agent.service.check.version;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.version.ConfluenceSupportedVersionCheckContext;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class ConfluenceSupportedVersionCheckContextProvider
implements CheckContextProvider<ConfluenceSupportedVersionCheckContext> {
    private final SystemInformationService systemInformationService;

    public ConfluenceSupportedVersionCheckContextProvider(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    @Override
    public ConfluenceSupportedVersionCheckContext apply(Map<String, Object> t) {
        String confluenceVersion = this.systemInformationService.getConfluenceInfo().getVersion();
        return new ConfluenceSupportedVersionCheckContext(confluenceVersion);
    }
}

