/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm;

import java.util.Optional;
import java.util.stream.Stream;

public class Optionals {
    @SafeVarargs
    public static <T> Optional<T> or(Optional<T> ... orderedOptions) {
        return Stream.of(orderedOptions).filter(Optional::isPresent).findFirst().map(Optional::get);
    }
}

