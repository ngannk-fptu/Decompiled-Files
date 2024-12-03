/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTFontSize
extends XmlObject {
    public static final DocumentFactory<CTFontSize> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctfontsizeb3b9type");
    public static final SchemaType type = Factory.getType();

    public double getVal();

    public XmlDouble xgetVal();

    public void setVal(double var1);

    public void xsetVal(XmlDouble var1);
}

