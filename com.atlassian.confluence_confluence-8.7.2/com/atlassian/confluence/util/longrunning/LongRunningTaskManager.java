/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.confluence.api.model.longtasks.LongTaskStatus
 *  com.atlassian.core.task.longrunning.LongRunningTask
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.NotImplementedException
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.util.longrunning;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.api.model.longtasks.LongTaskStatus;
import com.atlassian.confluence.util.longrunning.LongRunningTaskId;
import com.atlassian.core.task.longrunning.LongRunningTask;
import com.atlassian.user.User;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public interface LongRunningTaskManager {
    public LongRunningTaskId startLongRunningTask(@Nullable User var1, LongRunningTask var2);

    public @Nullable LongRunningTask getLongRunningTask(@Nullable User var1, LongRunningTaskId var2);

    public void stopTrackingLongRunningTask(LongRunningTaskId var1);

    public void stop(long var1, TimeUnit var3) throws TimeoutException;

    public void resume();

    default public List<LongTaskStatus> removeComplete() {
        throw new NotImplementedException("Method LongRunningTaskManager.removeComplete() is not implemented");
    }
}

