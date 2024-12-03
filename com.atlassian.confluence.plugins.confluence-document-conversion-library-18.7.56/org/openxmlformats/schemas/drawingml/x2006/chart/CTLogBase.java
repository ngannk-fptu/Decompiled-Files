/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STLogBase;

public interface CTLogBase
extends XmlObject {
    public static final DocumentFactory<CTLogBase> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctlogbase9191type");
    public static final SchemaType type = Factory.getType();

    public double getVal();

    public STLogBase xgetVal();

    public void setVal(double var1);

    public void xsetVal(STLogBase var1);
}

