/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.links;

import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.links.Link;
import java.util.List;

public abstract class BaseLink
extends Link {
    private boolean canSetTitle = true;
    private GenericLinkParser originalParser;

    protected BaseLink(GenericLinkParser parser) {
        super(parser.getOriginalLinkText());
        this.originalParser = parser;
        if (parser.getLinkBody() == null) {
            this.linkBody = parser.getNotLinkBody();
            this.aliasSpecified = false;
        } else {
            this.linkBody = parser.getLinkBody();
            this.aliasSpecified = true;
        }
        if (parser.getLinkTitle() != null) {
            this.title = parser.getLinkTitle();
            this.canSetTitle = false;
        }
        this.wikiDestination = parser.getNotLinkBody();
        this.wikiTitle = parser.getLinkTitle();
    }

    protected void setTitle(String title) {
        if (this.canSetTitle) {
            this.title = title;
        }
    }

    protected void setI18nTitle(String titleKey, List titleArgs) {
        if (this.canSetTitle) {
            this.titleKey = titleKey;
            this.titleArgs = titleArgs;
        }
    }

    public GenericLinkParser getOriginalParser() {
        return this.originalParser;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!o.getClass().equals(this.getClass())) {
            return false;
        }
        BaseLink link = (BaseLink)o;
        if (link.getUrl() == null || this.getUrl() == null) {
            return link.getUrl() == this.getUrl();
        }
        return link.getUrl().equals(this.getUrl());
    }

    @Override
    public int hashCode() {
        int result = this.getClass().hashCode();
        result = 29 * result + (this.getUrl() != null ? this.getUrl().hashCode() : 0);
        return result;
    }
}

