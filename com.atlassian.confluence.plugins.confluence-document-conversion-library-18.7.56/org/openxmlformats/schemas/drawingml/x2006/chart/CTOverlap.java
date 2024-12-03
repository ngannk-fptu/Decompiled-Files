/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STOverlap;

public interface CTOverlap
extends XmlObject {
    public static final DocumentFactory<CTOverlap> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctoverlapfd3ftype");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STOverlap xgetVal();

    public boolean isSetVal();

    public void setVal(Object var1);

    public void xsetVal(STOverlap var1);

    public void unsetVal();
}

