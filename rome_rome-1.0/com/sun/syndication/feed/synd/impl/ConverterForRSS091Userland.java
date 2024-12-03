/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.synd.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.feed.synd.impl.ConverterForRSS090;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ConverterForRSS091Userland
extends ConverterForRSS090 {
    public ConverterForRSS091Userland() {
        this("rss_0.91U");
    }

    protected ConverterForRSS091Userland(String type) {
        super(type);
    }

    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        List creators;
        Channel channel = (Channel)feed;
        super.copyInto(channel, syndFeed);
        syndFeed.setLanguage(channel.getLanguage());
        syndFeed.setCopyright(channel.getCopyright());
        Date pubDate = channel.getPubDate();
        if (pubDate != null) {
            syndFeed.setPublishedDate(pubDate);
        } else if (channel.getLastBuildDate() != null) {
            syndFeed.setPublishedDate(channel.getLastBuildDate());
        }
        String author = channel.getManagingEditor();
        if (author != null && !(creators = ((DCModule)syndFeed.getModule("http://purl.org/dc/elements/1.1/")).getCreators()).contains(author)) {
            HashSet<String> s = new HashSet<String>();
            s.addAll(creators);
            s.add(author);
            creators.clear();
            creators.addAll(s);
        }
    }

    protected SyndImage createSyndImage(Image rssImage) {
        SyndImage syndImage = super.createSyndImage(rssImage);
        syndImage.setDescription(rssImage.getDescription());
        return syndImage;
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
            SyndContentImpl content = new SyndContentImpl();
            content.setType(cont.getType());
            content.setValue(cont.getValue());
            ArrayList<SyndContentImpl> syndContents = new ArrayList<SyndContentImpl>();
            syndContents.add(content);
            syndEntry.setContents(syndContents);
        }
        return syndEntry;
    }

    protected WireFeed createRealFeed(String type, SyndFeed syndFeed) {
        Channel channel = (Channel)super.createRealFeed(type, syndFeed);
        channel.setLanguage(syndFeed.getLanguage());
        channel.setCopyright(syndFeed.getCopyright());
        channel.setPubDate(syndFeed.getPublishedDate());
        if (syndFeed.getAuthors() != null && syndFeed.getAuthors().size() > 0) {
            SyndPerson author = (SyndPerson)syndFeed.getAuthors().get(0);
            channel.setManagingEditor(author.getName());
        }
        return channel;
    }

    protected Image createRSSImage(SyndImage sImage) {
        Image image = super.createRSSImage(sImage);
        image.setDescription(sImage.getDescription());
        return image;
    }

    protected Item createRSSItem(SyndEntry sEntry) {
        List contents;
        Item item = super.createRSSItem(sEntry);
        SyndContent sContent = sEntry.getDescription();
        if (sContent != null) {
            item.setDescription(this.createItemDescription(sContent));
        }
        if ((contents = sEntry.getContents()) != null && contents.size() > 0) {
            SyndContent syndContent = (SyndContent)contents.get(0);
            Content cont = new Content();
            cont.setValue(syndContent.getValue());
            cont.setType(syndContent.getType());
            item.setContent(cont);
        }
        return item;
    }

    protected Description createItemDescription(SyndContent sContent) {
        Description desc = new Description();
        desc.setValue(sContent.getValue());
        desc.setType(sContent.getType());
        return desc;
    }
}

