/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.drawingml.x2006.chart.STThickness
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STThickness;

public interface CTThickness
extends XmlObject {
    public static final DocumentFactory<CTThickness> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctthicknessf632type");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STThickness xgetVal();

    public void setVal(Object var1);

    public void xsetVal(STThickness var1);
}

