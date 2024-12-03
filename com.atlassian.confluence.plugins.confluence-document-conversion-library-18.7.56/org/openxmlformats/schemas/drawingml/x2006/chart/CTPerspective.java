/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.STPerspective
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STPerspective;

public interface CTPerspective
extends XmlObject {
    public static final DocumentFactory<CTPerspective> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctperspectivefd2atype");
    public static final SchemaType type = Factory.getType();

    public short getVal();

    public STPerspective xgetVal();

    public boolean isSetVal();

    public void setVal(short var1);

    public void xsetVal(STPerspective var1);

    public void unsetVal();
}

