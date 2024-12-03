/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.business.insights.core.rest.validation.validators.queryparam;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.rest.validation.validators.queryparam.PositiveIntegerValidator;

public class PageLimitValidator
extends PositiveIntegerValidator {
    @VisibleForTesting
    static final String BAD_REQUEST_PAGE_LIMIT_NOT_NUMERIC_KEY = "data-pipeline.api.rest.queryparam.page.limit.should.be.numeric";
    @VisibleForTesting
    static final String BAD_REQUEST_PAGE_LIMIT_NEGATIVE_KEY = "data-pipeline.api.rest.queryparam.page.limit.should.be.positive";

    @Override
    protected String getNumberFormatFailureKey() {
        return BAD_REQUEST_PAGE_LIMIT_NOT_NUMERIC_KEY;
    }

    @Override
    protected String getNegativeNumberFailureKey() {
        return BAD_REQUEST_PAGE_LIMIT_NEGATIVE_KEY;
    }
}

