/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas.pdf;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.merge.MergeRuleSet;
import org.apache.xmlgraphics.xmp.schemas.pdf.AdobePDFAdapter;

public class AdobePDFSchema
extends XMPSchema {
    public static final String NAMESPACE = "http://ns.adobe.com/pdf/1.3/";
    private static MergeRuleSet mergeRuleSet = new MergeRuleSet();

    public AdobePDFSchema() {
        super(NAMESPACE, "pdf");
    }

    public static AdobePDFAdapter getAdapter(Metadata meta) {
        return new AdobePDFAdapter(meta, NAMESPACE);
    }

    @Override
    public MergeRuleSet getDefaultMergeRuleSet() {
        return mergeRuleSet;
    }
}

