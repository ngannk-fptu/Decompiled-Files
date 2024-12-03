/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.validator.Validator;
import com.opensymphony.xwork2.validator.ValidatorConfig;

public interface ValidatorFactory {
    public Validator getValidator(ValidatorConfig var1);

    public void registerValidator(String var1, String var2);

    public String lookupRegisteredValidatorType(String var1);
}

