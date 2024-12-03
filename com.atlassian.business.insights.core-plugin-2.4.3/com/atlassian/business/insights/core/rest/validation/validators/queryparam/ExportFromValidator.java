/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.rest.validation.validators.queryparam;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.rest.validation.QueryParamValidator;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import com.atlassian.business.insights.core.rest.validation.validators.util.ExportFromParser;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nonnull;

public class ExportFromValidator
implements QueryParamValidator {
    @VisibleForTesting
    public static final String BAD_REQUEST_FUTURE_FROM_DATE_KEY = "data-pipeline.api.rest.queryparam.fromdate.should.not.be.future";
    @VisibleForTesting
    public static final String BAD_REQUEST_INVALID_FROM_DATE_KEY = "data-pipeline.api.rest.queryparam.fromdate.invalid.should.be.iso.offset.datetime";

    @Override
    public void validate(@Nonnull String fieldValue, @Nonnull ValidationResult validationResult) {
        Optional<Instant> instant = ExportFromParser.parse(fieldValue);
        if (instant.isPresent()) {
            if (instant.get().isAfter(Instant.now())) {
                validationResult.add(BAD_REQUEST_FUTURE_FROM_DATE_KEY);
            }
        } else {
            validationResult.add(BAD_REQUEST_INVALID_FROM_DATE_KEY);
        }
    }
}

