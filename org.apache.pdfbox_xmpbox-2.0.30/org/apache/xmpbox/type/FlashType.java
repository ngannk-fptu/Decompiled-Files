/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="exif", namespace="http://ns.adobe.com/exif/1.0/")
public class FlashType
extends AbstractStructuredType {
    @PropertyType(type=Types.Boolean)
    public static final String FIRED = "Fired";
    @PropertyType(type=Types.Boolean)
    public static final String FUNCTION = "Function";
    @PropertyType(type=Types.Boolean)
    public static final String RED_EYE_MODE = "RedEyeMode";
    @PropertyType(type=Types.Integer)
    public static final String MODE = "Mode";
    @PropertyType(type=Types.Integer)
    public static final String RETURN = "Return";

    public FlashType(XMPMetadata metadata) {
        super(metadata);
    }
}

