/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroManager
 */
package com.atlassian.confluence.macro.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.macro.MacroDefinitionWikiMarkupSerializer;
import com.atlassian.confluence.macro.xhtml.MacroMigration;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroManager;

public class V2MacroToUnmigratedWikiMarkupMacroMigration
implements MacroMigration {
    private static final MacroDefinitionWikiMarkupSerializer SERIALIZER = new MacroDefinitionWikiMarkupSerializer();
    private final MacroManager v2MacroManager;

    public V2MacroToUnmigratedWikiMarkupMacroMigration(MacroManager v2MacroManager) {
        this.v2MacroManager = v2MacroManager;
    }

    @Override
    public MacroDefinition migrate(MacroDefinition macroDefinition, ConversionContext conversionContext) {
        String macroName = "unmigrated-inline-wiki-markup";
        Macro v2Macro = this.v2MacroManager.getEnabledMacro(macroDefinition.getName());
        if (v2Macro != null) {
            PageContext pageContext = conversionContext != null ? conversionContext.getPageContext() : null;
            TokenType tokenType = v2Macro.getTokenType(macroDefinition.getParameters(), macroDefinition.getBodyText(), (RenderContext)pageContext);
            if (tokenType == TokenType.BLOCK) {
                macroName = "unmigrated-wiki-markup";
            }
        }
        return MacroDefinition.builder(macroName).withMacroBody(new PlainTextMacroBody(SERIALIZER.serialize(macroDefinition))).build();
    }
}

