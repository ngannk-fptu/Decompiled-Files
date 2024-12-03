/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.macro.MacroDefinitionSerializer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class MacroDefinitionWikiMarkupSerializer
implements MacroDefinitionSerializer {
    @Override
    public String serialize(MacroDefinition macroDefinition) {
        StringBuilder result = new StringBuilder("{");
        boolean hasWrittenParameters = false;
        result.append(macroDefinition.getName());
        String defaultParameter = macroDefinition.getParameter("");
        if (StringUtils.isNotBlank((CharSequence)defaultParameter)) {
            result.append(':').append(defaultParameter);
            hasWrittenParameters = true;
        }
        if (macroDefinition.getParameters() != null) {
            int index = 1;
            for (Map.Entry<String, String> entry : macroDefinition.getParameters().entrySet()) {
                String paramKey = entry.getKey();
                String paramVal = entry.getValue();
                if (StringUtils.isBlank((CharSequence)paramKey)) continue;
                result.append(hasWrittenParameters ? (char)'|' : ':');
                if (String.valueOf(index).equals(paramKey)) {
                    result.append(paramVal);
                } else {
                    result.append(paramKey);
                    result.append('=');
                    result.append(paramVal);
                }
                hasWrittenParameters = true;
                ++index;
            }
        }
        result.append('}');
        if (macroDefinition.hasBody()) {
            result.append(macroDefinition.getBodyText());
            result.append('{').append(macroDefinition.getName()).append('}');
        }
        return result.toString();
    }
}

