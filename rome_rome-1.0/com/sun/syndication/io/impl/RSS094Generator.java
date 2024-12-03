/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Attribute
 *  org.jdom.Element
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.impl.RSS093Generator;
import org.jdom.Attribute;
import org.jdom.Element;

public class RSS094Generator
extends RSS093Generator {
    public RSS094Generator() {
        this("rss_0.94", "0.94");
    }

    protected RSS094Generator(String feedType, String version) {
        super(feedType, version);
    }

    protected void populateItem(Item item, Element eItem, int index) {
        super.populateItem(item, eItem, index);
        Description description = item.getDescription();
        if (description != null && description.getType() != null) {
            Element eDescription = eItem.getChild("description", this.getFeedNamespace());
            eDescription.setAttribute(new Attribute("type", description.getType()));
        }
        eItem.removeChild("expirationDate", this.getFeedNamespace());
    }
}

