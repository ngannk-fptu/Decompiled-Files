/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.Document;
import com.lowagie.text.xml.xmp.XmpSchema;

public class PdfSchema
extends XmpSchema {
    private static final long serialVersionUID = -1541148669123992185L;
    public static final String DEFAULT_XPATH_ID = "pdf";
    public static final String DEFAULT_XPATH_URI = "http://ns.adobe.com/pdf/1.3/";
    public static final String KEYWORDS = "pdf:keywords";
    public static final String VERSION = "pdf:PDFVersion";
    public static final String PRODUCER = "pdf:Producer";

    public PdfSchema() {
        super("xmlns:pdf=\"http://ns.adobe.com/pdf/1.3/\"");
        this.addProducer(Document.getVersion());
    }

    public void addKeywords(String keywords) {
        this.setProperty(KEYWORDS, keywords);
    }

    public void addProducer(String producer) {
        this.setProperty(PRODUCER, producer);
    }

    public void addVersion(String version) {
        this.setProperty(VERSION, version);
    }
}

