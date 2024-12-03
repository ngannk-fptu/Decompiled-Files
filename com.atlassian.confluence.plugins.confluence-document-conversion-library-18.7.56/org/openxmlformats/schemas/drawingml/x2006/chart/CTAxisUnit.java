/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.STAxisUnit
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STAxisUnit;

public interface CTAxisUnit
extends XmlObject {
    public static final DocumentFactory<CTAxisUnit> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctaxisunitead7type");
    public static final SchemaType type = Factory.getType();

    public double getVal();

    public STAxisUnit xgetVal();

    public void setVal(double var1);

    public void xsetVal(STAxisUnit var1);
}

