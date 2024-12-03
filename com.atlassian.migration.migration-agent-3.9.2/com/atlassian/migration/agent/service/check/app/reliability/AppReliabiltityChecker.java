/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.app.reliability;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.check.app.reliability.AppReliabilityContext;
import com.atlassian.migration.agent.service.check.app.reliability.NotReliableApp;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class AppReliabiltityChecker
implements Checker<AppReliabilityContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(AppReliabiltityChecker.class);
    private MigrationAppAggregatorService migrationAppAggregatorService;
    private PluginManager pluginManager;

    public AppReliabiltityChecker(MigrationAppAggregatorService migrationAppAggregatorService, PluginManager pluginManager) {
        this.migrationAppAggregatorService = migrationAppAggregatorService;
        this.pluginManager = pluginManager;
    }

    public CheckResult check(AppReliabilityContext ctx) {
        HashMap occurrences = new HashMap();
        HashSet apps = new HashSet();
        occurrences.put("listOfOccurrences", apps);
        ctx.appKeys.stream().map(this.pluginManager::getPlugin).filter(Objects::nonNull).filter(it -> !this.migrationAppAggregatorService.isAppReliable(it.getKey())).forEach(plugin -> apps.add(new NotReliableApp(plugin.getKey(), plugin.getName())));
        return new CheckResult(apps.isEmpty(), occurrences);
    }
}

