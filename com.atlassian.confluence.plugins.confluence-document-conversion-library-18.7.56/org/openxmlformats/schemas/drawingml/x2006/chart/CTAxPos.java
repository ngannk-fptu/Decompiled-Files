/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxPos;

public interface CTAxPos
extends XmlObject {
    public static final DocumentFactory<CTAxPos> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctaxposff69type");
    public static final SchemaType type = Factory.getType();

    public STAxPos.Enum getVal();

    public STAxPos xgetVal();

    public void setVal(STAxPos.Enum var1);

    public void xsetVal(STAxPos var1);
}

