/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks;

import com.atlassian.troubleshooting.api.healthcheck.FileSystemInfo;
import com.atlassian.troubleshooting.api.healthcheck.LicenseService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import org.springframework.beans.factory.annotation.Autowired;

public class ThreadLimitHealthCheckCondition
implements SupportHealthCheckCondition {
    private final LicenseService licenseService;
    private final FileSystemInfo fileSystemInfo;

    @Autowired
    public ThreadLimitHealthCheckCondition(FileSystemInfo fileSystemInfo, LicenseService licenseService) {
        this.licenseService = licenseService;
        this.fileSystemInfo = fileSystemInfo;
    }

    @Override
    public boolean shouldDisplay() {
        return !this.licenseService.isEvaluation() && this.fileSystemInfo.getThreadLimit().isPresent();
    }
}

