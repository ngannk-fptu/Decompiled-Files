/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 */
package com.atlassian.confluence.macro.wiki;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import java.util.Map;

public class UnmigratedBlockWikiMarkupMacro
implements Macro {
    public static final String MACRO_NAME = "unmigrated-wiki-markup";
    private WikiStyleRenderer wikiStyleRenderer;

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        return this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)(context != null ? context.getPageContext() : null), body);
    }

    @Override
    public Macro.BodyType getBodyType() {
        return Macro.BodyType.PLAIN_TEXT;
    }

    @Override
    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }
}

