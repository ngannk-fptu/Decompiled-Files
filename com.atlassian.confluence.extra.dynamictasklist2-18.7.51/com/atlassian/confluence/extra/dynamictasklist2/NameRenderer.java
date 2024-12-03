/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.renderer.v2.RenderMode
 */
package com.atlassian.confluence.extra.dynamictasklist2;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.v2.RenderMode;

public class NameRenderer {
    private static final String CLOSE_PARAGRAPH = "</p>";
    private static final String OPEN_PARAGRAPH = "<p>";
    private WikiStyleRenderer wikiStyleRenderer;
    private PageContext pageContext;

    public NameRenderer(WikiStyleRenderer wikiStyleRenderer, PageContext pageContext) {
        this.wikiStyleRenderer = wikiStyleRenderer;
        this.pageContext = pageContext;
    }

    public String render(String name, boolean asWikiMarkup) {
        if (name != null) {
            if (asWikiMarkup) {
                this.pageContext.pushRenderMode(RenderMode.allow((long)5272L));
                name = this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)this.pageContext, name);
                this.pageContext.popRenderMode();
                if (name.startsWith(OPEN_PARAGRAPH)) {
                    name = name.substring(OPEN_PARAGRAPH.length());
                }
                if (name.endsWith(CLOSE_PARAGRAPH)) {
                    name = name.substring(0, name.length() - CLOSE_PARAGRAPH.length());
                }
            } else {
                name = HtmlUtil.htmlEncode((String)name);
            }
        }
        return name;
    }
}

