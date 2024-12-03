/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Attribute
 *  org.jdom2.Element
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.impl.RSS093Generator;
import org.jdom2.Attribute;
import org.jdom2.Element;

public class RSS094Generator
extends RSS093Generator {
    public RSS094Generator() {
        this("rss_0.94", "0.94");
    }

    protected RSS094Generator(String feedType, String version) {
        super(feedType, version);
    }

    @Override
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

