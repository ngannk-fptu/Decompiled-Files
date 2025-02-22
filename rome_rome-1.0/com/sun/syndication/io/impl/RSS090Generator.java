/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Content
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
import com.sun.syndication.io.impl.BaseWireFeedGenerator;
import java.util.List;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class RSS090Generator
extends BaseWireFeedGenerator {
    private static final String RDF_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private static final String RSS_URI = "http://my.netscape.com/rdf/simple/0.9/";
    private static final String CONTENT_URI = "http://purl.org/rss/1.0/modules/content/";
    private static final Namespace RDF_NS = Namespace.getNamespace((String)"rdf", (String)"http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    private static final Namespace RSS_NS = Namespace.getNamespace((String)"http://my.netscape.com/rdf/simple/0.9/");
    private static final Namespace CONTENT_NS = Namespace.getNamespace((String)"content", (String)"http://purl.org/rss/1.0/modules/content/");

    public RSS090Generator() {
        this("rss_0.9");
    }

    protected RSS090Generator(String type) {
        super(type);
    }

    public Document generate(WireFeed feed) throws FeedException {
        Channel channel = (Channel)feed;
        Element root = this.createRootElement(channel);
        this.populateFeed(channel, root);
        RSS090Generator.purgeUnusedNamespaceDeclarations(root);
        return this.createDocument(root);
    }

    protected Namespace getFeedNamespace() {
        return RSS_NS;
    }

    protected Namespace getRDFNamespace() {
        return RDF_NS;
    }

    protected Namespace getContentNamespace() {
        return CONTENT_NS;
    }

    protected Document createDocument(Element root) {
        return new Document(root);
    }

    protected Element createRootElement(Channel channel) {
        Element root = new Element("RDF", this.getRDFNamespace());
        root.addNamespaceDeclaration(this.getFeedNamespace());
        root.addNamespaceDeclaration(this.getRDFNamespace());
        root.addNamespaceDeclaration(this.getContentNamespace());
        this.generateModuleNamespaceDefs(root);
        return root;
    }

    protected void populateFeed(Channel channel, Element parent) throws FeedException {
        this.addChannel(channel, parent);
        this.addImage(channel, parent);
        this.addTextInput(channel, parent);
        this.addItems(channel, parent);
        this.generateForeignMarkup(parent, (List)channel.getForeignMarkup());
    }

    protected void addChannel(Channel channel, Element parent) throws FeedException {
        Element eChannel = new Element("channel", this.getFeedNamespace());
        this.populateChannel(channel, eChannel);
        this.checkChannelConstraints(eChannel);
        parent.addContent((Content)eChannel);
        this.generateFeedModules(channel.getModules(), eChannel);
    }

    protected void populateChannel(Channel channel, Element eChannel) {
        String description;
        String link;
        String title = channel.getTitle();
        if (title != null) {
            eChannel.addContent((Content)this.generateSimpleElement("title", title));
        }
        if ((link = channel.getLink()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("link", link));
        }
        if ((description = channel.getDescription()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("description", description));
        }
    }

    protected void checkNotNullAndLength(Element parent, String childName, int minLen, int maxLen) throws FeedException {
        Element child = parent.getChild(childName, this.getFeedNamespace());
        if (child == null) {
            throw new FeedException("Invalid " + this.getType() + " feed, missing " + parent.getName() + " " + childName);
        }
        this.checkLength(parent, childName, minLen, maxLen);
    }

    protected void checkLength(Element parent, String childName, int minLen, int maxLen) throws FeedException {
        Element child = parent.getChild(childName, this.getFeedNamespace());
        if (child != null) {
            if (minLen > 0 && child.getText().length() < minLen) {
                throw new FeedException("Invalid " + this.getType() + " feed, " + parent.getName() + " " + childName + "short of " + minLen + " length");
            }
            if (maxLen > -1 && child.getText().length() > maxLen) {
                throw new FeedException("Invalid " + this.getType() + " feed, " + parent.getName() + " " + childName + "exceeds " + maxLen + " length");
            }
        }
    }

    protected void addImage(Channel channel, Element parent) throws FeedException {
        Image image = channel.getImage();
        if (image != null) {
            Element eImage = new Element("image", this.getFeedNamespace());
            this.populateImage(image, eImage);
            this.checkImageConstraints(eImage);
            parent.addContent((Content)eImage);
        }
    }

    protected void populateImage(Image image, Element eImage) {
        String link;
        String url;
        String title = image.getTitle();
        if (title != null) {
            eImage.addContent((Content)this.generateSimpleElement("title", title));
        }
        if ((url = image.getUrl()) != null) {
            eImage.addContent((Content)this.generateSimpleElement("url", url));
        }
        if ((link = image.getLink()) != null) {
            eImage.addContent((Content)this.generateSimpleElement("link", link));
        }
    }

    protected String getTextInputLabel() {
        return "textInput";
    }

    protected void addTextInput(Channel channel, Element parent) throws FeedException {
        TextInput textInput = channel.getTextInput();
        if (textInput != null) {
            Element eTextInput = new Element(this.getTextInputLabel(), this.getFeedNamespace());
            this.populateTextInput(textInput, eTextInput);
            this.checkTextInputConstraints(eTextInput);
            parent.addContent((Content)eTextInput);
        }
    }

    protected void populateTextInput(TextInput textInput, Element eTextInput) {
        String link;
        String name;
        String description;
        String title = textInput.getTitle();
        if (title != null) {
            eTextInput.addContent((Content)this.generateSimpleElement("title", title));
        }
        if ((description = textInput.getDescription()) != null) {
            eTextInput.addContent((Content)this.generateSimpleElement("description", description));
        }
        if ((name = textInput.getName()) != null) {
            eTextInput.addContent((Content)this.generateSimpleElement("name", name));
        }
        if ((link = textInput.getLink()) != null) {
            eTextInput.addContent((Content)this.generateSimpleElement("link", link));
        }
    }

    protected void addItems(Channel channel, Element parent) throws FeedException {
        List items = channel.getItems();
        for (int i = 0; i < items.size(); ++i) {
            this.addItem((Item)items.get(i), parent, i);
        }
        this.checkItemsConstraints(parent);
    }

    protected void addItem(Item item, Element parent, int index) throws FeedException {
        Element eItem = new Element("item", this.getFeedNamespace());
        this.populateItem(item, eItem, index);
        this.checkItemConstraints(eItem);
        this.generateItemModules(item.getModules(), eItem);
        parent.addContent((Content)eItem);
    }

    protected void populateItem(Item item, Element eItem, int index) {
        String link;
        String title = item.getTitle();
        if (title != null) {
            eItem.addContent((Content)this.generateSimpleElement("title", title));
        }
        if ((link = item.getLink()) != null) {
            eItem.addContent((Content)this.generateSimpleElement("link", link));
        }
        this.generateForeignMarkup(eItem, (List)item.getForeignMarkup());
    }

    protected Element generateSimpleElement(String name, String value) {
        Element element = new Element(name, this.getFeedNamespace());
        element.addContent(value);
        return element;
    }

    protected void checkChannelConstraints(Element eChannel) throws FeedException {
        this.checkNotNullAndLength(eChannel, "title", 0, 40);
        this.checkNotNullAndLength(eChannel, "description", 0, 500);
        this.checkNotNullAndLength(eChannel, "link", 0, 500);
    }

    protected void checkImageConstraints(Element eImage) throws FeedException {
        this.checkNotNullAndLength(eImage, "title", 0, 40);
        this.checkNotNullAndLength(eImage, "url", 0, 500);
        this.checkNotNullAndLength(eImage, "link", 0, 500);
    }

    protected void checkTextInputConstraints(Element eTextInput) throws FeedException {
        this.checkNotNullAndLength(eTextInput, "title", 0, 40);
        this.checkNotNullAndLength(eTextInput, "description", 0, 100);
        this.checkNotNullAndLength(eTextInput, "name", 0, 500);
        this.checkNotNullAndLength(eTextInput, "link", 0, 500);
    }

    protected void checkItemsConstraints(Element parent) throws FeedException {
        int count = parent.getChildren("item", this.getFeedNamespace()).size();
        if (count < 1 || count > 15) {
            throw new FeedException("Invalid " + this.getType() + " feed, item count is " + count + " it must be between 1 an 15");
        }
    }

    protected void checkItemConstraints(Element eItem) throws FeedException {
        this.checkNotNullAndLength(eItem, "title", 0, 100);
        this.checkNotNullAndLength(eItem, "link", 0, 500);
    }
}

