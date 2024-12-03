/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas.pdf;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.schemas.pdf.PDFXAdapter;

public class PDFXXMPSchema
extends XMPSchema {
    public static final String NAMESPACE = "http://www.npes.org/pdfx/ns/id/";

    public PDFXXMPSchema() {
        super(NAMESPACE, "pdfxid");
    }

    public static PDFXAdapter getAdapter(Metadata meta) {
        return new PDFXAdapter(meta, NAMESPACE);
    }
}

