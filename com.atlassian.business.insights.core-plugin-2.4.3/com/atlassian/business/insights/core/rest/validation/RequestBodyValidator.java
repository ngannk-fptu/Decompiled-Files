/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.business.insights.core.rest.validation;

import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface RequestBodyValidator {
    public void validate(@Nullable Object[] var1, @Nonnull ValidationResult var2);
}

