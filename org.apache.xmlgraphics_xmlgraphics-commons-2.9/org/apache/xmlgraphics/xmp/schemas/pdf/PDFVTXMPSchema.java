/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas.pdf;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.schemas.pdf.PDFVTAdapter;

public class PDFVTXMPSchema
extends XMPSchema {
    public static final String NAMESPACE = "http://www.npes.org/pdfvt/ns/id/";

    public PDFVTXMPSchema() {
        super(NAMESPACE, "pdfvtid");
    }

    public static PDFVTAdapter getAdapter(Metadata meta) {
        return new PDFVTAdapter(meta, NAMESPACE);
    }
}

