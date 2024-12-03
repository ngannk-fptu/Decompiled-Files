/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.links;

import com.atlassian.renderer.links.BaseLink;
import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import java.util.Arrays;

public class UrlLink
extends BaseLink {
    public static final String EXTERNAL_ICON = "external";
    public static final String MAILTO_ICON = "mailto";
    private final String unencodedUrl;

    public UrlLink(String url, String linkBody) {
        this(new GenericLinkParser(url));
        this.linkBody = linkBody;
    }

    public UrlLink(GenericLinkParser parser) {
        super(parser);
        this.iconName = EXTERNAL_ICON;
        this.url = parser.getNotLinkBody();
        this.setI18nTitle("renderer.external.link.title", null);
        if (this.url.startsWith("///")) {
            this.url = this.url.substring(2);
            this.relativeUrl = true;
            this.setI18nTitle("renderer.site.relative.link.title", null);
        } else if (this.url.startsWith("//")) {
            this.url = this.url.substring(1);
            this.setI18nTitle("renderer.relative.link.title", null);
        }
        if (this.url.startsWith("\\\\")) {
            this.url = "file:" + this.url.replaceAll("\\\\", "/");
        }
        this.unencodedUrl = this.url;
        this.url = HtmlEscaper.escapeAll(this.url, true);
        if (this.url.startsWith("mailto:")) {
            if (parser.getLinkBody() == null) {
                this.linkBody = this.linkBody.substring(7);
            }
            this.setI18nTitle("renderer.send.mail.to", Arrays.asList(this.linkBody));
            this.iconName = MAILTO_ICON;
        }
    }

    @Override
    public String getLinkAttributes() {
        return " class=\"external-link\"";
    }

    public String getUnencodedUrl() {
        return this.unencodedUrl;
    }
}

