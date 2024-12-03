/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http.timers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTracker;
import software.amazon.awssdk.http.Abortable;

@SdkInternalApi
public final class NoOpTimeoutTracker
implements TimeoutTracker {
    public static final NoOpTimeoutTracker INSTANCE = new NoOpTimeoutTracker();

    private NoOpTimeoutTracker() {
    }

    @Override
    public boolean hasExecuted() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void cancel() {
    }

    @Override
    public void abortable(Abortable abortable) {
    }
}

