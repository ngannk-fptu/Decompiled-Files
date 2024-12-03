/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTIntProperty
extends XmlObject {
    public static final DocumentFactory<CTIntProperty> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctintproperty32c3type");
    public static final SchemaType type = Factory.getType();

    public int getVal();

    public XmlInt xgetVal();

    public void setVal(int var1);

    public void xsetVal(XmlInt var1);
}

