/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STRadarStyle;

public interface CTRadarStyle
extends XmlObject {
    public static final DocumentFactory<CTRadarStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctradarstyle77d1type");
    public static final SchemaType type = Factory.getType();

    public STRadarStyle.Enum getVal();

    public STRadarStyle xgetVal();

    public boolean isSetVal();

    public void setVal(STRadarStyle.Enum var1);

    public void xsetVal(STRadarStyle var1);

    public void unsetVal();
}

