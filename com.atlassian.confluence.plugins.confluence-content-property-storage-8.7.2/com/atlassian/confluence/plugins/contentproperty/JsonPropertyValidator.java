/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.JsonString
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.contentproperty;

import com.atlassian.confluence.api.model.JsonString;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import org.apache.commons.lang3.StringUtils;

public class JsonPropertyValidator {
    public static void validateKey(SimpleValidationResult.Builder result, String key) {
        if (StringUtils.isBlank((CharSequence)key)) {
            result.addError("jsonproperty.key.required", new Object[0]);
            return;
        }
        if (key.length() > 255) {
            result.addError("jsonproperty.key.length.exceeded", new Object[]{key, 255});
        }
        if (key.contains("/")) {
            result.addError("jsonproperty.key.invalid.character", new Object[0]);
        }
    }

    public static void validateValue(SimpleValidationResult.Builder result, String key, JsonString value) {
        if (value == null) {
            result.addError("jsonproperty.value.required", new Object[0]);
            return;
        }
        String valueString = value.getValue();
        if (valueString == null) {
            result.addError("jsonproperty.value.required", new Object[0]);
        } else if (valueString.length() > 32768) {
            result.addError("jsonproperty.value.length.exceeded", new Object[]{key, 32768});
        }
    }
}

