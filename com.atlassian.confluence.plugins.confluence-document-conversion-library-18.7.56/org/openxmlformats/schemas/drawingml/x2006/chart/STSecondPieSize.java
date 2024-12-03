/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public interface STSecondPieSize
extends XmlAnySimpleType {
    public static final SimpleTypeFactory<STSecondPieSize> Factory = new SimpleTypeFactory(TypeSystemHolder.typeSystem, "stsecondpiesize58fdtype");
    public static final SchemaType type = Factory.getType();

    public Object getObjectValue();

    public void setObjectValue(Object var1);

    public SchemaType instanceType();
}

