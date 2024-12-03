/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Document
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.impl.RSS090Parser;
import java.util.Locale;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class RSS10Parser
extends RSS090Parser {
    private static final String RSS_URI = "http://purl.org/rss/1.0/";
    private static final Namespace RSS_NS = Namespace.getNamespace((String)"http://purl.org/rss/1.0/");

    public RSS10Parser() {
        this("rss_1.0", RSS_NS);
    }

    protected RSS10Parser(String type, Namespace ns) {
        super(type, ns);
    }

    @Override
    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();
        return defaultNS != null && defaultNS.equals((Object)this.getRDFNamespace()) && rssRoot.getChild("channel", this.getRSSNamespace()) != null;
    }

    @Override
    protected Namespace getRSSNamespace() {
        return Namespace.getNamespace((String)RSS_URI);
    }

    @Override
    protected Item parseItem(Element rssRoot, Element eItem, Locale locale) {
        String about;
        Element encoded;
        Item item = super.parseItem(rssRoot, eItem, locale);
        Element description = eItem.getChild("description", this.getRSSNamespace());
        if (description != null) {
            item.setDescription(this.parseItemDescription(rssRoot, description));
        }
        if ((encoded = eItem.getChild("encoded", this.getContentNamespace())) != null) {
            Content content = new Content();
            content.setType("html");
            content.setValue(encoded.getText());
            item.setContent(content);
        }
        if ((about = eItem.getAttributeValue("about", this.getRDFNamespace())) != null) {
            item.setUri(about);
        }
        return item;
    }

    @Override
    protected WireFeed parseChannel(Element rssRoot, Locale locale) {
        Channel channel = (Channel)super.parseChannel(rssRoot, locale);
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        String uri = eChannel.getAttributeValue("about", this.getRDFNamespace());
        if (uri != null) {
            channel.setUri(uri);
        }
        return channel;
    }

    protected Description parseItemDescription(Element rssRoot, Element eDesc) {
        Description desc = new Description();
        desc.setType("text/plain");
        desc.setValue(eDesc.getText());
        return desc;
    }
}

