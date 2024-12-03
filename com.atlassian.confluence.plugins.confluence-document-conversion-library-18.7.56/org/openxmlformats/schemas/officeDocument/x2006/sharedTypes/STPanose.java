/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.sharedTypes;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlHexBinary;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STPanose
extends XmlHexBinary {
    public static final SimpleTypeFactory<STPanose> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stpanosedb3etype");
    public static final SchemaType type = Factory.getType();
}

