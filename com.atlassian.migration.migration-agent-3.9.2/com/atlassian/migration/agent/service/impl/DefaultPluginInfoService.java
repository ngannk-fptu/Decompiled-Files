/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.agent.entity.ExecutionStatus;
import com.atlassian.migration.agent.service.PlanService;
import com.atlassian.migration.agent.service.PluginInfoService;
import com.atlassian.migration.agent.service.PluginVersionCheckResult;
import com.atlassian.migration.agent.service.version.PluginVersionInfo;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

public class DefaultPluginInfoService
implements PluginInfoService {
    private final PluginVersionManager pluginVersionManager;
    private final PlanService planService;

    public DefaultPluginInfoService(PluginVersionManager pluginVersionManager, PlanService planService) {
        this.pluginVersionManager = pluginVersionManager;
        this.planService = planService;
    }

    @Override
    public PluginVersionCheckResult checkPluginVersion(String cloudId) {
        Optional<PluginVersionInfo> versionInfo = this.pluginVersionManager.getPluginVersionInfo(cloudId);
        if (versionInfo.isPresent()) {
            PluginVersionInfo info = versionInfo.get();
            return new PluginVersionCheckResult(this.getResult(info), this.getUpgradeBy(info));
        }
        return new PluginVersionCheckResult(PluginVersionCheckResult.Result.LATEST, null);
    }

    private Instant getUpgradeBy(PluginVersionInfo info) {
        if (info.getUpgradeBy() != null) {
            return info.getUpgradeBy().atStartOfDay(ZoneId.of("UTC")).toInstant();
        }
        return null;
    }

    private PluginVersionCheckResult.Result getResult(PluginVersionInfo versionInfo) {
        boolean isOutdated;
        boolean bl = isOutdated = versionInfo.isOutdated() != false && !this.planService.hasPlans(ExecutionStatus.RUNNING);
        if (isOutdated) {
            return PluginVersionCheckResult.Result.OUTDATED;
        }
        if (versionInfo.getUpgradeBy() != null) {
            return PluginVersionCheckResult.Result.WITHIN_GRACE;
        }
        return PluginVersionCheckResult.Result.LATEST;
    }
}

