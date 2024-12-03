/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.RendererComponent;
import com.atlassian.renderer.v2.components.WikiLinkTransformer;
import com.atlassian.renderer.v2.components.link.LinkDecorator;
import com.google.common.base.Function;

public class LinkRendererComponent
implements RendererComponent {
    private LinkResolver linkResolver;

    public LinkRendererComponent(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinks();
    }

    @Override
    public String render(String wiki, final RenderContext context) {
        return new WikiLinkTransformer().transform(wiki, new Function<String, CharSequence>(){

            public CharSequence apply(String s) {
                return LinkRendererComponent.this.renderLink(s, context);
            }
        });
    }

    private String renderLink(String linkText, RenderContext context) {
        Link link = this.linkResolver.createLink(context, linkText);
        return context.getRenderedContentStore().addInline(new LinkDecorator(link));
    }
}

