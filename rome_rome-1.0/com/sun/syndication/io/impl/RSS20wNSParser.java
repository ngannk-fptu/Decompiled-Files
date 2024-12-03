/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.Namespace
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.impl.RSS20Parser;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class RSS20wNSParser
extends RSS20Parser {
    private static String RSS20_URI = "http://backend.userland.com/rss2";

    public RSS20wNSParser() {
        this("rss_2.0wNS");
    }

    protected RSS20wNSParser(String type) {
        super(type);
    }

    public boolean isMyType(Document document) {
        boolean ok;
        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();
        boolean bl = ok = defaultNS != null && defaultNS.equals((Object)this.getRSSNamespace());
        if (ok) {
            ok = super.isMyType(document);
        }
        return ok;
    }

    protected Namespace getRSSNamespace() {
        return Namespace.getNamespace((String)RSS20_URI);
    }

    protected WireFeed parseChannel(Element rssRoot) {
        WireFeed wFeed = super.parseChannel(rssRoot);
        wFeed.setFeedType("rss_2.0");
        return wFeed;
    }
}

