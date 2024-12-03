/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STSecondPieSize;

public interface CTSecondPieSize
extends XmlObject {
    public static final DocumentFactory<CTSecondPieSize> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsecondpiesize24edtype");
    public static final SchemaType type = Factory.getType();

    public Object getVal();

    public STSecondPieSize xgetVal();

    public boolean isSetVal();

    public void setVal(Object var1);

    public void xsetVal(STSecondPieSize var1);

    public void unsetVal();
}

