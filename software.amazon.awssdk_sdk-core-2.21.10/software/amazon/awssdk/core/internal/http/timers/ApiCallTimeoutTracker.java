/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.Abortable
 *  software.amazon.awssdk.utils.Validate
 */
package software.amazon.awssdk.core.internal.http.timers;

import java.util.concurrent.ScheduledFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTask;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTracker;
import software.amazon.awssdk.http.Abortable;
import software.amazon.awssdk.utils.Validate;

@SdkInternalApi
public final class ApiCallTimeoutTracker
implements TimeoutTracker {
    private final TimeoutTask timeoutTask;
    private final ScheduledFuture<?> future;

    public ApiCallTimeoutTracker(TimeoutTask timeout, ScheduledFuture<?> future) {
        this.timeoutTask = (TimeoutTask)Validate.paramNotNull((Object)timeout, (String)"timeoutTask");
        this.future = (ScheduledFuture)Validate.paramNotNull(future, (String)"scheduledFuture");
    }

    @Override
    public boolean hasExecuted() {
        return this.timeoutTask.hasExecuted();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void cancel() {
        this.future.cancel(false);
        this.timeoutTask.cancel();
    }

    @Override
    public void abortable(Abortable abortable) {
        this.timeoutTask.abortable(abortable);
    }
}

