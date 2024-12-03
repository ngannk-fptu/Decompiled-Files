/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Content
 *  org.jdom.Element
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Category;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Guid;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.impl.RSS094Generator;
import java.util.List;
import org.jdom.Content;
import org.jdom.Element;

public class RSS20Generator
extends RSS094Generator {
    public RSS20Generator() {
        this("rss_2.0", "2.0");
    }

    protected RSS20Generator(String feedType, String version) {
        super(feedType, version);
    }

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
        List categories = channel.getCategories();
        for (int i = 0; i < categories.size(); ++i) {
            eChannel.addContent((Content)this.generateCategoryElement((Category)categories.get(i)));
        }
    }

    public void populateItem(Item item, Element eItem, int index) {
        Guid guid;
        String comments;
        String author;
        super.populateItem(item, eItem, index);
        Element eDescription = eItem.getChild("description", this.getFeedNamespace());
        if (eDescription != null) {
            eDescription.removeAttribute("type");
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

