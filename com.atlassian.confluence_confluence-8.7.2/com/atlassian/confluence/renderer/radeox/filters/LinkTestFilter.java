/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkRenderer
 *  com.atlassian.renderer.links.LinkResolver
 *  com.atlassian.renderer.links.UnpermittedLink
 *  com.atlassian.renderer.links.UnresolvedLink
 *  com.atlassian.renderer.links.UrlLink
 *  org.radeox.api.engine.RenderEngine
 *  org.radeox.filter.context.FilterContext
 *  org.radeox.filter.regex.RegexTokenFilter
 *  org.radeox.regex.MatchResult
 */
package com.atlassian.confluence.renderer.radeox.filters;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.WikiRendererContextKeys;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkRenderer;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.links.UrlLink;
import org.radeox.api.engine.RenderEngine;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.RegexTokenFilter;
import org.radeox.regex.MatchResult;

public class LinkTestFilter
extends RegexTokenFilter {
    private int activeGroupNo = -1;
    private LinkResolver linkResolver;
    public static final String PATTERN = "(\\[)([\\p{L}&[^\\[\\]\\p{Space}]][\\p{L}&[^\\[\\]]]*)\\]";

    public LinkTestFilter() {
        this(PATTERN, 2);
    }

    protected LinkTestFilter(String pattern, int active_grp_no) {
        super(pattern);
        this.activeGroupNo = active_grp_no;
    }

    protected void setUp(FilterContext context) {
        context.getRenderContext().setCacheable(true);
    }

    protected int getLinkType() {
        return 1;
    }

    public String filter(String pattern, FilterContext context) {
        return super.filter(pattern, context);
    }

    public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
        RenderEngine engine = context.getRenderContext().getRenderEngine();
        PageContext pageContext = WikiRendererContextKeys.getPageContext(context.getRenderContext().getParameters());
        String resultStr = result.group(this.activeGroupNo);
        if (resultStr == null) {
            return;
        }
        if (!(engine instanceof LinkRenderer)) {
            buffer.append(resultStr);
            return;
        }
        Link link = this.linkResolver.createLink((RenderContext)pageContext, resultStr);
        if (link instanceof UrlLink) {
            pageContext.addExternalReference(link);
        }
        if (link instanceof UnresolvedLink || link instanceof UnpermittedLink) {
            this.appendUnresolvedLink(buffer, link);
        } else {
            buffer.append(((LinkRenderer)engine).renderLink(link, (RenderContext)pageContext));
        }
    }

    protected void appendUnresolvedLink(StringBuffer buffer, Link link) {
        buffer.append("<span class=\"error\">");
        buffer.append("&#91;");
        buffer.append(link.getLinkBody());
        buffer.append("&#93;");
        buffer.append("</span>");
    }

    public void setLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }
}

