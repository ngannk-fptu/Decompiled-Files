/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.troubleshooting.confluence.healthcheck.license;

import com.atlassian.troubleshooting.api.healthcheck.SoftLaunch;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SoftLaunch
public class LicenseUserLimitHealthCheck
implements SupportHealthCheck {
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;

    @Autowired
    public LicenseUserLimitHealthCheck(SupportHealthStatusBuilder supportHealthStatusBuilder) {
        this.supportHealthStatusBuilder = Objects.requireNonNull(supportHealthStatusBuilder);
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        return this.supportHealthStatusBuilder.ok(this, "Success", new Serializable[0]);
    }
}

