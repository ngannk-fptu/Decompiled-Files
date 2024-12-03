/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Document
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.io.impl.RSS20Parser;
import java.util.Locale;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class RSS20wNSParser
extends RSS20Parser {
    private static String RSS20_URI = "http://backend.userland.com/rss2";

    public RSS20wNSParser() {
        this("rss_2.0wNS");
    }

    protected RSS20wNSParser(String type) {
        super(type);
    }

    @Override
    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();
        return defaultNS != null && defaultNS.equals((Object)this.getRSSNamespace()) && super.isMyType(document);
    }

    @Override
    protected Namespace getRSSNamespace() {
        return Namespace.getNamespace((String)RSS20_URI);
    }

    @Override
    protected WireFeed parseChannel(Element rssRoot, Locale locale) {
        WireFeed wFeed = super.parseChannel(rssRoot, locale);
        wFeed.setFeedType("rss_2.0");
        return wFeed;
    }
}

