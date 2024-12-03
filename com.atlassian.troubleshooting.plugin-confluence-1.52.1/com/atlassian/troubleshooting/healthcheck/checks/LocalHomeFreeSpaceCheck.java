/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.healthcheck.checks;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.troubleshooting.api.healthcheck.LocalHomeFileSystemInfo;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import com.atlassian.troubleshooting.healthcheck.checks.analytic.LocalHomeFreeSpaceEvent;
import com.atlassian.troubleshooting.util.UnitConverterUtil;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalHomeFreeSpaceCheck
implements SupportHealthCheck {
    private static final Logger LOG = LoggerFactory.getLogger(LocalHomeFreeSpaceCheck.class);
    private static final String CHECK_KEY = "healthcheck.freespace.localhome";
    private final SupportHealthStatusBuilder healthStatusBuilder;
    private final LocalHomeFileSystemInfo localHomeFileSystemInfo;
    private final EventPublisher eventPublisher;

    public LocalHomeFreeSpaceCheck(SupportHealthStatusBuilder healthStatusBuilder, LocalHomeFileSystemInfo localHomeFileSystemInfo, EventPublisher eventPublisher) {
        this.healthStatusBuilder = healthStatusBuilder;
        this.localHomeFileSystemInfo = localHomeFileSystemInfo;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean isNodeSpecific() {
        return true;
    }

    @Override
    public SupportHealthStatus check() {
        int recommendFreeSpacePercentage = this.localHomeFileSystemInfo.getRecommendedThresholdPercentage();
        long recommendFreeSpaceBytes = this.localHomeFileSystemInfo.getRecommendedThresholdGB() * 1000L * 1000L * 1000L;
        try {
            FileStore localHomeFs = this.localHomeFileSystemInfo.getLocalHomeFileStore();
            long freeSpaceBytes = localHomeFs.getUsableSpace();
            long totalSpaceBytes = localHomeFs.getTotalSpace();
            if (totalSpaceBytes <= 0L) {
                LOG.warn(this.warningMessage("The check detects the system has 0kB total disk space. Please perform a manual check to verify there is disk space to run the application."));
                return this.getOkUnsure();
            }
            this.sendDiskSpaceAnalytic(freeSpaceBytes, totalSpaceBytes, recommendFreeSpacePercentage, this.localHomeFileSystemInfo.getRecommendedThresholdGB());
            long freespacePercentage = freeSpaceBytes * 100L / totalSpaceBytes;
            if (freespacePercentage < (long)recommendFreeSpacePercentage && freeSpaceBytes < recommendFreeSpaceBytes) {
                return this.getWarning(UnitConverterUtil.humanReadableByteCount(freeSpaceBytes, true), localHomeFs.toString());
            }
            return this.getOk(UnitConverterUtil.humanReadableByteCount(freeSpaceBytes, true), localHomeFs.toString());
        }
        catch (IOException e) {
            LOG.warn(this.warningMessage(e.getMessage()), (Throwable)e);
            return this.getOkUnsure();
        }
    }

    private void sendDiskSpaceAnalytic(long freeSpaceBytes, long totalSpaceBytes, int recommendFreeSpacePercentage, long recommendFreeSpaceGB) {
        this.eventPublisher.publish((Object)new LocalHomeFreeSpaceEvent(freeSpaceBytes, totalSpaceBytes, recommendFreeSpacePercentage, recommendFreeSpaceGB));
    }

    private SupportHealthStatus getOk(String freeSpaceWithUnit, String fileStoreName) {
        return this.healthStatusBuilder.ok(this, "healthcheck.freespace.localhome.ok", new Serializable[]{freeSpaceWithUnit, fileStoreName});
    }

    private SupportHealthStatus getWarning(String freeSpaceWithUnit, String fileStoreName) {
        return this.healthStatusBuilder.warning(this, "healthcheck.freespace.localhome.warn", new Serializable[]{freeSpaceWithUnit, fileStoreName});
    }

    private SupportHealthStatus getOkUnsure() {
        return this.healthStatusBuilder.ok(this, "healthcheck.freespace.localhome.notsure", new Serializable[0]);
    }

    private String warningMessage(String errorMessage) {
        return String.format("ATST Instant Health: Local home free space healthcheck cannot verify the free space in the partition where application home(%s) lives. Please perform manual check to verify that you have enough free space for application to run. Error: %s", this.localHomeFileSystemInfo.getLocalApplicationHomePath().toString(), errorMessage);
    }
}

