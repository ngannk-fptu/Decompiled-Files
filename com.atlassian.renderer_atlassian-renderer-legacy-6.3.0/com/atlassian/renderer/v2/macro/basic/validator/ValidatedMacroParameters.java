/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.basic.validator;

import com.atlassian.renderer.v2.macro.basic.validator.MacroParameterValidationException;
import com.atlassian.renderer.v2.macro.basic.validator.ParameterValidator;
import java.util.HashMap;
import java.util.Map;

public class ValidatedMacroParameters {
    private final Map<String, String> parameters;
    private final Map<String, ParameterValidator> validators = new HashMap<String, ParameterValidator>();

    public ValidatedMacroParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void setValidator(String parameterName, ParameterValidator parameterValidator) {
        this.validators.put(parameterName, parameterValidator);
    }

    public String getValue(String parameterName) throws MacroParameterValidationException {
        String parameterValue = this.parameters.get(parameterName);
        if (parameterValue == null) {
            return null;
        }
        ParameterValidator validator = this.validators.get(parameterName);
        if (validator == null) {
            return parameterValue;
        }
        validator.assertValid(parameterValue);
        return parameterValue;
    }
}

