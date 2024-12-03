/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Content
 *  org.jdom2.Element
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Guid;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.impl.RSS094Generator;
import java.util.List;
import org.jdom2.Content;
import org.jdom2.Element;

public class RSS20Generator
extends RSS094Generator {
    public RSS20Generator() {
        this("rss_2.0", "2.0");
    }

    protected RSS20Generator(String feedType, String version) {
        super(feedType, version);
    }

    @Override
    protected void populateChannel(Channel channel, Element eChannel) {
        int ttl;
        super.populateChannel(channel, eChannel);
        String generator = channel.getGenerator();
        if (generator != null) {
            eChannel.addContent((Content)this.generateSimpleElement("generator", generator));
        }
        if ((ttl = channel.getTtl()) > -1) {
            eChannel.addContent((Content)this.generateSimpleElement("ttl", String.valueOf(ttl)));
        }
        List<Category> categories = channel.getCategories();
        for (Category category : categories) {
            eChannel.addContent((Content)this.generateCategoryElement(category));
        }
        this.generateForeignMarkup(eChannel, channel.getForeignMarkup());
    }

    @Override
    public void populateItem(Item item, Element eItem, int index) {
        Guid guid;
        String comments;
        String author;
        super.populateItem(item, eItem, index);
        Element description = eItem.getChild("description", this.getFeedNamespace());
        if (description != null) {
            description.removeAttribute("type");
        }
        if ((author = item.getAuthor()) != null) {
            eItem.addContent((Content)this.generateSimpleElement("author", author));
        }
        if ((comments = item.getComments()) != null) {
            eItem.addContent((Content)this.generateSimpleElement("comments", comments));
        }
        if ((guid = item.getGuid()) != null) {
            Element eGuid = this.generateSimpleElement("guid", guid.getValue());
            if (!guid.isPermaLink()) {
                eGuid.setAttribute("isPermaLink", "false");
            }
            eItem.addContent((Content)eGuid);
        }
    }
}

