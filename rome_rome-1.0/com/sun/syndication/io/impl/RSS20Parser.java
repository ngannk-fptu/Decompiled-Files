/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Attribute
 *  org.jdom.Document
 *  org.jdom.Element
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.feed.rss.Description;
import com.sun.syndication.io.impl.RSS094Parser;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

public class RSS20Parser
extends RSS094Parser {
    public RSS20Parser() {
        this("rss_2.0");
    }

    protected RSS20Parser(String type) {
        super(type);
    }

    protected String getRSSVersion() {
        return "2.0";
    }

    protected boolean isHourFormat24(Element rssRoot) {
        return false;
    }

    protected Description parseItemDescription(Element rssRoot, Element eDesc) {
        Description desc = super.parseItemDescription(rssRoot, eDesc);
        desc.setType("text/html");
        return desc;
    }

    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        boolean ok = rssRoot.getName().equals("rss");
        if (ok) {
            ok = false;
            Attribute version = rssRoot.getAttribute("version");
            if (version != null) {
                ok = version.getValue().startsWith(this.getRSSVersion());
            }
        }
        return ok;
    }
}

