/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.renderer.v2.components.RendererComponent;

public class HtmlEscapeRendererComponent
implements RendererComponent {
    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.htmlEscape();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        return HtmlEscaper.escapeAllExceptQuotes(wiki, context.getRenderMode().preserveEntities());
    }

    public static String escapeHtml(String s, boolean preserveExistingEntities) {
        return HtmlEscaper.escapeAllExceptQuotes(s, preserveExistingEntities);
    }
}

