/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.factory;

import com.opensymphony.xwork2.validator.Validator;
import java.util.Map;

public interface ValidatorFactory {
    public Validator buildValidator(String var1, Map<String, Object> var2, Map<String, Object> var3) throws Exception;
}

