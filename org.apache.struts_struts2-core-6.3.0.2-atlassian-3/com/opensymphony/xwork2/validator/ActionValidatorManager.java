/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.Validator;
import com.opensymphony.xwork2.validator.ValidatorContext;
import java.util.List;

public interface ActionValidatorManager {
    public List<Validator> getValidators(Class var1, String var2, String var3);

    public List<Validator> getValidators(Class var1, String var2);

    public void validate(Object var1, String var2) throws ValidationException;

    public void validate(Object var1, String var2, ValidatorContext var3) throws ValidationException;

    public void validate(Object var1, String var2, String var3) throws ValidationException;

    public void validate(Object var1, String var2, ValidatorContext var3, String var4) throws ValidationException;
}

