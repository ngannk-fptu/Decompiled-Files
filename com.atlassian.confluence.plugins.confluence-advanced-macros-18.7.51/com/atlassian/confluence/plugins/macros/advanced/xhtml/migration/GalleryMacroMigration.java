/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder
 */
package com.atlassian.confluence.plugins.macros.advanced.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder;
import java.util.HashMap;
import java.util.Map;

public class GalleryMacroMigration
implements MacroMigration {
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        Map macroParameters = macroDefinition.getParameters();
        if (macroParameters.containsKey("reverseSort")) {
            HashMap<String, String> modifiedMacroParameters = new HashMap<String, String>(macroDefinition.getParameters());
            modifiedMacroParameters.remove("reverseSort");
            modifiedMacroParameters.put("reverse", "true");
            MacroDefinitionBuilder builder = MacroDefinition.builder((String)macroDefinition.getName()).withMacroBody(macroDefinition.getBody()).withParameters(modifiedMacroParameters);
            builder.setDefaultParameterValue(macroDefinition.getDefaultParameterValue());
            return builder.build();
        }
        return macroDefinition;
    }
}

