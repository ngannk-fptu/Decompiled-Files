/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.rest.validation;

import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import javax.annotation.Nonnull;

public interface QueryParamValidator {
    public void validate(@Nonnull String var1, @Nonnull ValidationResult var2);
}

