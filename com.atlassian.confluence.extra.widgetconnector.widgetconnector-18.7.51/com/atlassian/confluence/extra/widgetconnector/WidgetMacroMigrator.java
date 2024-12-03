/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.widgetconnector;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class WidgetMacroMigrator
implements MacroMigration {
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        String bodyText = macroDefinition.getBodyText();
        if (StringUtils.isBlank((CharSequence)bodyText)) {
            return macroDefinition;
        }
        HashMap<String, String> parameters = new HashMap<String, String>(macroDefinition.getParameters());
        parameters.put("url", StringUtils.strip((String)bodyText));
        macroDefinition.setParameters(parameters);
        return macroDefinition;
    }
}

