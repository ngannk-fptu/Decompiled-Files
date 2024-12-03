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
public interface TimeoutTask
extends Runnable {
    default public void abortable(Abortable abortable) {
    }

    default public void cancel() {
    }

    public boolean hasExecuted();
}

