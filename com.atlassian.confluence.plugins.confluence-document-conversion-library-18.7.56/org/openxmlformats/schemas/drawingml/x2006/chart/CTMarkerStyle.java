/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STMarkerStyle;

public interface CTMarkerStyle
extends XmlObject {
    public static final DocumentFactory<CTMarkerStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctmarkerstyle1f6ftype");
    public static final SchemaType type = Factory.getType();

    public STMarkerStyle.Enum getVal();

    public STMarkerStyle xgetVal();

    public void setVal(STMarkerStyle.Enum var1);

    public void xsetVal(STMarkerStyle var1);
}

