/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos;

public interface CTTickLblPos
extends XmlObject {
    public static final DocumentFactory<CTTickLblPos> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctticklblposff61type");
    public static final SchemaType type = Factory.getType();

    public STTickLblPos.Enum getVal();

    public STTickLblPos xgetVal();

    public boolean isSetVal();

    public void setVal(STTickLblPos.Enum var1);

    public void xsetVal(STTickLblPos var1);

    public void unsetVal();
}

