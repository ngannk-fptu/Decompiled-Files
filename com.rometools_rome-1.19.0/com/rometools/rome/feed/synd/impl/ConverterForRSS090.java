/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Element
 */
package com.rometools.rome.feed.synd.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.module.impl.ModuleUtils;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.rss.Source;
import com.rometools.rome.feed.synd.Converter;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.feed.synd.SyndImageImpl;
import com.rometools.rome.feed.synd.SyndLink;
import java.util.ArrayList;
import java.util.List;
import org.jdom2.Element;

public class ConverterForRSS090
implements Converter {
    private final String type;

    public ConverterForRSS090() {
        this("rss_0.9");
    }

    protected ConverterForRSS090(String type) {
        this.type = type;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        List<Item> items;
        syndFeed.setModules(ModuleUtils.cloneModules(feed.getModules()));
        List<Element> foreignMarkup = feed.getForeignMarkup();
        if (!foreignMarkup.isEmpty()) {
            syndFeed.setForeignMarkup(foreignMarkup);
        }
        syndFeed.setStyleSheet(feed.getStyleSheet());
        syndFeed.setEncoding(feed.getEncoding());
        Channel channel = (Channel)feed;
        syndFeed.setTitle(channel.getTitle());
        syndFeed.setLink(channel.getLink());
        syndFeed.setDescription(channel.getDescription());
        Image image = channel.getImage();
        if (image != null) {
            syndFeed.setImage(this.createSyndImage(image));
        }
        if ((items = channel.getItems()) != null) {
            syndFeed.setEntries(this.createSyndEntries(items, syndFeed.isPreservingWireFeed()));
        }
    }

    protected SyndImage createSyndImage(Image rssImage) {
        SyndImageImpl syndImage = new SyndImageImpl();
        syndImage.setTitle(rssImage.getTitle());
        syndImage.setUrl(rssImage.getUrl());
        syndImage.setLink(rssImage.getLink());
        syndImage.setWidth(rssImage.getWidth());
        syndImage.setHeight(rssImage.getHeight());
        return syndImage;
    }

    protected List<SyndEntry> createSyndEntries(List<Item> rssItems, boolean preserveWireItems) {
        ArrayList<SyndEntry> syndEntries = new ArrayList<SyndEntry>();
        for (Item item : rssItems) {
            syndEntries.add(this.createSyndEntry(item, preserveWireItems));
        }
        return syndEntries;
    }

    protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
        SyndEntryImpl syndEntry = new SyndEntryImpl();
        if (preserveWireItem) {
            syndEntry.setWireEntry(item);
        }
        syndEntry.setModules(ModuleUtils.cloneModules(item.getModules()));
        List<Element> foreignMarkup = item.getForeignMarkup();
        if (!foreignMarkup.isEmpty()) {
            syndEntry.setForeignMarkup(foreignMarkup);
        }
        syndEntry.setUri(item.getUri());
        syndEntry.setLink(item.getLink());
        syndEntry.setTitle(item.getTitle());
        syndEntry.setLink(item.getLink());
        syndEntry.setSource(this.createSource(item.getSource()));
        return syndEntry;
    }

    protected SyndFeed createSource(Source source) {
        SyndFeedImpl feed = null;
        if (source != null) {
            feed = new SyndFeedImpl();
            feed.setLink(source.getUrl());
            feed.setUri(source.getUrl());
            feed.setTitle(source.getValue());
        }
        return feed;
    }

    @Override
    public WireFeed createRealFeed(SyndFeed syndFeed) {
        return this.createRealFeed(this.getType(), syndFeed);
    }

    protected WireFeed createRealFeed(String type, SyndFeed syndFeed) {
        List<Element> foreignMarkup;
        List<SyndEntry> sEntries;
        Channel channel = new Channel(type);
        channel.setModules(ModuleUtils.cloneModules(syndFeed.getModules()));
        channel.setStyleSheet(syndFeed.getStyleSheet());
        channel.setEncoding(syndFeed.getEncoding());
        channel.setTitle(syndFeed.getTitle());
        String link = syndFeed.getLink();
        List<SyndLink> links = syndFeed.getLinks();
        if (link != null) {
            channel.setLink(link);
        } else if (!links.isEmpty()) {
            channel.setLink(links.get(0).getHref());
        }
        channel.setDescription(syndFeed.getDescription());
        SyndImage sImage = syndFeed.getImage();
        if (sImage != null) {
            channel.setImage(this.createRSSImage(sImage));
        }
        if ((sEntries = syndFeed.getEntries()) != null) {
            channel.setItems(this.createRSSItems(sEntries));
        }
        if (!(foreignMarkup = syndFeed.getForeignMarkup()).isEmpty()) {
            channel.setForeignMarkup(foreignMarkup);
        }
        return channel;
    }

    protected Image createRSSImage(SyndImage sImage) {
        Image image = new Image();
        image.setTitle(sImage.getTitle());
        image.setUrl(sImage.getUrl());
        image.setLink(sImage.getLink());
        image.setHeight(sImage.getHeight());
        image.setWidth(sImage.getWidth());
        return image;
    }

    protected List<Item> createRSSItems(List<SyndEntry> sEntries) {
        ArrayList<Item> list = new ArrayList<Item>();
        for (SyndEntry syndEntry : sEntries) {
            list.add(this.createRSSItem(syndEntry));
        }
        return list;
    }

    protected Item createRSSItem(SyndEntry sEntry) {
        Item item = new Item();
        item.setModules(ModuleUtils.cloneModules(sEntry.getModules()));
        item.setTitle(sEntry.getTitle());
        item.setLink(sEntry.getLink());
        List<Element> foreignMarkup = sEntry.getForeignMarkup();
        if (!foreignMarkup.isEmpty()) {
            item.setForeignMarkup(foreignMarkup);
        }
        item.setSource(this.createSource(sEntry.getSource()));
        String uri = sEntry.getUri();
        if (uri != null) {
            item.setUri(uri);
        }
        return item;
    }

    protected Source createSource(SyndFeed feed) {
        Source source = null;
        if (feed != null) {
            source = new Source();
            source.setUrl(feed.getUri());
            source.setValue(feed.getTitle());
        }
        return source;
    }
}

