/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Attribute
 *  org.jdom.Document
 *  org.jdom.Element
 *  org.jdom.Namespace
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Image;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.impl.DateParser;
import com.sun.syndication.io.impl.NumberParser;
import com.sun.syndication.io.impl.RSS090Parser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

public class RSS091UserlandParser
extends RSS090Parser {
    public RSS091UserlandParser() {
        this("rss_0.91U");
    }

    protected RSS091UserlandParser(String type) {
        super(type, null);
    }

    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        boolean ok = rssRoot.getName().equals("rss");
        if (ok) {
            ok = false;
            Attribute version = rssRoot.getAttribute("version");
            if (version != null) {
                ok = version.getValue().equals(this.getRSSVersion());
            }
        }
        return ok;
    }

    protected String getRSSVersion() {
        return "0.91";
    }

    protected Namespace getRSSNamespace() {
        return Namespace.getNamespace((String)"");
    }

    protected boolean isHourFormat24(Element rssRoot) {
        return true;
    }

    protected WireFeed parseChannel(Element rssRoot) {
        int i;
        Channel channel = (Channel)super.parseChannel(rssRoot);
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        Element e = eChannel.getChild("language", this.getRSSNamespace());
        if (e != null) {
            channel.setLanguage(e.getText());
        }
        if ((e = eChannel.getChild("rating", this.getRSSNamespace())) != null) {
            channel.setRating(e.getText());
        }
        if ((e = eChannel.getChild("copyright", this.getRSSNamespace())) != null) {
            channel.setCopyright(e.getText());
        }
        if ((e = eChannel.getChild("pubDate", this.getRSSNamespace())) != null) {
            channel.setPubDate(DateParser.parseDate(e.getText()));
        }
        if ((e = eChannel.getChild("lastBuildDate", this.getRSSNamespace())) != null) {
            channel.setLastBuildDate(DateParser.parseDate(e.getText()));
        }
        if ((e = eChannel.getChild("docs", this.getRSSNamespace())) != null) {
            channel.setDocs(e.getText());
        }
        if ((e = eChannel.getChild("docs", this.getRSSNamespace())) != null) {
            channel.setDocs(e.getText());
        }
        if ((e = eChannel.getChild("managingEditor", this.getRSSNamespace())) != null) {
            channel.setManagingEditor(e.getText());
        }
        if ((e = eChannel.getChild("webMaster", this.getRSSNamespace())) != null) {
            channel.setWebMaster(e.getText());
        }
        if ((e = eChannel.getChild("skipHours")) != null) {
            ArrayList<Integer> skipHours = new ArrayList<Integer>();
            List eHours = e.getChildren("hour", this.getRSSNamespace());
            for (i = 0; i < eHours.size(); ++i) {
                Element eHour = (Element)eHours.get(i);
                skipHours.add(new Integer(eHour.getText().trim()));
            }
            channel.setSkipHours(skipHours);
        }
        if ((e = eChannel.getChild("skipDays")) != null) {
            ArrayList<String> skipDays = new ArrayList<String>();
            List eDays = e.getChildren("day", this.getRSSNamespace());
            for (i = 0; i < eDays.size(); ++i) {
                Element eDay = (Element)eDays.get(i);
                skipDays.add(eDay.getText().trim());
            }
            channel.setSkipDays(skipDays);
        }
        return channel;
    }

    protected Image parseImage(Element rssRoot) {
        Image image = super.parseImage(rssRoot);
        if (image != null) {
            Integer val;
            Element eImage = this.getImage(rssRoot);
            Element e = eImage.getChild("width", this.getRSSNamespace());
            if (e != null && (val = NumberParser.parseInt(e.getText())) != null) {
                image.setWidth(val);
            }
            if ((e = eImage.getChild("height", this.getRSSNamespace())) != null && (val = NumberParser.parseInt(e.getText())) != null) {
                image.setHeight(val);
            }
            if ((e = eImage.getChild("description", this.getRSSNamespace())) != null) {
                image.setDescription(e.getText());
            }
        }
        return image;
    }

    protected List getItems(Element rssRoot) {
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        return eChannel != null ? eChannel.getChildren("item", this.getRSSNamespace()) : Collections.EMPTY_LIST;
    }

    protected Element getImage(Element rssRoot) {
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        return eChannel != null ? eChannel.getChild("image", this.getRSSNamespace()) : null;
    }

    protected String getTextInputLabel() {
        return "textInput";
    }

    protected Element getTextInput(Element rssRoot) {
        String elementName = this.getTextInputLabel();
        Element eChannel = rssRoot.getChild("channel", this.getRSSNamespace());
        return eChannel != null ? eChannel.getChild(elementName, this.getRSSNamespace()) : null;
    }

    protected Item parseItem(Element rssRoot, Element eItem) {
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
        return item;
    }

    protected Description parseItemDescription(Element rssRoot, Element eDesc) {
        Description desc = new Description();
        desc.setType("text/plain");
        desc.setValue(eDesc.getText());
        return desc;
    }
}

