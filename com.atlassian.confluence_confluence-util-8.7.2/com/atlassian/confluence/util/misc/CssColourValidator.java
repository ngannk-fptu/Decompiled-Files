/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.validator.ValidationException
 *  com.opensymphony.xwork2.validator.validators.FieldValidatorSupport
 */
package com.atlassian.confluence.util.misc;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;
import java.util.regex.Pattern;

public class CssColourValidator
extends FieldValidatorSupport {
    private static final Pattern WORD_MATCH = Pattern.compile("^[a-zA-Z]\\w*$");
    private static final Pattern SHORT_NUMERIC_MATCH = Pattern.compile("^#([\\da-fA-F]){3}$");
    private static final Pattern LONG_NUMERIC_MATCH = Pattern.compile("^#([\\da-fA-F]){6}$");
    private static final Pattern RGB_MATCH = Pattern.compile("^rgb\\(\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])\\s*,\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])\\s*,\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])\\s*\\)$");
    private static final Pattern RGBA_MATCH = Pattern.compile("^rgba\\(\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])\\s*,\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])\\s*,\\s*(0|[1-9]\\d?|1\\d\\d?|2[0-4]\\d|25[0-5])\\s*,\\s*((0.[0-9]*)|[01])\\s*\\)$");
    private static final Pattern PERC_MATCH = Pattern.compile("^rgb\\((\\d+)%,\\s*(\\d+)%,\\s*(\\d+)%\\)$");
    private static final Pattern HSL_MATCH = Pattern.compile("^hsl\\(\\s*(0|[1-9]\\d?|[12]\\d\\d|3[0-5]\\d)\\s*,\\s*((0|[1-9]\\d?|100)%)\\s*,\\s*((0|[1-9]\\d?|100)%)\\s*\\)$");
    private static final Pattern HSLA_MATCH = Pattern.compile("^hsla\\(\\s*(0|[1-9]\\d?|[12]\\d\\d|3[0-5]\\d)\\s*,\\s*((0|[1-9]\\d?|100)%)\\s*,\\s*((0|[1-9]\\d?|100)%)\\s*,\\s*((0.[0-9]*)|[01])\\s*\\)$");

    public void validate(Object object) throws ValidationException {
        String fieldValue = ((String)this.getFieldValue(this.getFieldName(), object)).trim();
        if (!CssColourValidator.check(fieldValue)) {
            this.addFieldError(this.getFieldName(), object);
        }
    }

    public static boolean check(String thingToCheck) {
        if (thingToCheck == null) {
            return true;
        }
        return (thingToCheck = thingToCheck.trim()).isEmpty() || WORD_MATCH.matcher(thingToCheck).matches() || SHORT_NUMERIC_MATCH.matcher(thingToCheck).matches() || LONG_NUMERIC_MATCH.matcher(thingToCheck).matches() || RGB_MATCH.matcher(thingToCheck).matches() || RGBA_MATCH.matcher(thingToCheck).matches() || PERC_MATCH.matcher(thingToCheck).matches() || HSL_MATCH.matcher(thingToCheck).matches() || HSLA_MATCH.matcher(thingToCheck).matches();
    }
}

