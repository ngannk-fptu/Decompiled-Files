/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 */
package com.atlassian.migration.agent.service.recentlyviewed;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.migration.agent.service.RecentlyViewedService;
import com.atlassian.migration.agent.service.recentlyviewed.LegacyRecentlyViewedService;
import com.atlassian.migration.agent.service.recentlyviewed.RecentlyViewedManagerWrapper;
import com.atlassian.migration.agent.service.version.ConfluenceServerVersion;

public class RecentlyViewedServiceLocator {
    private SystemInformationService systemInformationService;
    private RecentlyViewedManagerWrapper recentlyViewedManagerWrapper;
    private LegacyRecentlyViewedService legacyRecentlyViewedService;

    public RecentlyViewedServiceLocator(SystemInformationService systemInformationService, RecentlyViewedManagerWrapper recentlyViewedManagerWrapper, LegacyRecentlyViewedService legacyRecentlyViewedService) {
        this.systemInformationService = systemInformationService;
        this.recentlyViewedManagerWrapper = recentlyViewedManagerWrapper;
        this.legacyRecentlyViewedService = legacyRecentlyViewedService;
    }

    public RecentlyViewedService getRecentlyViewedService() {
        ConfluenceInfo confluenceInfo = this.systemInformationService.getConfluenceInfo();
        ConfluenceServerVersion version = ConfluenceServerVersion.of(confluenceInfo.getVersion());
        if (version.greaterOrEqual("6.2.0")) {
            return this.recentlyViewedManagerWrapper;
        }
        return this.legacyRecentlyViewedService;
    }
}

