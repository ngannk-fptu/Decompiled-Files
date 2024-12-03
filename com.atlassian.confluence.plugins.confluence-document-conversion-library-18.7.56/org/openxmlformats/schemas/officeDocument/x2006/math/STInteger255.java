/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.officeDocument.x2006.math;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STInteger255
extends XmlInteger {
    public static final SimpleTypeFactory<STInteger255> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stinteger2550f8etype");
    public static final SchemaType type = Factory.getType();

    public int getIntValue();

    public void setIntValue(int var1);
}

