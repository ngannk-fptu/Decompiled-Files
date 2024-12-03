/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.Namespace
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.impl.RSS090Parser;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

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

    public boolean isMyType(Document document) {
        boolean ok = false;
        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();
        List additionalNSs = rssRoot.getAdditionalNamespaces();
        boolean bl = ok = defaultNS != null && defaultNS.equals((Object)this.getRDFNamespace());
        if (ok) {
            if (additionalNSs == null) {
                ok = false;
            } else {
                ok = false;
                for (int i = 0; !ok && i < additionalNSs.size(); ++i) {
                    ok = this.getRSSNamespace().equals(additionalNSs.get(i));
                }
            }
        }
        return ok;
    }

    protected Namespace getRSSNamespace() {
        return Namespace.getNamespace((String)RSS_URI);
    }

    protected Item parseItem(Element rssRoot, Element eItem) {
        String uri;
        Element ce;
        Item item = super.parseItem(rssRoot, eItem);
        Element e = eItem.getChild("description", this.getRSSNamespace());
        if (e != null) {
            item.setDescription(this.parseItemDescription(rssRoot, e));
        }
        if ((ce = eItem.getChild("encoded", this.getContentNamespace())) != null) {
            Content content = new Content();
            content.setType("html");
            content.setValue(ce.getText());
            item.setContent(content);
        }
        if ((uri = eItem.getAttributeValue("about", this.getRDFNamespace())) != null) {
            item.setUri(uri);
        }
        return item;
    }

    protected WireFeed parseChannel(Element rssRoot) {
        Channel channel = (Channel)super.parseChannel(rssRoot);
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

