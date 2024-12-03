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
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class ColorStyleValidator
implements ParameterValidator {
    public static final Pattern NAMED_COLOR_PATTERN = Pattern.compile("\\w+");
    public static final Pattern HEX_SHORT_COLOR_PATTERN = Pattern.compile("^#([\\da-fA-F]){3}$");
    public static final Pattern HEX_LONG_COLOR_MATCH = Pattern.compile("^#([\\da-fA-F]){6}$");
    public static final Pattern RGB_COLOR_PATTERN = Pattern.compile("^#rgb\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)$");
    public static final Pattern PERC_COLOR_PATTERN = Pattern.compile("^#rgb\\((\\d+)%,\\s*(\\d+)%,\\s*(\\d+)%\\)$");
    private static final ColorStyleValidator INSTANCE = new ColorStyleValidator();

    private ColorStyleValidator() {
    }

    public static ColorStyleValidator getInstance() {
        return INSTANCE;
    }

    @Override
    public void assertValid(String propertyValue) throws MacroParameterValidationException {
        if (StringUtils.isBlank((String)propertyValue)) {
            return;
        }
        if (NAMED_COLOR_PATTERN.matcher(propertyValue).matches() || HEX_SHORT_COLOR_PATTERN.matcher(propertyValue).matches() || HEX_LONG_COLOR_MATCH.matcher(propertyValue).matches() || RGB_COLOR_PATTERN.matcher(propertyValue).matches() || PERC_COLOR_PATTERN.matcher(propertyValue).matches()) {
            return;
        }
        throw new MacroParameterValidationException("Color value is invalid");
    }

    private static Set createColorValues() {
        HashSet<String> strings = new HashSet<String>();
        strings.add("aqua");
        strings.add("black");
        strings.add("blue");
        strings.add("fuchsia");
        strings.add("gray");
        strings.add("green");
        strings.add("lime");
        strings.add("maroon");
        strings.add("navy");
        strings.add("olive");
        strings.add("orange");
        strings.add("purple");
        strings.add("red");
        strings.add("silver");
        strings.add("teal;");
        strings.add("white");
        strings.add("yellow");
        return strings;
    }
}

