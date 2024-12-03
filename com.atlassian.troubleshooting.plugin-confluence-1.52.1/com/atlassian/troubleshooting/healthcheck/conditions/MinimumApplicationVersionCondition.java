/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 */
package com.atlassian.troubleshooting.healthcheck.conditions;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import com.atlassian.troubleshooting.stp.spi.Version;

public class MinimumApplicationVersionCondition
implements SupportHealthCheckCondition {
    final ApplicationProperties applicationProperties;
    final String minimumApplicationVersion;

    public MinimumApplicationVersionCondition(ApplicationProperties applicationProperties, String minimumApplicationVersion) {
        this.applicationProperties = applicationProperties;
        this.minimumApplicationVersion = minimumApplicationVersion;
    }

    @Override
    public boolean shouldDisplay() {
        Version supportedVersion;
        Version currentVersion = Version.of(this.applicationProperties.getVersion());
        return currentVersion.compareTo(supportedVersion = Version.of(this.minimumApplicationVersion)) >= 0;
    }
}

