/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas;

import org.apache.xmlgraphics.util.QName;
import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.merge.MergeRuleSet;
import org.apache.xmlgraphics.xmp.merge.NoReplacePropertyMerger;
import org.apache.xmlgraphics.xmp.schemas.XMPBasicAdapter;

public class XMPBasicSchema
extends XMPSchema {
    public static final String NAMESPACE = "http://ns.adobe.com/xap/1.0/";
    public static final String PREFERRED_PREFIX = "xmp";
    public static final String QUALIFIER_NAMESPACE = "http://ns.adobe.com/xmp/Identifier/qual/1.0/";
    public static final QName SCHEME_QUALIFIER = new QName("http://ns.adobe.com/xmp/Identifier/qual/1.0/", "xmpidq:Scheme");
    private static MergeRuleSet mergeRuleSet = new MergeRuleSet();

    public XMPBasicSchema() {
        super(NAMESPACE, PREFERRED_PREFIX);
    }

    public static XMPBasicAdapter getAdapter(Metadata meta) {
        return new XMPBasicAdapter(meta, NAMESPACE);
    }

    @Override
    public MergeRuleSet getDefaultMergeRuleSet() {
        return mergeRuleSet;
    }

    static {
        mergeRuleSet.addRule(new QName(NAMESPACE, "CreateDate"), new NoReplacePropertyMerger());
    }
}

