/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STFontFamily
extends XmlInteger {
    public static final SimpleTypeFactory<STFontFamily> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stfontfamily9c6ctype");
    public static final SchemaType type = Factory.getType();

    public int getIntValue();

    public void setIntValue(int var1);
}

