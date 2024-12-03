/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom.Attribute
 *  org.jdom.DocType
 *  org.jdom.Document
 *  org.jdom.Element
 */
package com.sun.syndication.io.impl;

import com.sun.syndication.io.impl.RSS091UserlandParser;
import org.jdom.Attribute;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;

public class RSS091NetscapeParser
extends RSS091UserlandParser {
    static final String ELEMENT_NAME = "rss";
    static final String PUBLIC_ID = "-//Netscape Communications//DTD RSS 0.91//EN";
    static final String SYSTEM_ID = "http://my.netscape.com/publish/formats/rss-0.91.dtd";

    public RSS091NetscapeParser() {
        this("rss_0.91N");
    }

    protected RSS091NetscapeParser(String type) {
        super(type);
    }

    public boolean isMyType(Document document) {
        boolean ok = false;
        Element rssRoot = document.getRootElement();
        ok = rssRoot.getName().equals(ELEMENT_NAME);
        if (ok) {
            ok = false;
            Attribute version = rssRoot.getAttribute("version");
            if (version != null && (ok = version.getValue().equals(this.getRSSVersion()))) {
                ok = false;
                DocType docType = document.getDocType();
                if (docType != null) {
                    ok = ELEMENT_NAME.equals(docType.getElementName());
                    ok = ok && PUBLIC_ID.equals(docType.getPublicID());
                    ok = ok && SYSTEM_ID.equals(docType.getSystemID());
                }
            }
        }
        return ok;
    }

    protected boolean isHourFormat24(Element rssRoot) {
        return false;
    }

    protected String getTextInputLabel() {
        return "textinput";
    }
}

