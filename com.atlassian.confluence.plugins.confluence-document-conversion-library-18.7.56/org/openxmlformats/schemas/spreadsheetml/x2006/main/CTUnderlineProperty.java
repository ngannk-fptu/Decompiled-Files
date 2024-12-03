/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnderlineValues;

public interface CTUnderlineProperty
extends XmlObject {
    public static final DocumentFactory<CTUnderlineProperty> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctunderlineproperty8e20type");
    public static final SchemaType type = Factory.getType();

    public STUnderlineValues.Enum getVal();

    public STUnderlineValues xgetVal();

    public boolean isSetVal();

    public void setVal(STUnderlineValues.Enum var1);

    public void xsetVal(STUnderlineValues var1);

    public void unsetVal();
}

