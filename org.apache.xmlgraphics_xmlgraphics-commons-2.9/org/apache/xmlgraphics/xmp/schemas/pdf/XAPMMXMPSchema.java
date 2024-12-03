/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.xmp.schemas.pdf;

import org.apache.xmlgraphics.xmp.Metadata;
import org.apache.xmlgraphics.xmp.XMPSchema;
import org.apache.xmlgraphics.xmp.schemas.pdf.XAPMMAdapter;

public class XAPMMXMPSchema
extends XMPSchema {
    public static final String NAMESPACE = "http://ns.adobe.com/xap/1.0/mm/";

    public XAPMMXMPSchema() {
        super(NAMESPACE, "xmpMM");
    }

    public static XAPMMAdapter getAdapter(Metadata meta) {
        return new XAPMMAdapter(meta, NAMESPACE);
    }
}

