/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.browser.beans.MacroFormDetails
 *  com.atlassian.confluence.macro.browser.beans.MacroMetadata
 *  com.atlassian.confluence.macro.browser.beans.MacroParameter
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.tinymceplugin.placeholder;

import com.atlassian.confluence.macro.browser.beans.MacroFormDetails;
import com.atlassian.confluence.macro.browser.beans.MacroMetadata;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

class PlaceholderStringUtils {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
    private static final String SHOW_VALUE_OPTION = "showValueInPlaceholder";
    private static final String SHOW_NAME_OPTION = "showNameInPlaceholder";

    PlaceholderStringUtils() {
    }

    static String createParametersString(MacroDefinition macroDefinition, MacroMetadata metadata) {
        MacroFormDetails formDetails = null;
        if (metadata != null) {
            formDetails = metadata.getFormDetails();
        }
        StringBuilder parametersString = new StringBuilder();
        boolean showAllParameters = true;
        List parameterMetadata = formDetails != null ? formDetails.getParameters() : Collections.emptyList();
        for (MacroParameter param : parameterMetadata) {
            if (param.getOptions().getProperty(SHOW_NAME_OPTION) == null && param.getOptions().getProperty(SHOW_VALUE_OPTION) == null) continue;
            showAllParameters = false;
            break;
        }
        HashMap parameterCopy = macroDefinition.getParameters() != null ? new HashMap(macroDefinition.getParameters()) : Collections.emptyMap();
        parameterCopy.remove("atlassian-macro-output-type");
        boolean showDefaultParameter = true;
        if (formDetails != null) {
            showDefaultParameter = formDetails.isShowDefaultParamInPlaceholder();
        }
        for (MacroParameter macroParameter : parameterMetadata) {
            boolean showValue;
            if (StringUtils.isBlank((CharSequence)macroParameter.getName()) && !showDefaultParameter || !parameterCopy.containsKey(macroParameter.getName())) continue;
            String paramValue = (String)parameterCopy.remove(macroParameter.getName());
            boolean showName = Boolean.valueOf(macroParameter.getOptions().getProperty(SHOW_NAME_OPTION)) != false || showAllParameters;
            boolean bl = showValue = Boolean.valueOf(macroParameter.getOptions().getProperty(SHOW_VALUE_OPTION)) != false || showAllParameters;
            String pStr = PlaceholderStringUtils.buildParameterDisplay(macroParameter.getName(), paramValue, showName, showValue);
            if (!StringUtils.isNotBlank((CharSequence)pStr)) continue;
            parametersString.append(" | ").append(pStr);
        }
        if (showAllParameters && !parameterCopy.isEmpty()) {
            for (Map.Entry entry : parameterCopy.entrySet()) {
                String pStr;
                if (StringUtils.isBlank((CharSequence)((CharSequence)entry.getKey())) && !showDefaultParameter || !StringUtils.isNotBlank((CharSequence)(pStr = PlaceholderStringUtils.buildParameterDisplay((String)entry.getKey(), (String)entry.getValue(), true, true)))) continue;
                parametersString.append(" | ").append(pStr);
            }
        }
        return parametersString.toString();
    }

    static String truncate(String str, int maxLength) {
        Object shortened = StringUtils.left((String)str, (int)maxLength);
        shortened = (String)shortened + (str.length() > maxLength ? "..." : "");
        return shortened;
    }

    private static String buildParameterDisplay(String name, String value, boolean showName, boolean showValue) {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank((CharSequence)name) && showName && !NUMBER_PATTERN.matcher(name).matches()) {
            builder.append(name);
        }
        if (StringUtils.isNotBlank((CharSequence)value) && showValue) {
            if (builder.length() > 0) {
                builder.append(" = ");
            }
            builder.append(value);
        }
        return builder.toString();
    }
}

