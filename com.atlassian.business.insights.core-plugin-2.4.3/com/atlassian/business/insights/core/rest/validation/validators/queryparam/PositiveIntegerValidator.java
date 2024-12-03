/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.rest.validation.validators.queryparam;

import com.atlassian.business.insights.core.rest.validation.QueryParamValidator;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import javax.annotation.Nonnull;

public abstract class PositiveIntegerValidator
implements QueryParamValidator {
    @Override
    public void validate(@Nonnull String fieldValue, @Nonnull ValidationResult validationResult) {
        int value = 0;
        try {
            value = Integer.parseInt(fieldValue);
        }
        catch (NumberFormatException e) {
            String numberFormatFailureKey = this.getNumberFormatFailureKey();
            validationResult.add(numberFormatFailureKey);
        }
        if (value < 0) {
            String negativeNumberFailureKey = this.getNegativeNumberFailureKey();
            validationResult.add(negativeNumberFailureKey);
        }
    }

    protected abstract String getNumberFormatFailureKey();

    protected abstract String getNegativeNumberFailureKey();
}

