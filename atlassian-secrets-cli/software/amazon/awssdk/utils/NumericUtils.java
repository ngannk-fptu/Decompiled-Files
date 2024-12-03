/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.time.Duration;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class NumericUtils {
    private NumericUtils() {
    }

    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int)value;
    }

    public static Duration min(Duration a, Duration b) {
        return a.compareTo(b) < 0 ? a : b;
    }

    public static Duration max(Duration a, Duration b) {
        return a.compareTo(b) > 0 ? a : b;
    }
}

