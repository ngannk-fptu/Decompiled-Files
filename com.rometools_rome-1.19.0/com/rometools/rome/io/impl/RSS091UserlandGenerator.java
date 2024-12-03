/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Attribute
 *  org.jdom2.Content
 *  org.jdom2.Document
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.impl.DateParser;
import com.rometools.rome.io.impl.RSS090Generator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class RSS091UserlandGenerator
extends RSS090Generator {
    private final String version;

    public RSS091UserlandGenerator() {
        this("rss_0.91U", "0.91");
    }

    protected RSS091UserlandGenerator(String type, String version) {
        super(type);
        this.version = version;
    }

    @Override
    protected Namespace getFeedNamespace() {
        return Namespace.NO_NAMESPACE;
    }

    protected boolean isHourFormat24() {
        return true;
    }

    protected String getVersion() {
        return this.version;
    }

    @Override
    protected void addChannel(Channel channel, Element parent) throws FeedException {
        super.addChannel(channel, parent);
        Element eChannel = parent.getChild("channel", this.getFeedNamespace());
        this.addImage(channel, eChannel);
        this.addTextInput(channel, eChannel);
        this.addItems(channel, eChannel);
    }

    @Override
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
            for (Element hour : hours) {
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

    @Override
    protected void checkImageConstraints(Element eImage) throws FeedException {
        this.checkNotNullAndLength(eImage, "title", 1, 100);
        this.checkNotNullAndLength(eImage, "url", 1, 500);
        this.checkLength(eImage, "link", 1, 500);
        this.checkLength(eImage, "width", 1, 3);
        this.checkLength(eImage, "width", 1, 3);
        this.checkLength(eImage, "description", 1, 100);
    }

    @Override
    protected void checkItemConstraints(Element eItem) throws FeedException {
        this.checkNotNullAndLength(eItem, "title", 1, 100);
        this.checkNotNullAndLength(eItem, "link", 1, 500);
        this.checkLength(eItem, "description", 1, 500);
    }

    @Override
    protected void checkTextInputConstraints(Element eTextInput) throws FeedException {
        this.checkNotNullAndLength(eTextInput, "title", 1, 100);
        this.checkNotNullAndLength(eTextInput, "description", 1, 500);
        this.checkNotNullAndLength(eTextInput, "name", 1, 20);
        this.checkNotNullAndLength(eTextInput, "link", 1, 500);
    }

    @Override
    protected Document createDocument(Element root) {
        return new Document(root);
    }

    @Override
    protected Element createRootElement(Channel channel) {
        Element root = new Element("rss", this.getFeedNamespace());
        Attribute version = new Attribute("version", this.getVersion());
        root.setAttribute(version);
        root.addNamespaceDeclaration(this.getContentNamespace());
        this.generateModuleNamespaceDefs(root);
        return root;
    }

    protected Element generateSkipDaysElement(List<String> days) {
        Element skipDaysElement = new Element("skipDays");
        for (String day : days) {
            skipDaysElement.addContent((Content)this.generateSimpleElement("day", day.toString()));
        }
        return skipDaysElement;
    }

    protected Element generateSkipHoursElement(List<Integer> hours) {
        Element skipHoursElement = new Element("skipHours", this.getFeedNamespace());
        for (Integer hour : hours) {
            skipHoursElement.addContent((Content)this.generateSimpleElement("hour", hour.toString()));
        }
        return skipHoursElement;
    }

    @Override
    protected void populateChannel(Channel channel, Element eChannel) {
        List<String> skipDays;
        List<Integer> skipHours;
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
            eChannel.addContent((Content)this.generateSimpleElement("pubDate", DateParser.formatRFC822(pubDate, Locale.US)));
        }
        if ((lastBuildDate = channel.getLastBuildDate()) != null) {
            eChannel.addContent((Content)this.generateSimpleElement("lastBuildDate", DateParser.formatRFC822(lastBuildDate, Locale.US)));
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
        if ((skipHours = channel.getSkipHours()) != null && !skipHours.isEmpty()) {
            eChannel.addContent((Content)this.generateSkipHoursElement(skipHours));
        }
        if ((skipDays = channel.getSkipDays()) != null && !skipDays.isEmpty()) {
            eChannel.addContent((Content)this.generateSkipDaysElement(skipDays));
        }
    }

    @Override
    protected void populateFeed(Channel channel, Element parent) throws FeedException {
        this.addChannel(channel, parent);
    }

    @Override
    protected void populateImage(Image image, Element eImage) {
        String description;
        Integer height;
        super.populateImage(image, eImage);
        Integer width = image.getWidth();
        if (width != null) {
            eImage.addContent((Content)this.generateSimpleElement("width", String.valueOf(width)));
        }
        if ((height = image.getHeight()) != null) {
            eImage.addContent((Content)this.generateSimpleElement("height", String.valueOf(height)));
        }
        if ((description = image.getDescription()) != null) {
            eImage.addContent((Content)this.generateSimpleElement("description", description));
        }
    }

    @Override
    protected void populateItem(Item item, Element eItem, int index) {
        super.populateItem(item, eItem, index);
        Description description = item.getDescription();
        if (description != null) {
            eItem.addContent((Content)this.generateSimpleElement("description", description.getValue()));
        }
        Namespace contentNamespace = this.getContentNamespace();
        com.rometools.rome.feed.rss.Content content = item.getContent();
        if (item.getModule(contentNamespace.getURI()) == null && content != null) {
            Element elem = new Element("encoded", contentNamespace);
            elem.addContent(content.getValue());
            eItem.addContent((Content)elem);
        }
    }
}

