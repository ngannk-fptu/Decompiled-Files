/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.filesystem;

import com.atlassian.troubleshooting.api.healthcheck.OperatingSystemInfo;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenFilesLimitCheck
implements SupportHealthCheck {
    private final OperatingSystemInfo operatingSystemInfo;
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;

    @Autowired
    public OpenFilesLimitCheck(OperatingSystemInfo operatingSystemInfo, SupportHealthStatusBuilder supportHealthStatusBuilder) {
        this.operatingSystemInfo = operatingSystemInfo;
        this.supportHealthStatusBuilder = supportHealthStatusBuilder;
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    @Override
    public SupportHealthStatus check() {
        long maxFiles = this.operatingSystemInfo.getMaxFileDescriptorCount();
        long openFiles = this.operatingSystemInfo.getOpenFileDescriptorCount();
        float proportion = (float)openFiles / (float)maxFiles;
        float percentage = proportion * 100.0f;
        if (percentage >= 90.0f) {
            return this.supportHealthStatusBuilder.major(this, "confluence.healthcheck.openfiles.major", Long.valueOf(openFiles), Long.valueOf(maxFiles));
        }
        if (percentage >= 70.0f) {
            return this.supportHealthStatusBuilder.warning(this, "confluence.healthcheck.openfiles.warning", Long.valueOf(openFiles), Long.valueOf(maxFiles));
        }
        return this.supportHealthStatusBuilder.ok(this, "confluence.healthcheck.openfiles.ok", Long.valueOf(openFiles), Long.valueOf(maxFiles));
    }
}

