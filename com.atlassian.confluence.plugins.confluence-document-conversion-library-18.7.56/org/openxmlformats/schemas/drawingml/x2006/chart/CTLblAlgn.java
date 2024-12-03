/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLblAlgn;

public interface CTLblAlgn
extends XmlObject {
    public static final DocumentFactory<CTLblAlgn> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlblalgn133etype");
    public static final SchemaType type = Factory.getType();

    public STLblAlgn.Enum getVal();

    public STLblAlgn xgetVal();

    public void setVal(STLblAlgn.Enum var1);

    public void xsetVal(STLblAlgn var1);
}

