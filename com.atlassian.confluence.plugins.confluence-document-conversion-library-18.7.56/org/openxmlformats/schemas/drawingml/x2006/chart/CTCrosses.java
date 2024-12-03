/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STCrosses;

public interface CTCrosses
extends XmlObject {
    public static final DocumentFactory<CTCrosses> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcrossesbcb8type");
    public static final SchemaType type = Factory.getType();

    public STCrosses.Enum getVal();

    public STCrosses xgetVal();

    public void setVal(STCrosses.Enum var1);

    public void xsetVal(STCrosses var1);
}

