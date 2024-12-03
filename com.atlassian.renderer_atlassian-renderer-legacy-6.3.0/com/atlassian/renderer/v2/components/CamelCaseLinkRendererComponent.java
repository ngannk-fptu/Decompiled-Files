/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.RendererConfiguration;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import com.atlassian.renderer.v2.components.link.LinkDecorator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CamelCaseLinkRendererComponent
extends AbstractRegexRendererComponent {
    static final Pattern LINK_CAMELCASE_PATTERN = Pattern.compile("(^|[^\\p{Alpha}!\\^])([\\p{Lu}][\\p{Alnum}]*[\\p{L}&&[^\\p{Lu}]][\\p{Alnum}]*[\\p{Lu}][\\p{Alnum}]+)", 32);
    private LinkResolver linkResolver;
    private RendererConfiguration rendererConfiguration;

    public CamelCaseLinkRendererComponent(LinkResolver linkResolver, RendererConfiguration rendererConfiguration) {
        this.linkResolver = linkResolver;
        this.rendererConfiguration = rendererConfiguration;
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinks() && this.rendererConfiguration.isAllowCamelCase();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        if (this.rendererConfiguration.isAllowCamelCase()) {
            return this.regexRender(wiki, context, LINK_CAMELCASE_PATTERN);
        }
        return wiki;
    }

    @Override
    public void appendSubstitution(StringBuffer stringBuffer, RenderContext context, Matcher matcher) {
        String linkText = matcher.group(2);
        stringBuffer.append(matcher.group(1));
        Link link = this.linkResolver.createLink(context, linkText);
        stringBuffer.append(context.getRenderedContentStore().addInline(new LinkDecorator(link)));
    }
}

