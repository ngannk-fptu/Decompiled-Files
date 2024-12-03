/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STGrouping;

public interface CTGrouping
extends XmlObject {
    public static final DocumentFactory<CTGrouping> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctgroupingdcd9type");
    public static final SchemaType type = Factory.getType();

    public STGrouping.Enum getVal();

    public STGrouping xgetVal();

    public boolean isSetVal();

    public void setVal(STGrouping.Enum var1);

    public void xsetVal(STGrouping var1);

    public void unsetVal();
}

