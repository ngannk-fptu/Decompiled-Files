/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.renderer;

import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MacroParameterStringParser {
    private static final Logger log = LoggerFactory.getLogger(MacroParameterStringParser.class);

    MacroParameterStringParser() {
    }

    static MacroParameter extractParameter(String pluginKey, String macroName, String paramStr) {
        String[] paramParts = paramStr.split(":", 2);
        String name = paramParts[0];
        if (StringUtils.isBlank((CharSequence)name)) {
            log.warn("Skipping undefined parameter for macro '" + macroName + "'");
            return null;
        }
        boolean required = false;
        boolean multiple = false;
        boolean hidden = false;
        String defaultValue = null;
        String displayName = null;
        String description = null;
        String typeStr = null;
        Map<String, String> detailMap = new HashMap<String, String>();
        if (paramParts.length == 2) {
            detailMap = MacroParameterStringParser.fillParamDetailMap(detailMap, paramParts[1]);
            required = MacroParameterStringParser.blankOrTrue(detailMap, "required");
            multiple = MacroParameterStringParser.blankOrTrue(detailMap, "multiple");
            hidden = MacroParameterStringParser.blankOrTrue(detailMap, "hidden");
            defaultValue = detailMap.remove("default");
            displayName = detailMap.remove("title");
            description = detailMap.remove("desc");
            typeStr = detailMap.remove("type");
        } else {
            log.warn("No details for parameter in string : '" + paramStr + "' for macro '" + macroName + "'. Using defaults.");
        }
        if (description == null) {
            description = "";
        }
        MacroParameterType type = StringUtils.isBlank(typeStr) ? MacroParameterType.STRING : MacroParameterType.get(typeStr);
        MacroParameter macroParameter = new MacroParameter(pluginKey, macroName, name, type, required, multiple, defaultValue, hidden);
        macroParameter.setDisplayName(displayName);
        macroParameter.setDescription(description);
        if (!detailMap.isEmpty()) {
            MacroParameterStringParser.addEnumValues(macroParameter, detailMap);
            MacroParameterStringParser.addOptions(macroParameter, detailMap);
        }
        return macroParameter;
    }

    private static Map<String, String> fillParamDetailMap(Map<String, String> keyValMap, String paramPart) {
        String[] paramPairs;
        for (String paramPair : paramPairs = paramPart.split("\\|")) {
            if (StringUtils.isNotBlank((CharSequence)paramPair)) {
                String[] keyValPair = paramPair.split("=", 2);
                String key = keyValPair[0];
                String val = keyValPair.length > 1 ? keyValPair[1] : "";
                keyValMap.put(key, val);
                continue;
            }
            log.warn("Bad parameter details in string : " + paramPart);
        }
        return keyValMap;
    }

    private static void addOptions(MacroParameter macroParameter, Map<String, String> keyValMap) {
        keyValMap.forEach((key, value) -> {
            if (key.startsWith("option-")) {
                macroParameter.addOption(key.substring(7), (String)value);
            }
        });
    }

    private static void addEnumValues(MacroParameter macroParameter, Map<String, String> keyValMap) {
        String enumValuesStr = keyValMap.remove("enumValues");
        if (StringUtils.isNotBlank((CharSequence)enumValuesStr)) {
            String[] enumValues;
            for (String enumValue : enumValues = enumValuesStr.split(",")) {
                macroParameter.addEnumValue(enumValue);
            }
        }
    }

    private static boolean blankOrTrue(Map<String, String> keyValMap, String key) {
        String val = keyValMap.remove(key);
        return "".equals(val) || "true".equalsIgnoreCase(val);
    }
}

