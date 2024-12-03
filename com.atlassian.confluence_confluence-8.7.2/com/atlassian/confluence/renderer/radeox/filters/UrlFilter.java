/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.GenericLinkParser
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkResolver
 *  com.atlassian.renderer.links.UnpermittedLink
 *  com.atlassian.renderer.links.UnresolvedLink
 *  com.atlassian.renderer.links.UrlLink
 *  org.radeox.api.engine.RenderEngine
 *  org.radeox.filter.CacheFilter
 *  org.radeox.filter.context.FilterContext
 *  org.radeox.filter.regex.RegexTokenFilter
 *  org.radeox.regex.MatchResult
 */
package com.atlassian.confluence.renderer.radeox.filters;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.WikiRendererContextKeys;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.links.UrlLink;
import org.radeox.api.engine.RenderEngine;
import org.radeox.filter.CacheFilter;
import org.radeox.filter.context.FilterContext;
import org.radeox.filter.regex.RegexTokenFilter;
import org.radeox.regex.MatchResult;

public class UrlFilter
extends RegexTokenFilter
implements CacheFilter {
    public static final String URL_PATTERN;
    public static final String PURE_URL_PATTERN;
    private LinkResolver linkResolver;

    public UrlFilter() {
        super(URL_PATTERN);
    }

    public void setLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
        PageContext pageContext = WikiRendererContextKeys.getPageContext(context.getRenderContext().getParameters());
        String url = result.group(2);
        buffer.append(result.group(1));
        Link link = this.linkResolver.createLink((RenderContext)pageContext, url);
        UrlFilter.handleUrlLink(link, pageContext, url, buffer, null);
    }

    public static void handleUrlLink(Link link, PageContext pageContext, String url, StringBuffer buffer, RenderEngine engine) {
        if (link instanceof UnresolvedLink || link instanceof UnpermittedLink) {
            buffer.append(url);
        } else {
            buffer.append(pageContext.getRenderedContentStore().addInline((Object)pageContext.getLinkRenderer().renderLink(link, (RenderContext)pageContext)));
        }
        pageContext.addExternalReference((Link)new UrlLink(new GenericLinkParser(url)));
    }

    static {
        PURE_URL_PATTERN = UrlUtils.URL_PATTERN;
        URL_PATTERN = "([^\"\\[\\|'!]|^)" + PURE_URL_PATTERN;
    }
}

