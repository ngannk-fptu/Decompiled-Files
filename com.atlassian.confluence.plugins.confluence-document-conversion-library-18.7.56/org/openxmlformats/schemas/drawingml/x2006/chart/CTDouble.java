/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTDouble
extends XmlObject {
    public static final DocumentFactory<CTDouble> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdoublec10btype");
    public static final SchemaType type = Factory.getType();

    public double getVal();

    public XmlDouble xgetVal();

    public void setVal(double var1);

    public void xsetVal(XmlDouble var1);
}

