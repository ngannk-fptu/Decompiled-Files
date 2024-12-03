/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLayoutMode;

public interface CTLayoutMode
extends XmlObject {
    public static final DocumentFactory<CTLayoutMode> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlayoutmode53eftype");
    public static final SchemaType type = Factory.getType();

    public STLayoutMode.Enum getVal();

    public STLayoutMode xgetVal();

    public boolean isSetVal();

    public void setVal(STLayoutMode.Enum var1);

    public void xsetVal(STLayoutMode var1);

    public void unsetVal();
}

