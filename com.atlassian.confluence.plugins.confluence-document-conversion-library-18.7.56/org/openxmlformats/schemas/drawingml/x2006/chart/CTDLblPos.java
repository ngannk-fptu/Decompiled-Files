/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDLblPos;

public interface CTDLblPos
extends XmlObject {
    public static final DocumentFactory<CTDLblPos> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdlblpos9ce4type");
    public static final SchemaType type = Factory.getType();

    public STDLblPos.Enum getVal();

    public STDLblPos xgetVal();

    public void setVal(STDLblPos.Enum var1);

    public void xsetVal(STDLblPos var1);
}

