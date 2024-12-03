/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.macro.Macro
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.SubRenderer
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  com.atlassian.renderer.v2.macro.Macro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.renderer.v2.macro.basic.NoformatMacro
 */
package com.atlassian.confluence.plugins.macros.basic;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.renderer.v2.macro.Macro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class NoformatMacro
implements com.atlassian.confluence.macro.Macro {
    private final Macro oldNoformatMacro;

    public NoformatMacro(@ComponentImport SubRenderer v2SubRenderer) {
        this.oldNoformatMacro = new com.atlassian.renderer.v2.macro.basic.NoformatMacro(v2SubRenderer);
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        body = HtmlEscaper.escapeAll((String)body, (boolean)false);
        try {
            PageContext pageContext = context == null ? null : context.getPageContext();
            return this.oldNoformatMacro.execute(parameters, body, (RenderContext)pageContext);
        }
        catch (MacroException e) {
            throw new MacroExecutionException((Throwable)e);
        }
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.PLAIN_TEXT;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }
}

