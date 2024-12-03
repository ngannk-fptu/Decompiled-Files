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
import com.atlassian.business.insights.core.rest.validation.validators.util.SchemaVersionValueParser;
import javax.annotation.Nonnull;

public class SchemaVersionValidator
implements QueryParamValidator {
    @VisibleForTesting
    public static final String BAD_REQUEST_INVALID_SCHEMA_VERSION_TYPE_KEY = "data-pipeline.api.rest.queryparam.schemaversion.invalid.should.be.supported.integer.schemaversion";

    @Override
    public void validate(@Nonnull String fieldValue, @Nonnull ValidationResult validationResult) {
        if (SchemaVersionValueParser.isNotParsable(fieldValue)) {
            validationResult.add(BAD_REQUEST_INVALID_SCHEMA_VERSION_TYPE_KEY);
        }
    }
}

