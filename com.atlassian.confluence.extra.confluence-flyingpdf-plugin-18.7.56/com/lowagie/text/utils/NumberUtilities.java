/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.lowagie.text.utils;

import java.util.Optional;
import javax.annotation.Nonnull;

public final class NumberUtilities {
    private NumberUtilities() {
    }

    @Nonnull
    public static Optional<Float> parseFloat(String value) {
        try {
            return Optional.of(Float.valueOf(Float.parseFloat(value)));
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    public static Optional<Integer> parseInt(String value) {
        try {
            return Optional.of(Integer.parseInt(value));
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}

