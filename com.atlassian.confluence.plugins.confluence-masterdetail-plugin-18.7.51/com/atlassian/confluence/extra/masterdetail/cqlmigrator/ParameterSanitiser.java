/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.masterdetail.cqlmigrator;

import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

class ParameterSanitiser {
    ParameterSanitiser() {
    }

    static List<String> getSpaceKeysFromDelimitedString(String spacesString) {
        String spacesParameter = StringUtils.trim((String)spacesString);
        if (StringUtils.isBlank((CharSequence)spacesParameter)) {
            return Lists.newArrayList();
        }
        return Arrays.asList(StringUtils.split((String)spacesParameter, (String)",;/| "));
    }

    public static String getParameter(String paramKey, MacroDefinition macro) {
        String value = macro.getParameter(paramKey);
        return value != null ? value.trim() : null;
    }
}

