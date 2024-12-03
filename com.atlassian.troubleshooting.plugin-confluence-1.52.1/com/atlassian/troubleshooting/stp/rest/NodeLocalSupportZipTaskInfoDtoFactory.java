/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.rest;

import com.atlassian.troubleshooting.stp.rest.dto.NodeLocalSupportZipTaskInfoDto;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class NodeLocalSupportZipTaskInfoDtoFactory {
    @Nonnull
    public NodeLocalSupportZipTaskInfoDto create(@Nonnull CreateSupportZipMonitor taskMonitor) {
        return this.create(Objects.requireNonNull(taskMonitor), null);
    }

    @Nonnull
    protected NodeLocalSupportZipTaskInfoDto create(@Nonnull CreateSupportZipMonitor taskMonitor, @Nullable Boolean disabledButton) {
        return NodeLocalSupportZipTaskInfoDto.nodeAwareLocalSupportZipInfo(Objects.requireNonNull(taskMonitor), disabledButton);
    }
}

