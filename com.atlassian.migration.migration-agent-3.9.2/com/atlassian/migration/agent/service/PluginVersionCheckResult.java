/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service;

import java.time.Instant;

public class PluginVersionCheckResult {
    private Result result;
    private Instant upgradeBy;

    public PluginVersionCheckResult(Result result, Instant upgradeBy) {
        this.result = result;
        this.upgradeBy = upgradeBy;
    }

    public Result getResult() {
        return this.result;
    }

    public Instant getUpgradeBy() {
        return this.upgradeBy;
    }

    public static enum Result {
        OUTDATED,
        LATEST,
        WITHIN_GRACE;

    }
}

