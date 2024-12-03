/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Attribute
 *  org.jdom2.Document
 *  org.jdom2.Element
 *  org.jdom2.Namespace
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.WireFeed;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Content;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Image;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.impl.DateParser;
import com.rometools.rome.io.impl.NumberParser;
import com.rometools.rome.io.impl.RSS090Parser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;

public class RSS091UserlandParser
extends RSS090Parser {
    public RSS091UserlandParser() {
        this("rss_0.91U");
    }

    protected RSS091UserlandParser(String type) {
        super(type, null);
    }

    @Override
    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        Attribute version = rssRoot.getAttribute("version");
        return rssRoot.getName().equals("rss") && version != null && version.getValue().equals(this.getRSSVersion());
    }

    protected String getRSSVersion() {
        return "0.91";
    }

    @Override
    protected Namespace getRSSNamespace() {
        return Namespace.getNamespace((String)"");
    }

    protected boolean isHourFormat24(Element rssRoot) {
        return true;
    }

    @Override
    protected WireFeed parseChannel(Element rssRoot, Locale locale) {
        Element eSkipDays;
        Element eSkipHours;
        Element webMaster;
        Element managingEditor;
        Element generator;
        Element docs;
        Element lastBuildDate;
        Element pubDate;
        Element copyright;
        Element atinge;
        Channel channel = (Channel)super.parseChannel(rssRoot, locale);
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        Element language = eChannel.getChild("language", this.getRSSNamespace());
        if (language != null) {
            channel.setLanguage(language.getText());
        }
        if ((atinge = eChannel.getChild("rating", this.getRSSNamespace())) != null) {
            channel.setRating(atinge.getText());
        }
        if ((copyright = eChannel.getChild("copyright", this.getRSSNamespace())) != null) {
            channel.setCopyright(copyright.getText());
        }
        if ((pubDate = eChannel.getChild("pubDate", this.getRSSNamespace())) != null) {
            channel.setPubDate(DateParser.parseDate(pubDate.getText(), locale));
        }
        if ((lastBuildDate = eChannel.getChild("lastBuildDate", this.getRSSNamespace())) != null) {
            channel.setLastBuildDate(DateParser.parseDate(lastBuildDate.getText(), locale));
        }
        if ((docs = eChannel.getChild("docs", this.getRSSNamespace())) != null) {
            channel.setDocs(docs.getText());
        }
        if ((generator = eChannel.getChild("generator", this.getRSSNamespace())) != null) {
            channel.setGenerator(generator.getText());
        }
        if ((managingEditor = eChannel.getChild("managingEditor", this.getRSSNamespace())) != null) {
            channel.setManagingEditor(managingEditor.getText());
        }
        if ((webMaster = eChannel.getChild("webMaster", this.getRSSNamespace())) != null) {
            channel.setWebMaster(webMaster.getText());
        }
        if ((eSkipHours = eChannel.getChild("skipHours")) != null) {
            ArrayList<Integer> skipHours = new ArrayList<Integer>();
            List eHours = eSkipHours.getChildren("hour", this.getRSSNamespace());
            for (Element eHour : eHours) {
                skipHours.add(new Integer(eHour.getText().trim()));
            }
            channel.setSkipHours(skipHours);
        }
        if ((eSkipDays = eChannel.getChild("skipDays")) != null) {
            ArrayList<String> skipDays = new ArrayList<String>();
            List eDays = eSkipDays.getChildren("day", this.getRSSNamespace());
            for (Element eDay : eDays) {
                skipDays.add(eDay.getText().trim());
            }
            channel.setSkipDays(skipDays);
        }
        return channel;
    }

    @Override
    protected Image parseImage(Element rssRoot) {
        Image image = super.parseImage(rssRoot);
        if (image != null) {
            Element description;
            Integer val;
            Element height;
            Integer val2;
            Element eImage = this.getImage(rssRoot);
            Element width = eImage.getChild("width", this.getRSSNamespace());
            if (width != null && (val2 = NumberParser.parseInt(width.getText())) != null) {
                image.setWidth(val2);
            }
            if ((height = eImage.getChild("height", this.getRSSNamespace())) != null && (val = NumberParser.parseInt(height.getText())) != null) {
                image.setHeight(val);
            }
            if ((description = eImage.getChild("description", this.getRSSNamespace())) != null) {
                image.setDescription(description.getText());
            }
        }
        return image;
    }

    @Override
    protected List<Element> getItems(Element rssRoot) {
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        if (eChannel != null) {
            return eChannel.getChildren("item", this.getRSSNamespace());
        }
        return Collections.emptyList();
    }

    @Override
    protected Element getImage(Element rssRoot) {
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        if (eChannel != null) {
            return eChannel.getChild("image", this.getRSSNamespace());
        }
        return null;
    }

    protected String getTextInputLabel() {
        return "textInput";
    }

    @Override
    protected Element getTextInput(Element rssRoot) {
        String elementName = this.getTextInputLabel();
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        if (eChannel != null) {
            return eChannel.getChild(elementName, this.getRSSNamespace());
        }
        return null;
    }

    @Override
    protected Item parseItem(Element rssRoot, Element eItem, Locale locale) {
        Element encoded;
        Element pubDate;
        Item item = super.parseItem(rssRoot, eItem, locale);
        Element description = eItem.getChild("description", this.getRSSNamespace());
        if (description != null) {
            item.setDescription(this.parseItemDescription(rssRoot, description));
        }
        if ((pubDate = eItem.getChild("pubDate", this.getRSSNamespace())) != null) {
            item.setPubDate(DateParser.parseDate(pubDate.getText(), locale));
        }
        if ((encoded = eItem.getChild("encoded", this.getContentNamespace())) != null) {
            Content content = new Content();
            content.setType("html");
            content.setValue(encoded.getText());
            item.setContent(content);
        }
        return item;
    }

    protected Description parseItemDescription(Element rssRoot, Element eDesc) {
        Description desc = new Description();
        desc.setType("text/plain");
        desc.setValue(eDesc.getText());
        return desc;
    }
}

