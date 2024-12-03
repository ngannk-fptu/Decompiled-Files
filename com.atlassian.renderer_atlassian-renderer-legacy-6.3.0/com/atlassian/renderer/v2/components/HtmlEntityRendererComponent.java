/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlEntityRendererComponent
extends AbstractRegexRendererComponent {
    public static final Pattern HTML_ENTITY_PATTERN = Pattern.compile("&(?:(?:#[0-9]{1,10})|(?:[a-zA-Z]{1,10}));");

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderPhrases();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        return this.regexRender(wiki, context, HTML_ENTITY_PATTERN);
    }

    @Override
    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        buffer.append(context.getRenderedContentStore().addInline(matcher.group()));
    }
}

