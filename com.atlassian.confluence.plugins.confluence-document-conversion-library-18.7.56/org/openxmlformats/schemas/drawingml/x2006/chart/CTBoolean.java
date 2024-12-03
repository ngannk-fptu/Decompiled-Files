/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.chart;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTBoolean
extends XmlObject {
    public static final DocumentFactory<CTBoolean> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbooleancc3etype");
    public static final SchemaType type = Factory.getType();

    public boolean getVal();

    public XmlBoolean xgetVal();

    public boolean isSetVal();

    public void setVal(boolean var1);

    public void xsetVal(XmlBoolean var1);

    public void unsetVal();
}

