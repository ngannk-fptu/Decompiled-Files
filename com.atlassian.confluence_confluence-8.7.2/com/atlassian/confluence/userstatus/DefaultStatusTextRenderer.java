/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.renderer.v2.RenderMode
 */
package com.atlassian.confluence.userstatus;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.userstatus.StatusTextRenderer;
import com.atlassian.confluence.userstatus.tag.HashTagHelper;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.renderer.v2.RenderMode;

@Deprecated
public class DefaultStatusTextRenderer
implements StatusTextRenderer {
    private WikiStyleRenderer wikiStyleRenderer;
    private static final RenderMode STATUS_RENDER_MODE = RenderMode.LINKS_ONLY.or(RenderMode.allow((long)1040L));

    public DefaultStatusTextRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    @Override
    @HtmlSafe
    public String render(String status) {
        if (status == null) {
            return "";
        }
        String statusForRendering = HashTagHelper.linkHashTags(status);
        PageContext context = new PageContext();
        context.pushRenderMode(STATUS_RENDER_MODE);
        return this.wikiStyleRenderer.convertWikiToXHtml((RenderContext)context, statusForRendering);
    }
}

