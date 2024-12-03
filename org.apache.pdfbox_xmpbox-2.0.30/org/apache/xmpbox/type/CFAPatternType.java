/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="exif", namespace="http://ns.adobe.com/exif/1.0/")
public class CFAPatternType
extends AbstractStructuredType {
    @PropertyType(type=Types.Integer)
    public static final String COLUMNS = "Columns";
    @PropertyType(type=Types.Integer)
    public static final String ROWS = "Rows";
    @PropertyType(type=Types.Integer, card=Cardinality.Seq)
    public static final String VALUES = "Values";

    public CFAPatternType(XMPMetadata metadata) {
        super(metadata);
    }
}

