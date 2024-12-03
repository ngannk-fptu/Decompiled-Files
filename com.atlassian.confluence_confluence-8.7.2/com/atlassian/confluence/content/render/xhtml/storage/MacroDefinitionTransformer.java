/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionMarshallingStrategy;
import com.atlassian.confluence.xhtml.api.MacroDefinitionReplacer;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;

public interface MacroDefinitionTransformer {
    public String updateMacroDefinitions(String var1, ConversionContext var2, MacroDefinitionUpdater var3) throws XhtmlException;

    public String replaceMacroDefinitionsWithString(String var1, ConversionContext var2, MacroDefinitionReplacer var3) throws XhtmlException;

    public void handleMacroDefinitions(String var1, ConversionContext var2, MacroDefinitionHandler var3) throws XhtmlException;

    public void handleMacroDefinitions(String var1, ConversionContext var2, MacroDefinitionHandler var3, MacroDefinitionMarshallingStrategy var4) throws XhtmlException;
}

