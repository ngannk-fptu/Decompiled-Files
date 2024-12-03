/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkResolver
 */
package com.atlassian.confluence.links;

import com.atlassian.confluence.links.linktypes.IncludePageMacroLink;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.filters.UrlFilter;
import com.atlassian.confluence.util.RegExpProcessor;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class LinkExtractor
implements RegExpProcessor.RegExpProcessorHandler {
    private List<Link> extractedLinks = new ArrayList<Link>();
    private PageContext pageContext;
    private LinkResolver linkResolver;

    public LinkExtractor(LinkResolver linkResolver, PageContext pageContext) {
        this.linkResolver = linkResolver;
        this.pageContext = pageContext;
    }

    public List getExtractedLinks() {
        return this.extractedLinks;
    }

    @Override
    public void handleMatch(StringBuffer stringBuffer, Matcher matcher, RegExpProcessor regExp) {
        this.extractedLinks.add(this.linkResolver.createLink((RenderContext)this.pageContext, this.getRelevantMatchPart(regExp, matcher)));
    }

    private String getRelevantMatchPart(RegExpProcessor regExp, Matcher matcher) {
        if (regExp.getPattern().equals("([^a-zA-Z0-9!/\\[]|^)([A-Z])([a-z]+([A-Z][a-zA-Z0-9]+)+)(([^a-zA-Z0-9!\\]])|\r?\n|$)")) {
            return matcher.group(2) + matcher.group(3);
        }
        if (regExp.getPattern().equals(UrlFilter.URL_PATTERN)) {
            return matcher.group(2);
        }
        if (regExp.getPattern().equals(IncludePageMacroLink.pattern.pattern())) {
            return matcher.group(0);
        }
        return matcher.group(2);
    }
}

