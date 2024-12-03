/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Attribute
 *  org.jdom2.Content
 *  org.jdom2.Element
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Cloud;
import com.rometools.rome.feed.rss.Enclosure;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.rss.Source;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.impl.RSS091UserlandGenerator;
import java.util.List;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Element;

public class RSS092Generator
extends RSS091UserlandGenerator {
    public RSS092Generator() {
        this("rss_0.92", "0.92");
    }

    protected RSS092Generator(String type, String version) {
        super(type, version);
    }

    @Override
    protected void populateChannel(Channel channel, Element eChannel) {
        super.populateChannel(channel, eChannel);
        Cloud cloud = channel.getCloud();
        if (cloud != null) {
            eChannel.addContent((Content)this.generateCloud(cloud));
        }
    }

    protected Element generateCloud(Cloud cloud) {
        String protocol;
        String registerProcedure;
        String path;
        int port;
        Element eCloud = new Element("cloud", this.getFeedNamespace());
        String domain = cloud.getDomain();
        if (domain != null) {
            eCloud.setAttribute(new Attribute("domain", domain));
        }
        if ((port = cloud.getPort()) != 0) {
            eCloud.setAttribute(new Attribute("port", String.valueOf(port)));
        }
        if ((path = cloud.getPath()) != null) {
            eCloud.setAttribute(new Attribute("path", path));
        }
        if ((registerProcedure = cloud.getRegisterProcedure()) != null) {
            eCloud.setAttribute(new Attribute("registerProcedure", registerProcedure));
        }
        if ((protocol = cloud.getProtocol()) != null) {
            eCloud.setAttribute(new Attribute("protocol", protocol));
        }
        return eCloud;
    }

    protected int getNumberOfEnclosures(List<Enclosure> enclosures) {
        if (!enclosures.isEmpty()) {
            return 1;
        }
        return 0;
    }

    @Override
    protected void populateItem(Item item, Element eItem, int index) {
        super.populateItem(item, eItem, index);
        Source source = item.getSource();
        if (source != null) {
            eItem.addContent((Content)this.generateSourceElement(source));
        }
        List<Enclosure> enclosures = item.getEnclosures();
        for (int i = 0; i < this.getNumberOfEnclosures(enclosures); ++i) {
            eItem.addContent((Content)this.generateEnclosure(enclosures.get(i)));
        }
        List<Category> categories = item.getCategories();
        for (Category category : categories) {
            eItem.addContent((Content)this.generateCategoryElement(category));
        }
    }

    protected Element generateSourceElement(Source source) {
        Element sourceElement = new Element("source", this.getFeedNamespace());
        String url = source.getUrl();
        if (url != null) {
            sourceElement.setAttribute(new Attribute("url", url));
        }
        sourceElement.addContent(source.getValue());
        return sourceElement;
    }

    protected Element generateEnclosure(Enclosure enclosure) {
        String type;
        long length;
        Element enclosureElement = new Element("enclosure", this.getFeedNamespace());
        String url = enclosure.getUrl();
        if (url != null) {
            enclosureElement.setAttribute("url", url);
        }
        if ((length = enclosure.getLength()) != 0L) {
            enclosureElement.setAttribute("length", String.valueOf(length));
        }
        if ((type = enclosure.getType()) != null) {
            enclosureElement.setAttribute("type", type);
        }
        return enclosureElement;
    }

    protected Element generateCategoryElement(Category category) {
        Element categoryElement = new Element("category", this.getFeedNamespace());
        String domain = category.getDomain();
        if (domain != null) {
            categoryElement.setAttribute("domain", domain);
        }
        categoryElement.addContent(category.getValue());
        return categoryElement;
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
    }
}

