/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemCompatibilityService
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.jdk;

import com.atlassian.confluence.status.service.SystemCompatibilityService;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.confluence.healthcheck.common.Version;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;

public class JdkHealthCheck
implements SupportHealthCheck {
    private final SystemInformationService systemInformationService;
    private final SystemCompatibilityService systemCompatibilityService;
    private final SupportHealthStatusBuilder supportHealthStatusBuilder;

    @Autowired
    public JdkHealthCheck(SupportHealthStatusBuilder supportHealthStatusBuilder, SystemInformationService systemInformationService, SystemCompatibilityService systemCompatibilityService) {
        this.systemInformationService = systemInformationService;
        this.systemCompatibilityService = systemCompatibilityService;
        this.supportHealthStatusBuilder = supportHealthStatusBuilder;
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    @Override
    public SupportHealthStatus check() {
        String jdkRuntime = this.systemInformationService.getSystemProperties().getJavaRuntime();
        Version jdkVersion = new Version(this.systemInformationService.getSystemProperties().getJavaVersion());
        if (!this.getSupportedJavaRuntimes().contains(jdkRuntime)) {
            return this.supportHealthStatusBuilder.critical(this, "confluence.healthcheck.jdk.runtime.fail", new Serializable[]{jdkRuntime});
        }
        if (!this.systemCompatibilityService.getSupportedJavaVersions().contains(jdkVersion.getMajorAndMinor())) {
            return this.supportHealthStatusBuilder.critical(this, "confluence.healthcheck.jdk.version.fail", new Serializable[]{jdkVersion.getFullVersion()});
        }
        return this.supportHealthStatusBuilder.ok(this, "confluence.healthcheck.jdk.valid", new Serializable[]{jdkVersion.getFullVersion(), jdkRuntime});
    }

    private Collection<String> getSupportedJavaRuntimes() {
        try {
            return this.systemCompatibilityService.getSupportedJavaRuntimes();
        }
        catch (NoSuchMethodError e) {
            return Collections.singletonList(this.systemCompatibilityService.getSupportedJavaRuntime());
        }
    }
}

