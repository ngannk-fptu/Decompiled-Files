/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.SubRenderer
 */
package com.atlassian.confluence.macro.wiki;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.SubRenderer;
import java.util.Map;

public class UnmigratedInlineWikiMarkupMacro
implements Macro {
    public static final String DISABLE_ANTISAMY_FEATURE_KEY = "wiki.macro.disable.antisamy";
    public static final String MACRO_NAME = "unmigrated-inline-wiki-markup";
    private SubRenderer subRenderer;

    public void setSubRenderer(SubRenderer subRenderer) {
        this.subRenderer = subRenderer;
    }

    @Override
    public String execute(Map<String, String> parameters, String body, ConversionContext context) throws MacroExecutionException {
        PageContext pageContext = context.getPageContext();
        RenderMode renderMode = RenderMode.suppress((long)256L);
        if (pageContext != null) {
            renderMode = pageContext.getRenderMode().and(renderMode);
        }
        return this.subRenderer.render(body, (RenderContext)pageContext, renderMode);
    }

    @Override
    public Macro.BodyType getBodyType() {
        return Macro.BodyType.PLAIN_TEXT;
    }

    @Override
    public Macro.OutputType getOutputType() {
        return Macro.OutputType.INLINE;
    }
}

