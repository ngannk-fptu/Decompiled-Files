/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.cmpt.check.base.Checker
 */
package com.atlassian.migration.agent.service.check.version;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.cmpt.check.base.Checker;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.service.check.version.ConfluenceSupportedVersionCheckContext;
import com.atlassian.migration.agent.service.version.ConfluenceServerVersion;

public class ConfluenceSupportedVersionChecker
implements Checker<ConfluenceSupportedVersionCheckContext> {
    private final MigrationAgentConfiguration configuration;

    public ConfluenceSupportedVersionChecker(MigrationAgentConfiguration configuration) {
        this.configuration = configuration;
    }

    public CheckResult check(ConfluenceSupportedVersionCheckContext ctx) {
        ConfluenceServerVersion version = ConfluenceServerVersion.of(ctx.confluenceVersion);
        String minConfluenceSupportedVersion = this.configuration.getMinConfluenceSupportedVersion();
        return new CheckResult(version.greaterOrEqual(minConfluenceSupportedVersion));
    }
}

