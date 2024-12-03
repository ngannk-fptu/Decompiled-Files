/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.ValidatorContext;

public interface Validator<T> {
    public void setDefaultMessage(String var1);

    public String getDefaultMessage();

    public String getMessage(Object var1);

    public void setMessageKey(String var1);

    public String getMessageKey();

    public void setMessageParameters(String[] var1);

    public String[] getMessageParameters();

    public void setValidatorContext(ValidatorContext var1);

    public ValidatorContext getValidatorContext();

    public void validate(Object var1) throws ValidationException;

    public void setValidatorType(String var1);

    public String getValidatorType();

    public void setValueStack(ValueStack var1);
}

