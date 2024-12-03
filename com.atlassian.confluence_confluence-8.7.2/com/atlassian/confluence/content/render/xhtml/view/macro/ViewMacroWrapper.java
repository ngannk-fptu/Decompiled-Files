/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.JsonUtils;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.xhtml.api.MacroDefinition;

public interface ViewMacroWrapper {
    public String wrap(ConversionContext var1, Macro.OutputType var2, String var3, MacroDefinition var4);

    @Deprecated(since="8.3.0")
    default public String wrap(ConversionContext context, Macro.OutputType outputType, String macroBody, MacroDefinition macroDefinition, boolean wrap) {
        if (wrap && !JsonUtils.isJsonFormat(macroBody)) {
            return this.wrap(context, outputType, macroBody, macroDefinition);
        }
        return macroBody;
    }
}

