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
import com.rometools.rome.feed.module.Module;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.rss.TextInput;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.impl.BaseWireFeedParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

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

    @Override
    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        Namespace defaultNS = rssRoot.getNamespace();
        List additionalNSs = rssRoot.getAdditionalNamespaces();
        boolean myType = false;
        if (defaultNS != null && defaultNS.equals((Object)this.getRDFNamespace()) && additionalNSs != null) {
            for (Namespace namespace : additionalNSs) {
                if (!this.getRSSNamespace().equals((Object)namespace)) continue;
                myType = true;
                break;
            }
        }
        return myType;
    }

    @Override
    public WireFeed parse(Document document, boolean validate, Locale locale) throws IllegalArgumentException, FeedException {
        if (validate) {
            this.validateFeed(document);
        }
        Element rssRoot = document.getRootElement();
        return this.parseChannel(rssRoot, locale);
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

    protected WireFeed parseChannel(Element rssRoot, Locale locale) {
        Element description;
        Element link;
        Channel channel = new Channel(this.getType());
        channel.setStyleSheet(this.getStyleSheet(rssRoot.getDocument()));
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        Element title = eChannel.getChild("title", this.getRSSNamespace());
        if (title != null) {
            channel.setTitle(title.getText());
        }
        if ((link = eChannel.getChild("link", this.getRSSNamespace())) != null) {
            channel.setLink(link.getText());
        }
        if ((description = eChannel.getChild("description", this.getRSSNamespace())) != null) {
            channel.setDescription(description.getText());
        }
        channel.setImage(this.parseImage(rssRoot));
        channel.setTextInput(this.parseTextInput(rssRoot));
        ArrayList<Module> allFeedModules = new ArrayList<Module>();
        List<Module> rootModules = this.parseFeedModules(rssRoot, locale);
        List<Module> channelModules = this.parseFeedModules(eChannel, locale);
        if (rootModules != null) {
            allFeedModules.addAll(rootModules);
        }
        if (channelModules != null) {
            allFeedModules.addAll(channelModules);
        }
        channel.setModules(allFeedModules);
        channel.setItems(this.parseItems(rssRoot, locale));
        List<Element> foreignMarkup = this.extractForeignMarkup(eChannel, channel, this.getRSSNamespace());
        if (!foreignMarkup.isEmpty()) {
            channel.setForeignMarkup(foreignMarkup);
        }
        return channel;
    }

    protected List<Element> getItems(Element rssRoot) {
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
            Element link;
            Element url;
            image = new Image();
            Element title = eImage.getChild("title", this.getRSSNamespace());
            if (title != null) {
                image.setTitle(title.getText());
            }
            if ((url = eImage.getChild("url", this.getRSSNamespace())) != null) {
                image.setUrl(url.getText());
            }
            if ((link = eImage.getChild("link", this.getRSSNamespace())) != null) {
                image.setLink(link.getText());
            }
        }
        return image;
    }

    protected List<Item> parseItems(Element rssRoot, Locale locale) {
        ArrayList<Item> items = new ArrayList<Item>();
        for (Element item : this.getItems(rssRoot)) {
            items.add(this.parseItem(rssRoot, item, locale));
        }
        return items;
    }

    protected Item parseItem(Element rssRoot, Element eItem, Locale locale) {
        Element link;
        Item item = new Item();
        Element title = eItem.getChild("title", this.getRSSNamespace());
        if (title != null) {
            item.setTitle(title.getText());
        }
        if ((link = eItem.getChild("link", this.getRSSNamespace())) != null) {
            item.setLink(link.getText());
            item.setUri(link.getText());
        }
        item.setModules(this.parseItemModules(eItem, locale));
        List<Element> foreignMarkup = this.extractForeignMarkup(eItem, item, this.getRSSNamespace());
        Iterator<Element> iterator = foreignMarkup.iterator();
        while (iterator.hasNext()) {
            Element element = iterator.next();
            Namespace eNamespace = element.getNamespace();
            String eName = element.getName();
            if (!this.getContentNamespace().equals((Object)eNamespace) || !eName.equals("encoded")) continue;
            iterator.remove();
        }
        if (!foreignMarkup.isEmpty()) {
            item.setForeignMarkup(foreignMarkup);
        }
        return item;
    }

    protected TextInput parseTextInput(Element rssRoot) {
        TextInput textInput = null;
        Element eTextInput = this.getTextInput(rssRoot);
        if (eTextInput != null) {
            Element link;
            Element name;
            Element description;
            textInput = new TextInput();
            Element title = eTextInput.getChild("title", this.getRSSNamespace());
            if (title != null) {
                textInput.setTitle(title.getText());
            }
            if ((description = eTextInput.getChild("description", this.getRSSNamespace())) != null) {
                textInput.setDescription(description.getText());
            }
            if ((name = eTextInput.getChild("name", this.getRSSNamespace())) != null) {
                textInput.setName(name.getText());
            }
            if ((link = eTextInput.getChild("link", this.getRSSNamespace())) != null) {
                textInput.setLink(link.getText());
            }
        }
        return textInput;
    }
}

