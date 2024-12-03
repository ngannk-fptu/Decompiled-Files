/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.links;

import com.atlassian.renderer.links.Link;

public class UnpermittedLink
extends Link {
    private Link wrappedLink;

    public UnpermittedLink(Link wrappedLink) {
        super(wrappedLink.getOriginalLinkText());
        this.wrappedLink = wrappedLink;
    }

    public Link getWrappedLink() {
        return this.wrappedLink;
    }

    @Override
    public String getLinkBody() {
        return this.wrappedLink.getUnpermittedLinkBody();
    }

    @Override
    public boolean isRelativeUrl() {
        return false;
    }

    @Override
    public String getTitle() {
        return this.wrappedLink.getUnpermittedLinkBody();
    }

    @Override
    public String getUrl() {
        return "";
    }

    @Override
    public String getWikiDestination() {
        return this.wrappedLink.getWikiDestination();
    }

    @Override
    public String getWikiTitle() {
        return this.wrappedLink.getWikiTitle();
    }

    @Override
    public boolean isAliasSpecified() {
        return this.wrappedLink.isAliasSpecified();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnpermittedLink)) {
            return false;
        }
        UnpermittedLink unpermittedLink = (UnpermittedLink)o;
        return !(this.wrappedLink != null ? !this.wrappedLink.equals(unpermittedLink.wrappedLink) : unpermittedLink.wrappedLink != null);
    }

    @Override
    public int hashCode() {
        return this.wrappedLink != null ? this.wrappedLink.hashCode() : 0;
    }
}

