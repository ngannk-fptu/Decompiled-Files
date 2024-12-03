/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.definition.MacroBody
 *  com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.impresence2.migrator;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import org.apache.commons.lang.StringUtils;

public class OldImMacroMigrator
implements MacroMigration {
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        MacroBody macroBody = macroDefinition.getBody();
        String macroBodyText = macroBody.getBody();
        if (StringUtils.isNotBlank((String)macroBodyText)) {
            macroDefinition.setDefaultParameterValue(macroBodyText);
            macroDefinition.setBody((MacroBody)new PlainTextMacroBody(""));
        }
        return macroDefinition;
    }
}

