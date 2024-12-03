/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Element
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.impl.NumberParser;
import com.rometools.rome.io.impl.RSS093Parser;
import java.util.List;
import java.util.Locale;
import org.jdom2.Element;

public class RSS094Parser
extends RSS093Parser {
    public RSS094Parser() {
        this("rss_0.94");
    }

    protected RSS094Parser(String type) {
        super(type);
    }

    @Override
    protected String getRSSVersion() {
        return "0.94";
    }

    @Override
    protected WireFeed parseChannel(Element rssRoot, Locale locale) {
        Integer ttlValue;
        Channel channel = (Channel)super.parseChannel(rssRoot, locale);
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        List categories = eChannel.getChildren("category", this.getRSSNamespace());
        channel.setCategories(this.parseCategories(categories));
        Element ttl = eChannel.getChild("ttl", this.getRSSNamespace());
        if (ttl != null && ttl.getText() != null && (ttlValue = NumberParser.parseInt(ttl.getText())) != null) {
            channel.setTtl(ttlValue);
        }
        return channel;
    }

    @Override
    public Item parseItem(Element rssRoot, Element eItem, Locale locale) {
        Element comments;
        Element eGuid;
        Item item = super.parseItem(rssRoot, eItem, locale);
        item.setExpirationDate(null);
        Element author = eItem.getChild("author", this.getRSSNamespace());
        if (author != null) {
            item.setAuthor(author.getText());
        }
        if ((eGuid = eItem.getChild("guid", this.getRSSNamespace())) != null) {
            Guid guid = new Guid();
            String att = eGuid.getAttributeValue("isPermaLink");
            if (att != null) {
                guid.setPermaLink(att.equalsIgnoreCase("true"));
            }
            guid.setValue(eGuid.getText());
            item.setGuid(guid);
        }
        if ((comments = eItem.getChild("comments", this.getRSSNamespace())) != null) {
            item.setComments(comments.getText());
        }
        return item;
    }
}

