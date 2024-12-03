/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 */
package com.rometools.rome.feed.synd.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.impl.ConverterForRSS090;
import com.rometools.utils.Lists;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

public class ConverterForRSS091Userland
extends ConverterForRSS090 {
    public ConverterForRSS091Userland() {
        this("rss_0.91U");
    }

    protected ConverterForRSS091Userland(String type) {
        super(type);
    }

    @Override
    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        List<String> creators;
        Channel channel = (Channel)feed;
        super.copyInto(channel, syndFeed);
        syndFeed.setLanguage(channel.getLanguage());
        syndFeed.setCopyright(channel.getCopyright());
        syndFeed.setDocs(channel.getDocs());
        syndFeed.setManagingEditor(channel.getManagingEditor());
        syndFeed.setWebMaster(channel.getWebMaster());
        syndFeed.setGenerator(channel.getGenerator());
        Date pubDate = channel.getPubDate();
        if (pubDate != null) {
            syndFeed.setPublishedDate(pubDate);
        } else if (channel.getLastBuildDate() != null) {
            syndFeed.setPublishedDate(channel.getLastBuildDate());
        }
        String author = channel.getManagingEditor();
        if (author != null && !(creators = ((DCModule)syndFeed.getModule("http://purl.org/dc/elements/1.1/")).getCreators()).contains(author)) {
            LinkedHashSet<String> s = new LinkedHashSet<String>();
            s.addAll(creators);
            s.add(author);
            creators.clear();
            creators.addAll(s);
        }
    }

    protected Description createItemDescription(SyndContent sContent) {
        Description desc = new Description();
        desc.setValue(sContent.getValue());
        desc.setType(sContent.getType());
        return desc;
    }

    @Override
    protected Image createRSSImage(SyndImage sImage) {
        Image image = super.createRSSImage(sImage);
        image.setDescription(sImage.getDescription());
        return image;
    }

    @Override
    protected Item createRSSItem(SyndEntry sEntry) {
        List<SyndContent> contents;
        Item item = super.createRSSItem(sEntry);
        item.setComments(sEntry.getComments());
        SyndContent sContent = sEntry.getDescription();
        if (sContent != null) {
            item.setDescription(this.createItemDescription(sContent));
        }
        if (Lists.isNotEmpty(contents = sEntry.getContents())) {
            SyndContent syndContent = contents.get(0);
            Content cont = new Content();
            cont.setValue(syndContent.getValue());
            cont.setType(syndContent.getType());
            item.setContent(cont);
        }
        return item;
    }

    @Override
    protected WireFeed createRealFeed(String type, SyndFeed syndFeed) {
        Channel channel = (Channel)super.createRealFeed(type, syndFeed);
        channel.setLanguage(syndFeed.getLanguage());
        channel.setCopyright(syndFeed.getCopyright());
        channel.setPubDate(syndFeed.getPublishedDate());
        channel.setDocs(syndFeed.getDocs());
        channel.setManagingEditor(syndFeed.getManagingEditor());
        channel.setWebMaster(syndFeed.getWebMaster());
        channel.setGenerator(syndFeed.getGenerator());
        List<SyndPerson> authors = syndFeed.getAuthors();
        if (Lists.isNotEmpty(authors)) {
            SyndPerson author = authors.get(0);
            channel.setManagingEditor(author.getName());
        }
        return channel;
    }

    @Override
    protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
        Content cont;
        SyndEntry syndEntry = super.createSyndEntry(item, preserveWireItem);
        Description desc = item.getDescription();
        syndEntry.setComments(item.getComments());
        if (syndEntry.getPublishedDate() == null) {
            syndEntry.setPublishedDate(item.getPubDate());
        }
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
            ArrayList<SyndContent> syndContents = new ArrayList<SyndContent>();
            syndContents.add(content);
            syndEntry.setContents(syndContents);
        }
        return syndEntry;
    }

    @Override
    protected SyndImage createSyndImage(Image rssImage) {
        SyndImage syndImage = super.createSyndImage(rssImage);
        syndImage.setDescription(rssImage.getDescription());
        return syndImage;
    }
}

