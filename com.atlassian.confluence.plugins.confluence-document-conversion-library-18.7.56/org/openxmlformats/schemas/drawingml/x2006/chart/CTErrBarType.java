/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STErrBarType;

public interface CTErrBarType
extends XmlObject {
    public static final DocumentFactory<CTErrBarType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cterrbartypedcb4type");
    public static final SchemaType type = Factory.getType();

    public STErrBarType.Enum getVal();

    public STErrBarType xgetVal();

    public boolean isSetVal();

    public void setVal(STErrBarType.Enum var1);

    public void xsetVal(STErrBarType var1);

    public void unsetVal();
}

