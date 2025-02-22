/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.impl.ConverterForRSS090;
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

    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        Channel channel = (Channel)feed;
        super.copyInto(channel, syndFeed);
        if (channel.getUri() != null) {
            syndFeed.setUri(channel.getUri());
        } else {
            syndFeed.setUri(channel.getLink());
        }
    }

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
            ArrayList<SyndContentImpl> contents = new ArrayList<SyndContentImpl>();
            contents.add(contContent);
            syndEntry.setContents(contents);
        }
        return syndEntry;
    }

    protected WireFeed createRealFeed(String type, SyndFeed syndFeed) {
        Channel channel = (Channel)super.createRealFeed(type, syndFeed);
        if (syndFeed.getUri() != null) {
            channel.setUri(syndFeed.getUri());
        } else {
            channel.setUri(syndFeed.getLink());
        }
        return channel;
    }

    protected Item createRSSItem(SyndEntry sEntry) {
        String uri;
        List contents;
        Item item = super.createRSSItem(sEntry);
        SyndContent desc = sEntry.getDescription();
        if (desc != null) {
            item.setDescription(this.createItemDescription(desc));
        }
        if ((contents = sEntry.getContents()) != null && contents.size() > 0) {
            item.setContent(this.createItemContent((SyndContent)contents.get(0)));
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

