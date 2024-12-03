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
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.rss.TextInput;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.impl.BaseWireFeedParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class RSS090Parser
extends BaseWireFeedParser {
    private static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String RSS_URI = "http://my.netscape.com/rdf/simple/0.9/";
    private static final String CONTENT_URI = "http://purl.org/rss/1.0/modules/content/";
    private static final Namespace RDF_NS = Namespace.getNamespace((String)"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    private static final Namespace RSS_NS = Namespace.getNamespace((String)"http://my.netscape.com/rdf/simple/0.9/");
    private static final Namespace CONTENT_NS = Namespace.getNamespace((String)"http://purl.org/rss/1.0/modules/content/");

    public RSS090Parser() {
        this("rss_0.9", RSS_NS);
    }

    protected RSS090Parser(String type, Namespace ns) {
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

    public WireFeed parse(Document document, boolean validate) throws IllegalArgumentException, FeedException {
        if (validate) {
            this.validateFeed(document);
        }
        Element rssRoot = document.getRootElement();
        return this.parseChannel(rssRoot);
    }

    protected void validateFeed(Document document) throws FeedException {
    }

    protected Namespace getRSSNamespace() {
        return RSS_NS;
    }

    protected Namespace getRDFNamespace() {
        return RDF_NS;
    }

    protected Namespace getContentNamespace() {
        return CONTENT_NS;
    }

    protected WireFeed parseChannel(Element rssRoot) {
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        Channel channel = new Channel(this.getType());
        Element e = eChannel.getChild("title", this.getRSSNamespace());
        if (e != null) {
            channel.setTitle(e.getText());
        }
        if ((e = eChannel.getChild("link", this.getRSSNamespace())) != null) {
            channel.setLink(e.getText());
        }
        if ((e = eChannel.getChild("description", this.getRSSNamespace())) != null) {
            channel.setDescription(e.getText());
        }
        channel.setImage(this.parseImage(rssRoot));
        channel.setTextInput(this.parseTextInput(rssRoot));
        ArrayList allFeedModules = new ArrayList();
        List rootModules = this.parseFeedModules(rssRoot);
        List channelModules = this.parseFeedModules(eChannel);
        if (rootModules != null) {
            allFeedModules.addAll(rootModules);
        }
        if (channelModules != null) {
            allFeedModules.addAll(channelModules);
        }
        channel.setModules(allFeedModules);
        channel.setItems(this.parseItems(rssRoot));
        List foreignMarkup = this.extractForeignMarkup(eChannel, channel, this.getRSSNamespace());
        if (foreignMarkup.size() > 0) {
            channel.setForeignMarkup(foreignMarkup);
        }
        return channel;
    }

    protected List getItems(Element rssRoot) {
        return rssRoot.getChildren("item", this.getRSSNamespace());
    }

    protected Element getImage(Element rssRoot) {
        return rssRoot.getChild("image", this.getRSSNamespace());
    }

    protected Element getTextInput(Element rssRoot) {
        return rssRoot.getChild("textinput", this.getRSSNamespace());
    }

    protected Image parseImage(Element rssRoot) {
        Image image = null;
        Element eImage = this.getImage(rssRoot);
        if (eImage != null) {
            image = new Image();
            Element e = eImage.getChild("title", this.getRSSNamespace());
            if (e != null) {
                image.setTitle(e.getText());
            }
            if ((e = eImage.getChild("url", this.getRSSNamespace())) != null) {
                image.setUrl(e.getText());
            }
            if ((e = eImage.getChild("link", this.getRSSNamespace())) != null) {
                image.setLink(e.getText());
            }
        }
        return image;
    }

    protected List parseItems(Element rssRoot) {
        List eItems = this.getItems(rssRoot);
        ArrayList<Item> items = new ArrayList<Item>();
        Iterator i = eItems.iterator();
        while (i.hasNext()) {
            Element eItem = (Element)i.next();
            items.add(this.parseItem(rssRoot, eItem));
        }
        return items;
    }

    protected Item parseItem(Element rssRoot, Element eItem) {
        Item item = new Item();
        Element e = eItem.getChild("title", this.getRSSNamespace());
        if (e != null) {
            item.setTitle(e.getText());
        }
        if ((e = eItem.getChild("link", this.getRSSNamespace())) != null) {
            item.setLink(e.getText());
            item.setUri(e.getText());
        }
        item.setModules(this.parseItemModules(eItem));
        List foreignMarkup = this.extractForeignMarkup(eItem, item, this.getRSSNamespace());
        Iterator iterator = foreignMarkup.iterator();
        while (iterator.hasNext()) {
            Element ie = (Element)iterator.next();
            if (!this.getContentNamespace().equals((Object)ie.getNamespace()) || !ie.getName().equals("encoded")) continue;
            iterator.remove();
        }
        if (foreignMarkup.size() > 0) {
            item.setForeignMarkup(foreignMarkup);
        }
        return item;
    }

    protected TextInput parseTextInput(Element rssRoot) {
        TextInput textInput = null;
        Element eTextInput = this.getTextInput(rssRoot);
        if (eTextInput != null) {
            textInput = new TextInput();
            Element e = eTextInput.getChild("title", this.getRSSNamespace());
            if (e != null) {
                textInput.setTitle(e.getText());
            }
            if ((e = eTextInput.getChild("description", this.getRSSNamespace())) != null) {
                textInput.setDescription(e.getText());
            }
            if ((e = eTextInput.getChild("name", this.getRSSNamespace())) != null) {
                textInput.setName(e.getText());
            }
            if ((e = eTextInput.getChild("link", this.getRSSNamespace())) != null) {
                textInput.setLink(e.getText());
            }
        }
        return textInput;
    }
}

