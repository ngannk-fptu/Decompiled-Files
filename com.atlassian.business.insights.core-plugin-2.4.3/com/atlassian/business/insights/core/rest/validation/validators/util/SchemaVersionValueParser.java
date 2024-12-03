/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.rest.validation.validators.util;

import java.util.Optional;
import javax.annotation.Nonnull;

public final class SchemaVersionValueParser {
    private SchemaVersionValueParser() {
    }

    @Nonnull
    public static Optional<Integer> parse(@Nonnull String value) {
        try {
            return Optional.of(value).map(Integer::parseInt);
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static boolean isNotParsable(@Nonnull String value) {
        return !SchemaVersionValueParser.parse(value).isPresent();
    }
}

