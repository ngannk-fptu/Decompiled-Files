/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.util;

import java.util.Optional;
import java.util.function.Supplier;

public class OptionalUtils {
    public static <T> Optional<T> orElseGetOptional(Optional<T> optional, Supplier<Optional<T>> supplier) {
        return optional.isPresent() ? optional : supplier.get();
    }
}

