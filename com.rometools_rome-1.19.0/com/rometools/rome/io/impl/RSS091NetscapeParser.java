/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.Attribute
 *  org.jdom2.DocType
 *  org.jdom2.Document
 *  org.jdom2.Element
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.io.impl.RSS091UserlandParser;
import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;

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

    @Override
    public boolean isMyType(Document document) {
        Element rssRoot = document.getRootElement();
        String name = rssRoot.getName();
        Attribute version = rssRoot.getAttribute("version");
        DocType docType = document.getDocType();
        return name.equals(ELEMENT_NAME) && version != null && version.getValue().equals(this.getRSSVersion()) && docType != null && ELEMENT_NAME.equals(docType.getElementName()) && PUBLIC_ID.equals(docType.getPublicID()) && SYSTEM_ID.equals(docType.getSystemID());
    }

    @Override
    protected boolean isHourFormat24(Element rssRoot) {
        return false;
    }

    @Override
    protected String getTextInputLabel() {
        return "textinput";
    }
}

