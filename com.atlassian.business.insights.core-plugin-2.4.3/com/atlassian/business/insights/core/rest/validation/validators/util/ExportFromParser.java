/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.core.rest.validation.validators.util;

import com.atlassian.business.insights.core.util.DateConversionUtil;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class ExportFromParser {
    private ExportFromParser() {
    }

    @Nonnull
    public static Optional<Instant> parse(@Nullable String fieldValue) {
        try {
            return Optional.ofNullable(fieldValue).map(DateConversionUtil::parseIsoOffsetDatetime);
        }
        catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}

