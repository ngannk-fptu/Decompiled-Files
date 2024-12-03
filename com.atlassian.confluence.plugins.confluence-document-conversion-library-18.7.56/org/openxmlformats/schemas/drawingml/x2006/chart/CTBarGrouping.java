/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STBarGrouping;

public interface CTBarGrouping
extends XmlObject {
    public static final DocumentFactory<CTBarGrouping> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbargrouping8bf0type");
    public static final SchemaType type = Factory.getType();

    public STBarGrouping.Enum getVal();

    public STBarGrouping xgetVal();

    public boolean isSetVal();

    public void setVal(STBarGrouping.Enum var1);

    public void xsetVal(STBarGrouping var1);

    public void unsetVal();
}

