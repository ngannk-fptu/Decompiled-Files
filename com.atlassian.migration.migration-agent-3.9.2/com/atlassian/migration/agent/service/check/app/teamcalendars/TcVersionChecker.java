/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.migration.agent.service.check.app.teamcalendars;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.check.app.teamcalendars.TcVersionContext;
import com.atlassian.migration.agent.service.version.ConfluenceServerVersion;
import com.atlassian.plugin.Plugin;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TcVersionChecker
implements Checker<TcVersionContext> {
    public static final String TC_APP_VERSION = "tcAppVersion";
    private final MigrationAgentConfiguration configuration;

    public TcVersionChecker(MigrationAgentConfiguration configuration) {
        this.configuration = configuration;
    }

    public CheckResult check(TcVersionContext ctx) {
        List<Plugin> enabledPlugins = ctx.enabledPlugins;
        Optional<Object> teamCalendarPlugin = Optional.empty();
        if (enabledPlugins != null) {
            teamCalendarPlugin = enabledPlugins.stream().filter(plugin -> plugin.getKey().equals("com.atlassian.confluence.extra.team-calendars")).findFirst();
        }
        if (teamCalendarPlugin.isPresent()) {
            String teamCalendarVersion = ((Plugin)teamCalendarPlugin.get()).getPluginInformation().getVersion();
            ConfluenceServerVersion teamCalendarPluginVersion = ConfluenceServerVersion.of(teamCalendarVersion);
            if (teamCalendarPluginVersion.lessThan(this.configuration.getMinSupportedTCVersion())) {
                return new CheckResult(false, Collections.singletonMap(TC_APP_VERSION, teamCalendarVersion));
            }
            return new CheckResult(true, Collections.singletonMap(TC_APP_VERSION, teamCalendarVersion));
        }
        return new CheckResult(true);
    }

    static String retrieveTcAppVersion(Map<String, Object> details) {
        return details.getOrDefault(TC_APP_VERSION, null);
    }
}

