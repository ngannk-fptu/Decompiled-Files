/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas.pdf;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.merge.MergeRuleSet;
import org.apache.xmlgraphics.xmp.schemas.pdf.PDFUAAdapter;

public class PDFUAXMPSchema
extends XMPSchema {
    public static final String NAMESPACE = "http://www.aiim.org/pdfua/ns/id/";
    private static MergeRuleSet mergeRuleSet = new MergeRuleSet();

    public PDFUAXMPSchema() {
        super(NAMESPACE, "pdfuaid");
    }

    public static PDFUAAdapter getAdapter(Metadata meta) {
        return new PDFUAAdapter(meta, NAMESPACE);
    }

    @Override
    public MergeRuleSet getDefaultMergeRuleSet() {
        return mergeRuleSet;
    }
}

