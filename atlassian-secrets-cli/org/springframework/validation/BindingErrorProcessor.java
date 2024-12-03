/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation;

import org.springframework.beans.PropertyAccessException;
import org.springframework.validation.BindingResult;

public interface BindingErrorProcessor {
    public void processMissingFieldError(String var1, BindingResult var2);

    public void processPropertyAccessException(PropertyAccessException var1, BindingResult var2);
}

