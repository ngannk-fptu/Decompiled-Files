/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2.macro.basic.validator;

import com.atlassian.renderer.v2.macro.basic.CssSizeValue;
import com.atlassian.renderer.v2.macro.basic.validator.MacroParameterValidationException;
import com.atlassian.renderer.v2.macro.basic.validator.ParameterValidator;
import org.apache.commons.lang.StringUtils;

public class CssSizeValidator
implements ParameterValidator {
    private static final CssSizeValidator INSTANCE = new CssSizeValidator();

    private CssSizeValidator() {
    }

    public static CssSizeValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public void assertValid(String propertyValue) throws MacroParameterValidationException {
        if (StringUtils.isBlank((String)propertyValue)) {
            return;
        }
        CssSizeValue sizeValue = new CssSizeValue(propertyValue);
        if (!sizeValue.isValid()) {
            throw new MacroParameterValidationException("Size parameter must be a number (optionally followed by 'px', 'pt' or 'em').");
        }
    }
}

