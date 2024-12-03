/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas.pdf;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchemaAdapter;
import org.apache.xmlgraphics.xmp.XMPSchemaRegistry;

public class AdobePDFAdapter
extends XMPSchemaAdapter {
    private static final String KEYWORDS = "Keywords";
    private static final String PDFVERSION = "PDFVersion";
    private static final String PRODUCER = "Producer";
    private static final String TRAPPED = "Trapped";

    public AdobePDFAdapter(Metadata meta, String namespace) {
        super(meta, XMPSchemaRegistry.getInstance().getSchema(namespace));
    }

    public String getKeywords() {
        return this.getValue(KEYWORDS);
    }

    public void setKeywords(String value) {
        this.setValue(KEYWORDS, value);
    }

    public String getPDFVersion() {
        return this.getValue(PDFVERSION);
    }

    public void setPDFVersion(String value) {
        this.setValue(PDFVERSION, value);
    }

    public String getProducer() {
        return this.getValue(PRODUCER);
    }

    public void setProducer(String value) {
        this.setValue(PRODUCER, value);
    }

    public void setTrapped(String v) {
        this.setValue(TRAPPED, v);
    }
}

