/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.macro.basic.validator;

import com.atlassian.renderer.v2.macro.basic.validator.MacroParameterValidationException;
import com.atlassian.renderer.v2.macro.basic.validator.ParameterValidator;
import java.util.regex.Pattern;

public class WidthSizeValidator
implements ParameterValidator {
    private static final Pattern WIDTH_SIZE_PATTERN = Pattern.compile("(\\d+)\\s*(px|%)?");
    private static final WidthSizeValidator INSTANCE = new WidthSizeValidator();

    private WidthSizeValidator() {
    }

    public static WidthSizeValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public void assertValid(String parameterValue) throws MacroParameterValidationException {
        if (!WIDTH_SIZE_PATTERN.matcher(parameterValue).matches()) {
            throw new MacroParameterValidationException("Width parameter must be a number (optionally followed by 'px' or '%').");
        }
    }
}

