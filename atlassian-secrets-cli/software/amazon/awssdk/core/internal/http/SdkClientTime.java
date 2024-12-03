/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core.internal.http;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.SdkGlobalTime;

@SdkInternalApi
public final class SdkClientTime {
    private volatile int timeOffset = SdkGlobalTime.getGlobalTimeOffset();

    public int getTimeOffset() {
        return this.timeOffset;
    }

    public void setTimeOffset(int timeOffset) {
        this.timeOffset = timeOffset;
        SdkGlobalTime.setGlobalTimeOffset(timeOffset);
    }
}

