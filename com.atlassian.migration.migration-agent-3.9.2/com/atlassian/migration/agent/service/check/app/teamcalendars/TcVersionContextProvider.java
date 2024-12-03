/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 */
package com.atlassian.migration.agent.service.check.app.teamcalendars;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionContext;
import java.util.List;
import java.util.Map;

public class TcVersionContextProvider
implements CheckContextProvider<TcVersionContext> {
    private final SystemInformationService systemInformationService;

    public TcVersionContextProvider(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    @Override
    public TcVersionContext apply(Map<String, Object> parameters) {
        List enabledPlugins = this.systemInformationService.getConfluenceInfo().getEnabledPlugins();
        return new TcVersionContext(enabledPlugins);
    }
}

