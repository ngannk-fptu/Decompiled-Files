/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Content
 *  org.jdom2.Element
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.rss.Enclosure;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.impl.DateParser;
import com.rometools.rome.io.impl.RSS092Generator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.jdom2.Content;
import org.jdom2.Element;

public class RSS093Generator
extends RSS092Generator {
    public RSS093Generator() {
        this("rss_0.93", "0.93");
    }

    protected RSS093Generator(String feedType, String version) {
        super(feedType, version);
    }

    @Override
    protected void populateItem(Item item, Element eItem, int index) {
        Date expirationDate;
        super.populateItem(item, eItem, index);
        Date pubDate = item.getPubDate();
        if (pubDate != null) {
            eItem.addContent((Content)this.generateSimpleElement("pubDate", DateParser.formatRFC822(pubDate, Locale.US)));
        }
        if ((expirationDate = item.getExpirationDate()) != null) {
            eItem.addContent((Content)this.generateSimpleElement("expirationDate", DateParser.formatRFC822(expirationDate, Locale.US)));
        }
    }

    @Override
    protected int getNumberOfEnclosures(List<Enclosure> enclosures) {
        return enclosures.size();
    }
}

