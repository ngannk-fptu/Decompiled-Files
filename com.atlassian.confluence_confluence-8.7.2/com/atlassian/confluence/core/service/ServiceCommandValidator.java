/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.core.service.FieldValidationError;
import com.atlassian.confluence.core.service.ValidationError;
import java.util.Collection;

public interface ServiceCommandValidator {
    public void addFieldValidationError(String var1, String var2);

    public void addValidationError(String var1, Object ... var2);

    public void addFieldValidationError(String var1, String var2, Object ... var3);

    public void addFieldValidationError(FieldValidationError var1);

    public Collection<ValidationError> getValidationErrors();
}

