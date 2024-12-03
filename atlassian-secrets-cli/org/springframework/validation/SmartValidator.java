/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.validation;

import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public interface SmartValidator
extends Validator {
    public void validate(@Nullable Object var1, Errors var2, Object ... var3);
}

