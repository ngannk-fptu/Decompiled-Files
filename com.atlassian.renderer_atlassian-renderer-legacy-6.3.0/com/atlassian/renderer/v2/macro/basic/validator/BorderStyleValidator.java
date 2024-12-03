/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2.macro.basic.validator;

import com.atlassian.renderer.v2.macro.basic.validator.MacroParameterValidationException;
import com.atlassian.renderer.v2.macro.basic.validator.ParameterValidator;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class BorderStyleValidator
implements ParameterValidator {
    private static final Set VALID_VALUES = BorderStyleValidator.createBorderStyleValues();
    private static final BorderStyleValidator INSTANCE = new BorderStyleValidator();

    private BorderStyleValidator() {
    }

    public static BorderStyleValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public void assertValid(String propertyValue) throws MacroParameterValidationException {
        if (StringUtils.isBlank((String)propertyValue)) {
            return;
        }
        if (!VALID_VALUES.contains(propertyValue)) {
            throw new MacroParameterValidationException("Border style is not a valid CSS2 border-style value");
        }
    }

    private static Set createBorderStyleValues() {
        HashSet<String> strings = new HashSet<String>();
        strings.add("none");
        strings.add("hidden");
        strings.add("dotted");
        strings.add("dashed");
        strings.add("solid");
        strings.add("double");
        strings.add("groove");
        strings.add("ridge");
        strings.add("inset");
        strings.add("outset");
        return strings;
    }
}

