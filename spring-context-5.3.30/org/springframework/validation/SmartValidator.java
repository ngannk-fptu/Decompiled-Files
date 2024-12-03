/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.validation;

import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public interface SmartValidator
extends Validator {
    public void validate(Object var1, Errors var2, Object ... var3);

    default public void validateValue(Class<?> targetType, String fieldName, @Nullable Object value, Errors errors, Object ... validationHints) {
        throw new IllegalArgumentException("Cannot validate individual value for " + targetType);
    }
}

