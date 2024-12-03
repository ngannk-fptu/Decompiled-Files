/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation;

import org.springframework.validation.Errors;

public interface Validator {
    public boolean supports(Class<?> var1);

    public void validate(Object var1, Errors var2);
}

