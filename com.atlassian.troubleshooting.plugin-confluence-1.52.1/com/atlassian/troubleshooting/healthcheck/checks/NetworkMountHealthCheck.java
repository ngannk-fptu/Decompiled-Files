/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks;

import com.atlassian.troubleshooting.api.healthcheck.FileSystemInfo;
import com.atlassian.troubleshooting.api.healthcheck.IndexInfoService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.IOException;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class NetworkMountHealthCheck
implements SupportHealthCheck {
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkMountHealthCheck.class);
    private final FileSystemInfo fileSystemInfo;
    private final SupportHealthStatusBuilder healthStatusBuilder;
    private final IndexInfoService indexInfo;

    @Autowired
    public NetworkMountHealthCheck(FileSystemInfo fileSystemInfo, IndexInfoService indexInfo, SupportHealthStatusBuilder healthStatusBuilder) {
        this.fileSystemInfo = fileSystemInfo;
        this.indexInfo = indexInfo;
        this.healthStatusBuilder = healthStatusBuilder;
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    @Override
    public SupportHealthStatus check() {
        String indexMount = this.getIndexFileStoreMount();
        if (indexMount.toLowerCase().equals("nfs")) {
            return this.healthStatusBuilder.warning(this, "healthcheck.network.mount.warn", new Serializable[]{this.indexInfo.getIndexRootPath(), indexMount});
        }
        return this.healthStatusBuilder.ok(this, "healthcheck.network.mount.ok", new Serializable[]{this.indexInfo.getIndexRootPath(), indexMount});
    }

    private String getIndexFileStoreMount() {
        try {
            return this.fileSystemInfo.getFileStore(this.indexInfo.getIndexRootPath()).type();
        }
        catch (IOException e) {
            LOGGER.info("Cannot execute health check as index path lookup is failing due to: ", (Throwable)e);
            throw new RuntimeException(e);
        }
    }
}

