/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.troubleshooting.stp.request.SupportRequestCreationRequest;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SupportRequestService {
    public static final int DEFAULT_MAX_MEGA_BYTES_PER_FILE = 100;

    @Nonnull
    public TaskMonitor<Void> createSupportRequest(@Nonnull SupportRequestCreationRequest var1);

    @Nullable
    public <T> TaskMonitor<T> getMonitor(@Nonnull String var1);
}

