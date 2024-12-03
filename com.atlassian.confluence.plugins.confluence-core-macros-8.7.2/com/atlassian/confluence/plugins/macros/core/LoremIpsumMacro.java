/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.renderer.v2.macro.basic.LoremIpsumMacro
 */
package com.atlassian.confluence.plugins.macros.core;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class LoremIpsumMacro
implements com.atlassian.confluence.macro.Macro {
    private final Macro oldPanelMacro = new com.atlassian.renderer.v2.macro.basic.LoremIpsumMacro();

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        try {
            return this.oldPanelMacro.execute(parameters, body, (RenderContext)(context != null ? context.getPageContext() : null));
        }
        catch (MacroException e) {
            throw new MacroExecutionException((Throwable)e);
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }
}

