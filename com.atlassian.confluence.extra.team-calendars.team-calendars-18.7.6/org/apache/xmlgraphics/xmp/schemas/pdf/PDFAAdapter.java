/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas.pdf;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchemaAdapter;
import org.apache.xmlgraphics.xmp.XMPSchemaRegistry;

public class PDFAAdapter
extends XMPSchemaAdapter {
    private static final String PART = "part";
    private static final String AMD = "amd";
    private static final String CONFORMANCE = "conformance";

    public PDFAAdapter(Metadata meta, String namespace) {
        super(meta, XMPSchemaRegistry.getInstance().getSchema(namespace));
    }

    public void setPart(int value) {
        this.setValue(PART, Integer.toString(value));
    }

    public int getPart() {
        return Integer.parseInt(this.getValue(PART));
    }

    public void setAmendment(String value) {
        this.setValue(AMD, value);
    }

    public String getAmendment() {
        return this.getValue(AMD);
    }

    public void setConformance(String value) {
        this.setValue(CONFORMANCE, value);
    }

    public String getConformance() {
        return this.getValue(CONFORMANCE);
    }
}

