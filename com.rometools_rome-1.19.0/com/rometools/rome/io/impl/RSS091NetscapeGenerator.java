/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jdom2.DocType
 *  org.jdom2.Document
 *  org.jdom2.Element
 */
package com.rometools.rome.io.impl;

import com.rometools.rome.io.impl.RSS091UserlandGenerator;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;

public class RSS091NetscapeGenerator
extends RSS091UserlandGenerator {
    public RSS091NetscapeGenerator() {
        this("rss_0.91N", "0.91");
    }

    protected RSS091NetscapeGenerator(String type, String version) {
        super(type, version);
    }

    @Override
    protected Document createDocument(Element root) {
        Document doc = new Document(root);
        DocType docType = new DocType("rss", "-//Netscape Communications//DTD RSS 0.91//EN", "http://my.netscape.com/publish/formats/rss-0.91.dtd");
        doc.setDocType(docType);
        return doc;
    }

    @Override
    protected String getTextInputLabel() {
        return "textinput";
    }

    @Override
    protected boolean isHourFormat24() {
        return false;
    }
}

