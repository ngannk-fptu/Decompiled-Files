/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 *  com.atlassian.migration.app.dto.MigrationPath
 *  com.atlassian.plugin.Plugin
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.check.app.outdated;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.service.MigrationAppAggregatorResponse;
import com.atlassian.migration.agent.service.PreflightErrorCode;
import com.atlassian.migration.agent.service.app.PluginManager;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedContext;
import com.atlassian.migration.agent.service.check.app.outdated.ServerAppsOutdatedDto;
import com.atlassian.migration.agent.service.impl.AppAssessmentFacade;
import com.atlassian.migration.agent.service.impl.MigrationAppAggregatorService;
import com.atlassian.migration.app.MigratabliltyInfo;
import com.atlassian.migration.app.dto.MigrationPath;
import com.atlassian.plugin.Plugin;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class ServerAppsOutdatedChecker
implements Checker<ServerAppsOutdatedContext> {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ServerAppsOutdatedChecker.class);
    private static final String VIOLATIONS_KEY = "violations";
    private final PluginManager pluginManager;
    private final MigrationAppAggregatorService appAggregatorService;
    private final AppAssessmentFacade appAssessmentFacade;

    public ServerAppsOutdatedChecker(PluginManager pluginManager, MigrationAppAggregatorService appAggregatorService, AppAssessmentFacade appAssessmentFacade) {
        this.pluginManager = pluginManager;
        this.appAggregatorService = appAggregatorService;
        this.appAssessmentFacade = appAssessmentFacade;
    }

    public CheckResult check(ServerAppsOutdatedContext ctx) {
        try {
            List serverAppsOutdated = ctx.appKeys.stream().map(this::getOutdatedServerApp).filter(Optional::isPresent).map(Optional::get).sorted(Comparator.comparing(outdatedApp -> outdatedApp.name)).collect(Collectors.toList());
            return new CheckResult(serverAppsOutdated.isEmpty(), Collections.singletonMap(VIOLATIONS_KEY, serverAppsOutdated));
        }
        catch (Exception e) {
            log.error("Error when checking server apps version outdated.", (Throwable)e);
            return Checker.buildCheckResultWithExecutionError((int)PreflightErrorCode.GENERIC_ERROR.getCode());
        }
    }

    private Optional<ServerAppsOutdatedDto> getOutdatedServerApp(String appKey) {
        try {
            MigrationAppAggregatorResponse response = this.appAggregatorService.getCachedServerAppData(appKey);
            Plugin plugin = this.pluginManager.getPlugin(appKey);
            String serverVersion = plugin.getPluginInformation().getVersion();
            String versionForMigration = response.getCloudMigrationAssistantCompatibility();
            List<MigratabliltyInfo.VersionRange> versionRangeList = response.getCloudMigrationAssistantCompatibilityRangeList();
            if (StringUtils.isNotEmpty((CharSequence)versionForMigration)) {
                boolean needsUpgrade = AppAssessmentFacade.needsUpgrade(response, plugin);
                if (response.getMigrationPath() == MigrationPath.AUTOMATED && needsUpgrade) {
                    ServerAppsOutdatedDto serverAppOutdatedDto = ServerAppsOutdatedDto.builder().name(plugin.getName()).key(plugin.getKey()).version(serverVersion).versionWithMigration(MigratabliltyInfo.Companion.calculateNextMigratableVersion(serverVersion, !versionForMigration.isEmpty() ? versionForMigration : "", versionRangeList)).url(this.appAssessmentFacade.getUpgradeAppUrl()).build();
                    return Optional.of(serverAppOutdatedDto);
                }
            }
            return Optional.empty();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static List<ServerAppsOutdatedDto> retrieveOutdatedServerApps(Map<String, Object> details) {
        return details.getOrDefault(VIOLATIONS_KEY, Collections.emptyList());
    }
}

