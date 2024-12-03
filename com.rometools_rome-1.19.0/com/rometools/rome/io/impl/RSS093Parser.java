/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Element
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.impl.DateParser;
import com.rometools.rome.io.impl.RSS092Parser;
import java.util.Locale;
import org.jdom2.Element;

public class RSS093Parser
extends RSS092Parser {
    public RSS093Parser() {
        this("rss_0.93");
    }

    protected RSS093Parser(String type) {
        super(type);
    }

    @Override
    protected String getRSSVersion() {
        return "0.93";
    }

    @Override
    protected Item parseItem(Element rssRoot, Element eItem, Locale locale) {
        String type;
        Element description;
        Element expirationDate;
        Item item = super.parseItem(rssRoot, eItem, locale);
        Element pubDate = eItem.getChild("pubDate", this.getRSSNamespace());
        if (pubDate != null) {
            item.setPubDate(DateParser.parseDate(pubDate.getText(), locale));
        }
        if ((expirationDate = eItem.getChild("expirationDate", this.getRSSNamespace())) != null) {
            item.setExpirationDate(DateParser.parseDate(expirationDate.getText(), locale));
        }
        if ((description = eItem.getChild("description", this.getRSSNamespace())) != null && (type = description.getAttributeValue("type")) != null) {
            item.getDescription().setType(type);
        }
        return item;
    }
}

