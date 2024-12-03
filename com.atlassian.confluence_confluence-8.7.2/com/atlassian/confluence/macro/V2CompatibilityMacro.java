/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class V2CompatibilityMacro
implements Macro {
    private com.atlassian.renderer.v2.macro.Macro v2Macro;
    private Macro.BodyType bodyType;

    public V2CompatibilityMacro(com.atlassian.renderer.v2.macro.Macro v2Macro, Macro.BodyType bodyType) {
        this.v2Macro = v2Macro;
        this.bodyType = bodyType;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext) throws MacroExecutionException {
        RenderMode bodyRenderMode = this.v2Macro.getBodyRenderMode();
        if (bodyRenderMode != null && bodyRenderMode.htmlEscape()) {
            boolean preserveExistingEntities = bodyRenderMode.preserveEntities();
            body = HtmlEscaper.escapeAll((String)body, (boolean)preserveExistingEntities);
        }
        try {
            return this.v2Macro.execute(parameters, body, (RenderContext)(conversionContext != null ? conversionContext.getPageContext() : null));
        }
        catch (MacroException e) {
            throw new MacroExecutionException(e);
        }
    }

    @Override
    public Macro.BodyType getBodyType() {
        return this.bodyType;
    }

    @Override
    public Macro.OutputType getOutputType() {
        return this.v2Macro.isInline() ? Macro.OutputType.INLINE : Macro.OutputType.BLOCK;
    }
}

