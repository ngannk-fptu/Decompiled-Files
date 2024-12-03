/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STPositiveCoordinate
extends XmlLong {
    public static final SimpleTypeFactory<STPositiveCoordinate> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stpositivecoordinatecbfctype");
    public static final SchemaType type = Factory.getType();
}

