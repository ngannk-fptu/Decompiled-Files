/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLegendPos;

public interface CTLegendPos
extends XmlObject {
    public static final DocumentFactory<CTLegendPos> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlegendpos053ftype");
    public static final SchemaType type = Factory.getType();

    public STLegendPos.Enum getVal();

    public STLegendPos xgetVal();

    public boolean isSetVal();

    public void setVal(STLegendPos.Enum var1);

    public void xsetVal(STLegendPos var1);

    public void unsetVal();
}

