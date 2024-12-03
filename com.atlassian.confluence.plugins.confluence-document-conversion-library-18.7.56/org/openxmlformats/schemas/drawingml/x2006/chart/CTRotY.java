/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.STRotY
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STRotY;

public interface CTRotY
extends XmlObject {
    public static final DocumentFactory<CTRotY> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctroty8f1atype");
    public static final SchemaType type = Factory.getType();

    public int getVal();

    public STRotY xgetVal();

    public boolean isSetVal();

    public void setVal(int var1);

    public void xsetVal(STRotY var1);

    public void unsetVal();
}

