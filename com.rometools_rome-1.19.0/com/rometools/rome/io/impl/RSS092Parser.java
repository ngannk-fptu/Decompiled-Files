/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Strings
 *  org.jdom2.Content
 *  org.jdom2.Element
 *  org.jdom2.output.XMLOutputter
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.rss.Category;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Cloud;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Enclosure;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.feed.rss.Source;
import com.rometools.rome.io.impl.NumberParser;
import com.rometools.rome.io.impl.RSS091UserlandParser;
import com.rometools.utils.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.jdom2.Content;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSS092Parser
extends RSS091UserlandParser {
    private static final Logger LOG = LoggerFactory.getLogger(RSS092Parser.class);

    public RSS092Parser() {
        this("rss_0.92");
    }

    protected RSS092Parser(String type) {
        super(type);
    }

    @Override
    protected String getRSSVersion() {
        return "0.92";
    }

    @Override
    protected WireFeed parseChannel(Element rssRoot, Locale locale) {
        Channel channel = (Channel)super.parseChannel(rssRoot, locale);
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        Element eCloud = eChannel.getChild("cloud", this.getRSSNamespace());
        if (eCloud != null) {
            String protocol;
            String registerProcedure;
            String path;
            String port;
            Cloud cloud = new Cloud();
            String domain = eCloud.getAttributeValue("domain");
            if (domain != null) {
                cloud.setDomain(domain);
            }
            if ((port = eCloud.getAttributeValue("port")) != null) {
                cloud.setPort(Integer.parseInt(port.trim()));
            }
            if ((path = eCloud.getAttributeValue("path")) != null) {
                cloud.setPath(path);
            }
            if ((registerProcedure = eCloud.getAttributeValue("registerProcedure")) != null) {
                cloud.setRegisterProcedure(registerProcedure);
            }
            if ((protocol = eCloud.getAttributeValue("protocol")) != null) {
                cloud.setProtocol(protocol);
            }
            channel.setCloud(cloud);
        }
        return channel;
    }

    @Override
    protected Item parseItem(Element rssRoot, Element eItem, Locale locale) {
        List eEnclosures;
        Item item = super.parseItem(rssRoot, eItem, locale);
        Element eSource = eItem.getChild("source", this.getRSSNamespace());
        if (eSource != null) {
            Source source = new Source();
            String url = eSource.getAttributeValue("url");
            source.setUrl(url);
            source.setValue(eSource.getText());
            item.setSource(source);
        }
        if (!(eEnclosures = eItem.getChildren("enclosure")).isEmpty()) {
            ArrayList<Enclosure> enclosures = new ArrayList<Enclosure>();
            for (Element eEnclosure : eEnclosures) {
                Enclosure enclosure = new Enclosure();
                String url = eEnclosure.getAttributeValue("url");
                if (url != null) {
                    enclosure.setUrl(url);
                }
                String length = eEnclosure.getAttributeValue("length");
                enclosure.setLength(NumberParser.parseLong(length, 0L));
                String type = eEnclosure.getAttributeValue("type");
                if (type != null) {
                    enclosure.setType(type);
                }
                enclosures.add(enclosure);
            }
            item.setEnclosures(enclosures);
        }
        List categories = eItem.getChildren("category");
        item.setCategories(this.parseCategories(categories));
        return item;
    }

    protected List<Category> parseCategories(List<Element> eCats) {
        ArrayList<Category> cats = new ArrayList<Category>();
        for (Element eCat : eCats) {
            String text = eCat.getText();
            if (Strings.isBlank((String)text)) continue;
            Category cat = new Category();
            String domain = eCat.getAttributeValue("domain");
            if (domain != null) {
                cat.setDomain(domain);
            }
            cat.setValue(text);
            cats.add(cat);
        }
        if (cats.isEmpty()) {
            return null;
        }
        return cats;
    }

    @Override
    protected Description parseItemDescription(Element rssRoot, Element eDesc) {
        Description desc = new Description();
        StringBuilder sb = new StringBuilder();
        XMLOutputter xmlOut = new XMLOutputter();
        for (Content c : eDesc.getContent()) {
            switch (c.getCType()) {
                case Text: 
                case CDATA: {
                    sb.append(c.getValue());
                    break;
                }
                case EntityRef: {
                    LOG.debug("Entity: {}", (Object)c.getValue());
                    sb.append(c.getValue());
                    break;
                }
                case Element: {
                    sb.append(xmlOut.outputString((Element)c));
                    break;
                }
            }
        }
        desc.setValue(sb.toString());
        String att = eDesc.getAttributeValue("type");
        if (att == null) {
            att = "text/html";
        }
        desc.setType(att);
        return desc;
    }
}

