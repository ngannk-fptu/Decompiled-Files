/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 */
package com.atlassian.business.insights.core.rest.validation.validators.queryparam;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.rest.validation.validators.queryparam.PositiveIntegerValidator;

public class PageOffsetValidator
extends PositiveIntegerValidator {
    @VisibleForTesting
    static final String BAD_REQUEST_PAGE_OFFSET_NOT_NUMERIC_KEY = "data-pipeline.api.rest.queryparam.page.offset.should.be.numeric";
    @VisibleForTesting
    static final String BAD_REQUEST_PAGE_OFFSET_NEGATIVE_KEY = "data-pipeline.api.rest.queryparam.page.offset.should.be.positive";

    @Override
    protected String getNumberFormatFailureKey() {
        return BAD_REQUEST_PAGE_OFFSET_NOT_NUMERIC_KEY;
    }

    @Override
    protected String getNegativeNumberFailureKey() {
        return BAD_REQUEST_PAGE_OFFSET_NEGATIVE_KEY;
    }
}

