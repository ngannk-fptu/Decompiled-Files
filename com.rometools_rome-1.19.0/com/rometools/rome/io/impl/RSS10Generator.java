/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Content
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.impl.RSS090Generator;
import java.util.List;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class RSS10Generator
extends RSS090Generator {
    private static final String RSS_URI = "http://purl.org/rss/1.0/";
    private static final Namespace RSS_NS = Namespace.getNamespace((String)"http://purl.org/rss/1.0/");

    public RSS10Generator() {
        super("rss_1.0");
    }

    protected RSS10Generator(String feedType) {
        super(feedType);
    }

    @Override
    protected Namespace getFeedNamespace() {
        return RSS_NS;
    }

    @Override
    protected void populateChannel(Channel channel, Element eChannel) {
        List<Item> items;
        super.populateChannel(channel, eChannel);
        String channelUri = channel.getUri();
        if (channelUri != null) {
            eChannel.setAttribute("about", channelUri, this.getRDFNamespace());
        }
        if (!(items = channel.getItems()).isEmpty()) {
            Element eItems = new Element("items", this.getFeedNamespace());
            Element eSeq = new Element("Seq", this.getRDFNamespace());
            for (Item item : items) {
                Element lis = new Element("li", this.getRDFNamespace());
                String uri = item.getUri();
                if (uri != null) {
                    lis.setAttribute("resource", uri, this.getRDFNamespace());
                }
                eSeq.addContent((Content)lis);
            }
            eItems.addContent((Content)eSeq);
            eChannel.addContent((Content)eItems);
        }
    }

    @Override
    protected void populateItem(Item item, Element eItem, int index) {
        super.populateItem(item, eItem, index);
        String link = item.getLink();
        String uri = item.getUri();
        if (uri != null) {
            eItem.setAttribute("about", uri, this.getRDFNamespace());
        } else if (link != null) {
            eItem.setAttribute("about", link, this.getRDFNamespace());
        }
        Description description = item.getDescription();
        if (description != null) {
            eItem.addContent((Content)this.generateSimpleElement("description", description.getValue()));
        }
        if (item.getModule(this.getContentNamespace().getURI()) == null && item.getContent() != null) {
            Element elem = new Element("encoded", this.getContentNamespace());
            elem.addContent(item.getContent().getValue());
            eItem.addContent((Content)elem);
        }
    }

    @Override
    protected void checkChannelConstraints(Element eChannel) throws FeedException {
        this.checkNotNullAndLength(eChannel, "title", 0, -1);
        this.checkNotNullAndLength(eChannel, "description", 0, -1);
        this.checkNotNullAndLength(eChannel, "link", 0, -1);
    }

    @Override
    protected void checkImageConstraints(Element eImage) throws FeedException {
        this.checkNotNullAndLength(eImage, "title", 0, -1);
        this.checkNotNullAndLength(eImage, "url", 0, -1);
        this.checkNotNullAndLength(eImage, "link", 0, -1);
    }

    @Override
    protected void checkTextInputConstraints(Element eTextInput) throws FeedException {
        this.checkNotNullAndLength(eTextInput, "title", 0, -1);
        this.checkNotNullAndLength(eTextInput, "description", 0, -1);
        this.checkNotNullAndLength(eTextInput, "name", 0, -1);
        this.checkNotNullAndLength(eTextInput, "link", 0, -1);
    }

    @Override
    protected void checkItemsConstraints(Element parent) throws FeedException {
    }

    @Override
    protected void checkItemConstraints(Element eItem) throws FeedException {
        this.checkNotNullAndLength(eItem, "title", 0, -1);
        this.checkNotNullAndLength(eItem, "link", 0, -1);
    }
}

