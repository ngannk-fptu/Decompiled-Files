/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.utils;

import java.util.stream.Stream;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class ComparableUtils {
    private ComparableUtils() {
    }

    public static <T> int safeCompare(Comparable<T> d1, T d2) {
        if (d1 != null && d2 != null) {
            return d1.compareTo(d2);
        }
        if (d1 == null && d2 != null) {
            return -1;
        }
        if (d1 != null) {
            return 1;
        }
        return 0;
    }

    @SafeVarargs
    public static <T extends Comparable<T>> T minimum(T ... values) {
        return (T)(values == null ? null : (Comparable)Stream.of(values).min(Comparable::compareTo).orElse(null));
    }

    @SafeVarargs
    public static <T extends Comparable<T>> T maximum(T ... values) {
        return (T)(values == null ? null : (Comparable)Stream.of(values).max(Comparable::compareTo).orElse(null));
    }
}

