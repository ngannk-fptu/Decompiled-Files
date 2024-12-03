/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.links;

import com.atlassian.renderer.links.Link;
import java.util.List;

public class UnresolvedLink
extends Link {
    private Link unresolvedLink;

    public UnresolvedLink(String originalLinkText) {
        this(originalLinkText, originalLinkText);
    }

    public UnresolvedLink(String originalLinkText, String linkBody) {
        super(originalLinkText);
        this.linkBody = linkBody;
    }

    public UnresolvedLink(String originalLinkText, Link unresolvedLink) {
        super(originalLinkText);
        this.unresolvedLink = unresolvedLink;
        this.linkBody = unresolvedLink.getLinkBody();
    }

    @Override
    public boolean isRelativeUrl() {
        return false;
    }

    @Override
    public String getTitle() {
        if (this.unresolvedLink == null) {
            return this.getOriginalLinkText();
        }
        return this.unresolvedLink.getTitle();
    }

    @Override
    public String getTitleKey() {
        return this.unresolvedLink == null ? null : this.unresolvedLink.getTitleKey();
    }

    @Override
    public List getTitleArgs() {
        return this.unresolvedLink == null ? null : this.unresolvedLink.getTitleArgs();
    }

    @Override
    public String getUrl() {
        return "";
    }

    @Override
    public String getWikiDestination() {
        return this.unresolvedLink == null ? null : this.unresolvedLink.getWikiDestination();
    }

    @Override
    public String getWikiTitle() {
        return this.unresolvedLink == null ? null : this.unresolvedLink.getWikiTitle();
    }

    @Override
    public boolean isAliasSpecified() {
        return this.unresolvedLink != null && this.unresolvedLink.isAliasSpecified();
    }
}

