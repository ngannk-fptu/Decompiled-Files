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
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.extra.layout.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class SectionMacro
implements Macro {
    private static final com.atlassian.confluence.extra.layout.SectionMacro INSTANCE = new com.atlassian.confluence.extra.layout.SectionMacro();

    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        try {
            return INSTANCE.execute(parameters, body, (RenderContext)(conversionContext != null ? conversionContext.getPageContext() : null));
        }
        catch (MacroException e) {
            throw new MacroExecutionException((Throwable)e);
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.RICH_TEXT;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }
}

