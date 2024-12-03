/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.chart.STDispBlanksAs;

public interface CTDispBlanksAs
extends XmlObject {
    public static final DocumentFactory<CTDispBlanksAs> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdispblanksas3069type");
    public static final SchemaType type = Factory.getType();

    public STDispBlanksAs.Enum getVal();

    public STDispBlanksAs xgetVal();

    public boolean isSetVal();

    public void setVal(STDispBlanksAs.Enum var1);

    public void xsetVal(STDispBlanksAs var1);

    public void unsetVal();
}

