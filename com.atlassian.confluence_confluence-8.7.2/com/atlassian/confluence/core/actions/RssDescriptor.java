/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.actions;

public class RssDescriptor {
    private String title;
    private String atomHref;
    private String rssHref;

    public RssDescriptor(String baseHref, String title, boolean authenticated) {
        baseHref = authenticated ? (String)baseHref + "&amp;publicFeed=false&amp;os_authType=basic" : (String)baseHref + "&amp;publicFeed=true";
        this.atomHref = (String)baseHref + "&amp;rssType=atom";
        this.rssHref = (String)baseHref + "&amp;rssType=rss2";
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAtomHref() {
        return this.atomHref;
    }

    public String getRssHref() {
        return this.rssHref;
    }
}

