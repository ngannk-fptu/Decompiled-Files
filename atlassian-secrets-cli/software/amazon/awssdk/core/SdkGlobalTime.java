/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.core;

import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class SdkGlobalTime {
    private static volatile int globalTimeOffset;

    private SdkGlobalTime() {
    }

    public static int getGlobalTimeOffset() {
        return globalTimeOffset;
    }

    public static void setGlobalTimeOffset(int timeOffset) {
        globalTimeOffset = timeOffset;
    }
}

