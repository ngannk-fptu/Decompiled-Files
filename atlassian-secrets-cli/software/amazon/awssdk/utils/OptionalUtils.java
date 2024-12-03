/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.util.Optional;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class OptionalUtils {
    private OptionalUtils() {
    }

    @SafeVarargs
    public static <T> Optional<T> firstPresent(Optional<T> firstValue, Supplier<Optional<T>> ... fallbackValues) {
        if (firstValue.isPresent()) {
            return firstValue;
        }
        for (Supplier<Optional<Optional<T>>> supplier : fallbackValues) {
            Optional<T> fallbackValue = supplier.get();
            if (!fallbackValue.isPresent()) continue;
            return fallbackValue;
        }
        return Optional.empty();
    }

    public static <T> Optional<T> firstPresent(Optional<T> firstValue, Supplier<T> fallbackValue) {
        if (firstValue.isPresent()) {
            return firstValue;
        }
        return Optional.ofNullable(fallbackValue.get());
    }
}

