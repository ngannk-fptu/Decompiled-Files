/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.STFirstSliceAng
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STFirstSliceAng;

public interface CTFirstSliceAng
extends XmlObject {
    public static final DocumentFactory<CTFirstSliceAng> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfirstsliceang0ceetype");
    public static final SchemaType type = Factory.getType();

    public int getVal();

    public STFirstSliceAng xgetVal();

    public boolean isSetVal();

    public void setVal(int var1);

    public void xsetVal(STFirstSliceAng var1);

    public void unsetVal();
}

