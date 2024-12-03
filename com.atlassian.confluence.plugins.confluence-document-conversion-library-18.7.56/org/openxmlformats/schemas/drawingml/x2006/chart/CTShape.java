/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STShape;

public interface CTShape
extends XmlObject {
    public static final DocumentFactory<CTShape> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctshape89e5type");
    public static final SchemaType type = Factory.getType();

    public STShape.Enum getVal();

    public STShape xgetVal();

    public boolean isSetVal();

    public void setVal(STShape.Enum var1);

    public void xsetVal(STShape var1);

    public void unsetVal();
}

