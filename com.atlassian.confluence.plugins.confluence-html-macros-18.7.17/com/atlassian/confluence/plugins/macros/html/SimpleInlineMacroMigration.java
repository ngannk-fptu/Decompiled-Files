/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.definition.MacroBody
 *  com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody
 *  com.atlassian.confluence.macro.xhtml.MacroMigration
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.html;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import org.apache.commons.lang3.StringUtils;

public class SimpleInlineMacroMigration
implements MacroMigration {
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        macroDefinition.setBody((MacroBody)new PlainTextMacroBody(StringUtils.defaultString((String)macroDefinition.getBodyText())));
        return macroDefinition;
    }
}

