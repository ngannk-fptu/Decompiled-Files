/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.Abortable
 */
package software.amazon.awssdk.core.internal.http.timers;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.Abortable;

@SdkInternalApi
public interface TimeoutTracker {
    public boolean hasExecuted();

    public boolean isEnabled();

    public void cancel();

    public void abortable(Abortable var1);
}

