/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Content
 *  org.jdom.Element
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.impl.DateParser;
import com.sun.syndication.io.impl.RSS092Generator;
import java.util.Date;
import java.util.List;
import org.jdom.Content;
import org.jdom.Element;

public class RSS093Generator
extends RSS092Generator {
    public RSS093Generator() {
        this("rss_0.93", "0.93");
    }

    protected RSS093Generator(String feedType, String version) {
        super(feedType, version);
    }

    protected void populateItem(Item item, Element eItem, int index) {
        Date expirationDate;
        super.populateItem(item, eItem, index);
        Date pubDate = item.getPubDate();
        if (pubDate != null) {
            eItem.addContent((Content)this.generateSimpleElement("pubDate", DateParser.formatRFC822(pubDate)));
        }
        if ((expirationDate = item.getExpirationDate()) != null) {
            eItem.addContent((Content)this.generateSimpleElement("expirationDate", DateParser.formatRFC822(expirationDate)));
        }
    }

    protected int getNumberOfEnclosures(List enclosures) {
        return enclosures.size();
    }
}

