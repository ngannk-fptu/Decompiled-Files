/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.confluence.api.model.longtasks.LongTaskStatus
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.longrunning;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.api.model.longtasks.LongTaskStatus;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.confluence.util.longrunning.LongRunningTaskManager;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.user.User;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface LongRunningTaskManagerInternal
extends LongRunningTaskManager {
    public LongRunningTaskId queueLongRunningTask(LongRunningTask var1);

    public void startIfQueued(LongRunningTaskId var1);

    public PageResponse<LongTaskStatus> getAllTasks(@Nullable ConfluenceUser var1, LimitedRequest var2);

    public void runToCompletion(@Nullable User var1, LongRunningTask var2);

    public int getTaskCount();
}

