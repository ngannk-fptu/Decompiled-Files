/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.healthcheck.checks.eol;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="healthchecks.check.endOfLife.version.missing")
public class EolCheckVersionMissingEvent {
    private final String marketplaceName;
    private final String versionName;

    public EolCheckVersionMissingEvent(String marketplaceName, String versionName) {
        this.marketplaceName = marketplaceName;
        this.versionName = versionName;
    }

    public String getVersionName() {
        return this.versionName;
    }

    public String getMarketplaceName() {
        return this.marketplaceName;
    }
}

