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
import javax.annotation.Nonnull;

public class ForceExportValidator
implements QueryParamValidator {
    @VisibleForTesting
    public static final String BAD_REQUEST_INVALID_FORCE_EXPORT_KEY = "data-pipeline.api.rest.queryparam.forceexport.invalid.should.be.boolean";

    @Override
    public void validate(@Nonnull String fieldValue, @Nonnull ValidationResult validationResult) {
        if (!"true".equals(fieldValue) && !"false".equals(fieldValue)) {
            validationResult.add(BAD_REQUEST_INVALID_FORCE_EXPORT_KEY);
        }
    }
}

