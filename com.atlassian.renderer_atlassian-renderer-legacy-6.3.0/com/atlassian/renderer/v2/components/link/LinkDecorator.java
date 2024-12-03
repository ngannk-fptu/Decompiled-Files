/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.link;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.links.UrlLink;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.Renderable;
import com.atlassian.renderer.v2.SubRenderer;

public class LinkDecorator
implements Renderable {
    Link link;

    public LinkDecorator(Link link) {
        this.link = link;
    }

    @Override
    public void render(SubRenderer subRenderer, RenderContext context, StringBuffer buffer) {
        if (this.link instanceof UrlLink) {
            context.addExternalReference(this.link);
        }
        String renderedLink = (this.link instanceof UnresolvedLink || this.link instanceof UnpermittedLink) && !context.isRenderingForWysiwyg() ? RenderUtils.error(context, "&#91;" + this.link.getLinkBody() + "&#93;", "&#91;" + this.link.getOriginalLinkText() + "&#93;", true) : context.getLinkRenderer().renderLink(this.link, context);
        buffer.append(renderedLink);
    }
}

