/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Element
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.impl.DateParser;
import com.sun.syndication.io.impl.RSS092Parser;
import org.jdom.Element;

public class RSS093Parser
extends RSS092Parser {
    public RSS093Parser() {
        this("rss_0.93");
    }

    protected RSS093Parser(String type) {
        super(type);
    }

    protected String getRSSVersion() {
        return "0.93";
    }

    protected Item parseItem(Element rssRoot, Element eItem) {
        String type;
        Item item = super.parseItem(rssRoot, eItem);
        Element e = eItem.getChild("pubDate", this.getRSSNamespace());
        if (e != null) {
            item.setPubDate(DateParser.parseDate(e.getText()));
        }
        if ((e = eItem.getChild("expirationDate", this.getRSSNamespace())) != null) {
            item.setExpirationDate(DateParser.parseDate(e.getText()));
        }
        if ((e = eItem.getChild("description", this.getRSSNamespace())) != null && (type = e.getAttributeValue("type")) != null) {
            item.getDescription().setType(type);
        }
        return item;
    }
}

