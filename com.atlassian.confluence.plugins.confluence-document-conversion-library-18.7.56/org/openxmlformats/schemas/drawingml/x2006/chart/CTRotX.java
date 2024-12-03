/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.STRotX
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STRotX;

public interface CTRotX
extends XmlObject {
    public static final DocumentFactory<CTRotX> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctrotx3c3btype");
    public static final SchemaType type = Factory.getType();

    public byte getVal();

    public STRotX xgetVal();

    public boolean isSetVal();

    public void setVal(byte var1);

    public void xsetVal(STRotX var1);

    public void unsetVal();
}

