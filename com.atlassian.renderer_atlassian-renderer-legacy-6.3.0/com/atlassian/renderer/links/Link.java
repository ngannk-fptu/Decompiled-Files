/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.links;

import java.util.List;

public abstract class Link {
    private final String originalLinkText;
    protected String url;
    protected String title;
    protected String linkBody;
    protected boolean relativeUrl;
    protected String iconName;
    protected String titleKey;
    protected List titleArgs;
    protected String wikiDestination;
    protected String wikiTitle;
    protected boolean aliasSpecified;

    public Link(String originalLinkText) {
        this.originalLinkText = originalLinkText;
    }

    public String getOriginalLinkText() {
        return this.originalLinkText;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getLinkBody() {
        return this.linkBody;
    }

    public String getUnpermittedLinkBody() {
        return this.linkBody;
    }

    public boolean isRelativeUrl() {
        return this.relativeUrl;
    }

    public String getLinkAttributes() {
        return "";
    }

    public String getIconName() {
        return this.iconName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!o.getClass().equals(this.getClass())) {
            return false;
        }
        Link link = (Link)o;
        return this.originalLinkText == null ? link.originalLinkText == null : this.originalLinkText.equals(link.originalLinkText);
    }

    public int hashCode() {
        return this.originalLinkText != null ? this.originalLinkText.hashCode() : 0;
    }

    public String getTitleKey() {
        return this.titleKey;
    }

    public List getTitleArgs() {
        return this.titleArgs;
    }

    public String getWikiDestination() {
        return this.wikiDestination;
    }

    public String getWikiTitle() {
        return this.wikiTitle;
    }

    public boolean isAliasSpecified() {
        return this.aliasSpecified;
    }
}

