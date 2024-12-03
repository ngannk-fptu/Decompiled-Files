/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd.impl;

import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.impl.ConverterForRSS092;
import java.util.Date;

public class ConverterForRSS093
extends ConverterForRSS092 {
    public ConverterForRSS093() {
        this("rss_0.93");
    }

    protected ConverterForRSS093(String type) {
        super(type);
    }

    @Override
    protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
        SyndEntry syndEntry = super.createSyndEntry(item, preserveWireItem);
        Date pubDate = item.getPubDate();
        Date publishedDate = syndEntry.getPublishedDate();
        if (pubDate != null && publishedDate == null) {
            syndEntry.setPublishedDate(pubDate);
        }
        return syndEntry;
    }

    @Override
    protected Item createRSSItem(SyndEntry sEntry) {
        Item item = super.createRSSItem(sEntry);
        item.setPubDate(sEntry.getPublishedDate());
        return item;
    }
}

