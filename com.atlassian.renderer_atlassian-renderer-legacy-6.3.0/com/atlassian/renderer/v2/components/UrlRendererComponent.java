/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.links.UrlLink;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlRendererComponent
extends AbstractRegexRendererComponent {
    public static Pattern URL_PATTERN = null;
    public static String PURE_URL_PATTERN = null;
    public static final char[] INVALID_END_CHARS = new char[]{'.', ',', '>', ')', ']', ';', '}', '\"', '\'', '!'};
    private LinkResolver linkResolver;

    public UrlRendererComponent(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderLinks();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        if (wiki.indexOf(":") == -1) {
            return wiki;
        }
        return this.regexRender(wiki, context, URL_PATTERN);
    }

    @Override
    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        String url = matcher.group(2);
        StringBuffer prepChars = new StringBuffer();
        while (this.endsWithBadChar(url)) {
            prepChars.insert(0, url.charAt(url.length() - 1));
            url = url.substring(0, url.length() - 1);
        }
        buffer.append(matcher.group(1));
        Link link = this.linkResolver.createLink(context, url);
        buffer.append(context.addRenderedContent(this.handleUrlLink(link, context, url)));
        buffer.append(prepChars);
    }

    private boolean endsWithBadChar(String url) {
        char c = url.charAt(url.length() - 1);
        for (int i = 0; i < INVALID_END_CHARS.length; ++i) {
            if (c != INVALID_END_CHARS[i]) continue;
            return true;
        }
        return false;
    }

    public String handleUrlLink(Link link, RenderContext renderContext, String url) {
        renderContext.addExternalReference(new UrlLink(new GenericLinkParser(url)));
        if (link instanceof UnresolvedLink || link instanceof UnpermittedLink) {
            return url;
        }
        return renderContext.getLinkRenderer().renderLink(link, renderContext);
    }

    static {
        Iterator it = UrlUtil.URL_PROTOCOLS.iterator();
        String protocols = "";
        while (it.hasNext()) {
            if (!protocols.equals("")) {
                protocols = protocols + "|";
            }
            protocols = protocols + it.next();
        }
        PURE_URL_PATTERN = "(?<![\\p{Alnum}])((" + protocols + ")([-_.!~*';/?:@#&=%+$,\\p{Alnum}\\[\\]\\(\\)\\\\])+)";
        URL_PATTERN = Pattern.compile("([^\"\\[\\|'!]|^)" + PURE_URL_PATTERN);
    }
}

