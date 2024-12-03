/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STScatterStyle;

public interface CTScatterStyle
extends XmlObject {
    public static final DocumentFactory<CTScatterStyle> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctscatterstyle94c9type");
    public static final SchemaType type = Factory.getType();

    public STScatterStyle.Enum getVal();

    public STScatterStyle xgetVal();

    public boolean isSetVal();

    public void setVal(STScatterStyle.Enum var1);

    public void xsetVal(STScatterStyle var1);

    public void unsetVal();
}

