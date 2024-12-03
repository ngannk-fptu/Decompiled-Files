/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STSplitType;

public interface CTSplitType
extends XmlObject {
    public static final DocumentFactory<CTSplitType> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsplittypeac32type");
    public static final SchemaType type = Factory.getType();

    public STSplitType.Enum getVal();

    public STSplitType xgetVal();

    public boolean isSetVal();

    public void setVal(STSplitType.Enum var1);

    public void xsetVal(STSplitType var1);

    public void unsetVal();
}

