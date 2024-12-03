/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Lists
 */
package com.rometools.rome.feed.synd.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.module.DCModule;
import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndLink;
import com.rometools.rome.feed.synd.SyndLinkImpl;
import com.rometools.rome.feed.synd.SyndPerson;
import com.rometools.rome.feed.synd.impl.ConverterForRSS093;
import com.rometools.utils.Lists;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ConverterForRSS094
extends ConverterForRSS093 {
    public ConverterForRSS094() {
        this("rss_0.94");
    }

    protected ConverterForRSS094(String type) {
        super(type);
    }

    @Override
    public void copyInto(WireFeed feed, SyndFeed syndFeed) {
        Channel channel = (Channel)feed;
        super.copyInto(channel, syndFeed);
        List<Category> cats = channel.getCategories();
        if (!cats.isEmpty()) {
            LinkedHashSet<SyndCategory> s = new LinkedHashSet<SyndCategory>();
            s.addAll(this.createSyndCategories(cats));
            s.addAll(syndFeed.getCategories());
            syndFeed.setCategories(new ArrayList<SyndCategory>(s));
        }
    }

    @Override
    protected SyndEntry createSyndEntry(Item item, boolean preserveWireItem) {
        List<String> creators;
        SyndEntry syndEntry = super.createSyndEntry(item, preserveWireItem);
        String author = item.getAuthor();
        if (author != null && !(creators = ((DCModule)syndEntry.getModule("http://purl.org/dc/elements/1.1/")).getCreators()).contains(author)) {
            LinkedHashSet<String> s = new LinkedHashSet<String>();
            s.addAll(creators);
            s.add(author);
            creators.clear();
            creators.addAll(s);
        }
        Guid guid = item.getGuid();
        String itemLink = item.getLink();
        if (guid != null) {
            String guidValue = guid.getValue();
            syndEntry.setUri(guidValue);
            if (itemLink == null && guid.isPermaLink()) {
                syndEntry.setLink(guidValue);
            }
        } else {
            syndEntry.setUri(itemLink);
        }
        if (item.getComments() != null) {
            SyndLinkImpl comments = new SyndLinkImpl();
            comments.setRel("comments");
            comments.setHref(item.getComments());
            comments.setType("text/html");
        }
        return syndEntry;
    }

    @Override
    protected WireFeed createRealFeed(String type, SyndFeed syndFeed) {
        Channel channel = (Channel)super.createRealFeed(type, syndFeed);
        List<SyndCategory> cats = syndFeed.getCategories();
        if (!cats.isEmpty()) {
            channel.setCategories(this.createRSSCategories(cats));
        }
        return channel;
    }

    @Override
    protected Item createRSSItem(SyndEntry sEntry) {
        Item item = super.createRSSItem(sEntry);
        List<SyndPerson> authors = sEntry.getAuthors();
        if (Lists.isNotEmpty(authors)) {
            SyndPerson author = authors.get(0);
            item.setAuthor(author.getEmail());
        }
        Guid guid = null;
        String uri = sEntry.getUri();
        String link = sEntry.getLink();
        if (uri != null) {
            guid = new Guid();
            guid.setPermaLink(false);
            guid.setValue(uri);
        } else if (link != null) {
            guid = new Guid();
            guid.setPermaLink(true);
            guid.setValue(link);
        }
        item.setGuid(guid);
        SyndLink comments = sEntry.findRelatedLink("comments");
        if (comments != null && (comments.getType() == null || comments.getType().endsWith("html"))) {
            item.setComments(comments.getHref());
        }
        return item;
    }
}

