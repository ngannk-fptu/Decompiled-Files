/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
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

