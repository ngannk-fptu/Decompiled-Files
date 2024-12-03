/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.java.ao.util;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class StreamUtils {
    public static <T> Stream<T> ofNullable(@Nullable T t) {
        return StreamUtils.ofOptional(Optional.ofNullable(t));
    }

    private static <T> Stream<T> ofOptional(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }
}

