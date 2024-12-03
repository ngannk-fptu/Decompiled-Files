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

public class JobIdValidator
implements QueryParamValidator {
    @VisibleForTesting
    static final String BAD_REQUEST_JOB_ID_NEGATIVE = "data-pipeline.api.rest.queryparam.job.id.should.be.positive";
    @VisibleForTesting
    static final String BAD_REQUEST_JOB_ID_NON_NUMERIC = "data-pipeline.api.rest.queryparam.job.id.should.be.numeric";

    @Override
    public void validate(@Nonnull String fieldValue, @Nonnull ValidationResult validationResult) {
        try {
            int jobId = Integer.parseInt(fieldValue);
            if (jobId < 0) {
                validationResult.add(BAD_REQUEST_JOB_ID_NEGATIVE);
            }
        }
        catch (NumberFormatException e) {
            validationResult.add(BAD_REQUEST_JOB_ID_NON_NUMERIC);
        }
    }
}

