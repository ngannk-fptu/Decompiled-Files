/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.radeox.macro.parameter.MacroParameter
 */
package com.atlassian.renderer.macro.macros;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.macro.BaseMacro;
import java.io.IOException;
import java.io.Writer;
import org.radeox.macro.parameter.MacroParameter;

public class QuoteMacro
extends BaseMacro {
    private WikiStyleRenderer wikiStyleRenderer;

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public String getName() {
        return "quote";
    }

    public void execute(Writer writer, MacroParameter macroParameter) throws IllegalArgumentException, IOException {
        String renderedContent = this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)macroParameter.getContext().getParameters().get("RENDER_CONTEXT"), macroParameter.getContent().trim());
        writer.write("<blockquote class=\"blockquote\"><p>" + renderedContent + "</p></blockquote>");
    }
}

