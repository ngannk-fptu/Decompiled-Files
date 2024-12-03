/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="stDim", namespace="http://ns.adobe.com/xap/1.0/sType/Dimensions#")
public class DimensionsType
extends AbstractStructuredType {
    @PropertyType(type=Types.Real)
    public static final String H = "h";
    @PropertyType(type=Types.Real)
    public static final String W = "w";
    @PropertyType(type=Types.Text)
    public static final String UNIT = "unit";

    public DimensionsType(XMPMetadata metadata) {
        super(metadata);
    }
}

