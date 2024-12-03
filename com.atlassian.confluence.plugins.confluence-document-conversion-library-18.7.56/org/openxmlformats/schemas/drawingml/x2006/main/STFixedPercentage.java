/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STFixedPercentage
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<STFixedPercentage> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stfixedpercentagef0cftype");
    public static final SchemaType type = Factory.getType();

    public Object getObjectValue();

    public void setObjectValue(Object var1);

    public SchemaType instanceType();
}

