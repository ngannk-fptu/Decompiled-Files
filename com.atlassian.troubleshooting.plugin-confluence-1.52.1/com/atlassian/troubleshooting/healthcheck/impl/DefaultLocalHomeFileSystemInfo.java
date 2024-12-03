/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.impl;

import com.atlassian.troubleshooting.api.healthcheck.LocalHomeFileSystemInfo;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultLocalHomeFileSystemInfo
implements LocalHomeFileSystemInfo {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultLocalHomeFileSystemInfo.class);
    private static final int DEFAULT_RECOMMENDED_PERCENTAGE = 10;
    private static final int DEFAULT_RECOMMENDED_GB = 10;
    private final SupportApplicationInfo supportApplicationInfo;

    @Autowired
    public DefaultLocalHomeFileSystemInfo(SupportApplicationInfo supportApplicationInfo) {
        this.supportApplicationInfo = supportApplicationInfo;
    }

    @Override
    public Path getLocalApplicationHomePath() {
        String localHomeDirectory = this.supportApplicationInfo.getLocalApplicationHome();
        return Paths.get(localHomeDirectory, new String[0]);
    }

    @Override
    public FileStore getLocalHomeFileStore() throws IOException {
        Path localHomePath = this.getLocalApplicationHomePath();
        return Files.getFileStore(localHomePath);
    }

    @Override
    public int getRecommendedThresholdPercentage() {
        try {
            return Integer.parseInt(System.getProperty("troubleshooting.healthcheck.minimum.freespace.percentage"));
        }
        catch (Exception e) {
            LOG.info(String.format("The recommended threshold for the local home free space healthcheck was not set so it will use %d %% as a default", 10), (Throwable)e);
            return 10;
        }
    }

    @Override
    public long getRecommendedThresholdGB() {
        try {
            return Long.parseLong(System.getProperty("troubleshooting.healthcheck.minimum.freespace.gb"));
        }
        catch (Exception e) {
            LOG.info(String.format("The recommended threshold for the local home free space healthcheck was not set so it will use %d GB as a default", 10), (Throwable)e);
            return 10L;
        }
    }
}

