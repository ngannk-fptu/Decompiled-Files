/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Attribute
 *  org.jdom.Content
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.Namespace
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.impl.DateParser;
import com.sun.syndication.io.impl.RSS090Generator;
import java.util.Date;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class RSS091UserlandGenerator
extends RSS090Generator {
    private String _version;

    public RSS091UserlandGenerator() {
        this("rss_0.91U", "0.91");
    }

    protected RSS091UserlandGenerator(String type, String version) {
        super(type);
        this._version = version;
    }

    protected String getVersion() {
        return this._version;
    }

    protected Namespace getFeedNamespace() {
        return Namespace.NO_NAMESPACE;
    }

    protected Document createDocument(Element root) {
        return new Document(root);
    }

    protected Element createRootElement(Channel channel) {
        Element root = new Element("rss", this.getFeedNamespace());
        Attribute version = new Attribute("version", this.getVersion());
        root.setAttribute(version);
        root.addNamespaceDeclaration(this.getContentNamespace());
        this.generateModuleNamespaceDefs(root);
        return root;
    }

    protected void populateFeed(Channel channel, Element parent) throws FeedException {
        this.addChannel(channel, parent);
    }

    protected void addChannel(Channel channel, Element parent) throws FeedException {
        super.addChannel(channel, parent);
        Element eChannel = parent.getChild("channel", this.getFeedNamespace());
        this.addImage(channel, eChannel);
        this.addTextInput(channel, eChannel);
        this.addItems(channel, eChannel);
    }

    protected void populateChannel(Channel channel, Element eChannel) {
        List skipDays;
        List skipHours;
        String webMaster;
        String managingEditor;
        String docs;
        Date lastBuildDate;
        Date pubDate;
        String copyright;
        String rating;
        super.populateChannel(channel, eChannel);
        String language = channel.getLanguage();
        if (language != null) {
            eChannel.addContent((Content)this.generateSimpleElement("language", language));
        }
        if ((rating = channel.getRating()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("rating", rating));
        }
        if ((copyright = channel.getCopyright()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("copyright", copyright));
        }
        if ((pubDate = channel.getPubDate()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("pubDate", DateParser.formatRFC822(pubDate)));
        }
        if ((lastBuildDate = channel.getLastBuildDate()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("lastBuildDate", DateParser.formatRFC822(lastBuildDate)));
        }
        if ((docs = channel.getDocs()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("docs", docs));
        }
        if ((managingEditor = channel.getManagingEditor()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("managingEditor", managingEditor));
        }
        if ((webMaster = channel.getWebMaster()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("webMaster", webMaster));
        }
        if ((skipHours = channel.getSkipHours()) != null && skipHours.size() > 0) {
            eChannel.addContent((Content)this.generateSkipHoursElement(skipHours));
        }
        if ((skipDays = channel.getSkipDays()) != null && skipDays.size() > 0) {
            eChannel.addContent((Content)this.generateSkipDaysElement(skipDays));
        }
    }

    protected Element generateSkipHoursElement(List hours) {
        Element skipHoursElement = new Element("skipHours", this.getFeedNamespace());
        for (int i = 0; i < hours.size(); ++i) {
            skipHoursElement.addContent((Content)this.generateSimpleElement("hour", hours.get(i).toString()));
        }
        return skipHoursElement;
    }

    protected Element generateSkipDaysElement(List days) {
        Element skipDaysElement = new Element("skipDays");
        for (int i = 0; i < days.size(); ++i) {
            skipDaysElement.addContent((Content)this.generateSimpleElement("day", days.get(i).toString()));
        }
        return skipDaysElement;
    }

    protected void populateImage(Image image, Element eImage) {
        String description;
        int height;
        super.populateImage(image, eImage);
        int width = image.getWidth();
        if (width > -1) {
            eImage.addContent((Content)this.generateSimpleElement("width", String.valueOf(width)));
        }
        if ((height = image.getHeight()) > -1) {
            eImage.addContent((Content)this.generateSimpleElement("height", String.valueOf(height)));
        }
        if ((description = image.getDescription()) != null) {
            eImage.addContent((Content)this.generateSimpleElement("description", description));
        }
    }

    protected void populateItem(Item item, Element eItem, int index) {
        super.populateItem(item, eItem, index);
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

    protected boolean isHourFormat24() {
        return true;
    }

    protected void checkChannelConstraints(Element eChannel) throws FeedException {
        this.checkNotNullAndLength(eChannel, "title", 1, 100);
        this.checkNotNullAndLength(eChannel, "description", 1, 500);
        this.checkNotNullAndLength(eChannel, "link", 1, 500);
        this.checkNotNullAndLength(eChannel, "language", 2, 5);
        this.checkLength(eChannel, "rating", 20, 500);
        this.checkLength(eChannel, "copyright", 1, 100);
        this.checkLength(eChannel, "pubDate", 1, 100);
        this.checkLength(eChannel, "lastBuildDate", 1, 100);
        this.checkLength(eChannel, "docs", 1, 500);
        this.checkLength(eChannel, "managingEditor", 1, 100);
        this.checkLength(eChannel, "webMaster", 1, 100);
        Element skipHours = eChannel.getChild("skipHours");
        if (skipHours != null) {
            List hours = skipHours.getChildren();
            for (int i = 0; i < hours.size(); ++i) {
                Element hour = (Element)hours.get(i);
                int value = Integer.parseInt(hour.getText().trim());
                if (this.isHourFormat24()) {
                    if (value >= 1 && value <= 24) continue;
                    throw new FeedException("Invalid hour value " + value + ", it must be between 1 and 24");
                }
                if (value >= 0 && value <= 23) continue;
                throw new FeedException("Invalid hour value " + value + ", it must be between 0 and 23");
            }
        }
    }

    protected void checkImageConstraints(Element eImage) throws FeedException {
        this.checkNotNullAndLength(eImage, "title", 1, 100);
        this.checkNotNullAndLength(eImage, "url", 1, 500);
        this.checkLength(eImage, "link", 1, 500);
        this.checkLength(eImage, "width", 1, 3);
        this.checkLength(eImage, "width", 1, 3);
        this.checkLength(eImage, "description", 1, 100);
    }

    protected void checkTextInputConstraints(Element eTextInput) throws FeedException {
        this.checkNotNullAndLength(eTextInput, "title", 1, 100);
        this.checkNotNullAndLength(eTextInput, "description", 1, 500);
        this.checkNotNullAndLength(eTextInput, "name", 1, 20);
        this.checkNotNullAndLength(eTextInput, "link", 1, 500);
    }

    protected void checkItemConstraints(Element eItem) throws FeedException {
        this.checkNotNullAndLength(eItem, "title", 1, 100);
        this.checkNotNullAndLength(eItem, "link", 1, 500);
        this.checkLength(eItem, "description", 1, 500);
    }
}

