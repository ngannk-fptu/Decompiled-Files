/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.troubleshooting.api.healthcheck.Application;
import com.atlassian.troubleshooting.api.healthcheck.FileSystemInfo;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

public class ThreadLimitHealthCheck
implements SupportHealthCheck {
    private static final int MIN_THREAD_LIMIT = 4096;
    private final SupportHealthStatusBuilder healthStatusBuilder;
    private final FileSystemInfo fileSystemInfo;
    private final Application application;

    @Autowired
    public ThreadLimitHealthCheck(FileSystemInfo fileSystemInfo, ApplicationProperties properties, SupportHealthStatusBuilder healthStatusBuilder) {
        this.healthStatusBuilder = healthStatusBuilder;
        this.application = Application.byAppDisplayName(properties.getDisplayName());
        this.fileSystemInfo = fileSystemInfo;
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    @Override
    public SupportHealthStatus check() {
        Optional<FileSystemInfo.ThreadLimit> threadLimit = this.fileSystemInfo.getThreadLimit();
        if (threadLimit.isPresent()) {
            if (threadLimit.get().greaterThanOrEqualTo(4096)) {
                return this.healthStatusBuilder.ok(this, "healthcheck.threadlimit.valid", new Serializable[]{this.application.name(), Integer.valueOf(4096)});
            }
            return this.healthStatusBuilder.warning(this, "healthcheck.threadlimit.fail", new Serializable[]{this.application.name(), threadLimit.get().toString(), Integer.valueOf(4096)});
        }
        return this.healthStatusBuilder.ok(this, "healthcheck.threadlimit.undefined", new Serializable[]{this.application.name(), Integer.valueOf(4096)});
    }
}

