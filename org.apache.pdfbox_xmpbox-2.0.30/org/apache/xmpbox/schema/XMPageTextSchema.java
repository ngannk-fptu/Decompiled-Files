/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="xmpTPg", namespace="http://ns.adobe.com/xap/1.0/t/pg/")
public class XMPageTextSchema
extends XMPSchema {
    @PropertyType(type=Types.Dimensions)
    public static final String MAX_PAGE_SIZE = "MaxPageSize";
    @PropertyType(type=Types.Integer)
    public static final String N_PAGES = "NPages";

    public XMPageTextSchema(XMPMetadata metadata) {
        super(metadata);
    }

    public XMPageTextSchema(XMPMetadata metadata, String prefix) {
        super(metadata, prefix);
    }
}

