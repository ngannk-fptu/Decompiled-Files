/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 */
package com.rometools.rome.feed.synd.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.impl.ConverterForRSS090;
import com.rometools.utils.Lists;
import java.util.ArrayList;
import java.util.List;

public class ConverterForRSS10
extends ConverterForRSS090 {
    public ConverterForRSS10() {
        this("rss_1.0");
    }

    protected ConverterForRSS10(String type) {
        super(type);
    }

    @Override
    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        Channel channel = (Channel)feed;
        super.copyInto(channel, syndFeed);
        String uri = channel.getUri();
        if (uri != null) {
            syndFeed.setUri(uri);
        } else {
            String link = channel.getLink();
            syndFeed.setUri(link);
        }
    }

    @Override
    protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
        Content cont;
        SyndEntry syndEntry = super.createSyndEntry(item, preserveWireItem);
        Description desc = item.getDescription();
        if (desc != null) {
            SyndContentImpl descContent = new SyndContentImpl();
            descContent.setType(desc.getType());
            descContent.setValue(desc.getValue());
            syndEntry.setDescription(descContent);
        }
        if ((cont = item.getContent()) != null) {
            SyndContentImpl contContent = new SyndContentImpl();
            contContent.setType(cont.getType());
            contContent.setValue(cont.getValue());
            ArrayList<SyndContent> contents = new ArrayList<SyndContent>();
            contents.add(contContent);
            syndEntry.setContents(contents);
        }
        return syndEntry;
    }

    @Override
    protected WireFeed createRealFeed(String type, SyndFeed syndFeed) {
        Channel channel = (Channel)super.createRealFeed(type, syndFeed);
        String uri = syndFeed.getUri();
        if (uri != null) {
            channel.setUri(uri);
        } else {
            String link = syndFeed.getLink();
            channel.setUri(link);
        }
        return channel;
    }

    @Override
    protected Item createRSSItem(SyndEntry sEntry) {
        String uri;
        List<SyndContent> contents;
        Item item = super.createRSSItem(sEntry);
        SyndContent desc = sEntry.getDescription();
        if (desc != null) {
            item.setDescription(this.createItemDescription(desc));
        }
        if (Lists.isNotEmpty(contents = sEntry.getContents())) {
            item.setContent(this.createItemContent(contents.get(0)));
        }
        if ((uri = sEntry.getUri()) != null) {
            item.setUri(uri);
        }
        return item;
    }

    protected Description createItemDescription(SyndContent sContent) {
        Description desc = new Description();
        desc.setValue(sContent.getValue());
        desc.setType(sContent.getType());
        return desc;
    }

    protected Content createItemContent(SyndContent sContent) {
        Content cont = new Content();
        cont.setValue(sContent.getValue());
        cont.setType(sContent.getType());
        return cont;
    }
}

