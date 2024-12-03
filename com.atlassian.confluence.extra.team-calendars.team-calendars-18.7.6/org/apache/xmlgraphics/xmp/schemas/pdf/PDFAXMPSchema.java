/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas.pdf;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.merge.MergeRuleSet;
import org.apache.xmlgraphics.xmp.schemas.pdf.PDFAAdapter;

public class PDFAXMPSchema
extends XMPSchema {
    public static final String NAMESPACE = "http://www.aiim.org/pdfa/ns/id/";
    private static MergeRuleSet mergeRuleSet = new MergeRuleSet();

    public PDFAXMPSchema() {
        super(NAMESPACE, "pdfaid");
    }

    public static PDFAAdapter getAdapter(Metadata meta) {
        return new PDFAAdapter(meta, NAMESPACE);
    }

    @Override
    public MergeRuleSet getDefaultMergeRuleSet() {
        return mergeRuleSet;
    }
}

