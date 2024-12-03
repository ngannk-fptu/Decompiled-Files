/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTBooleanProperty
extends XmlObject {
    public static final DocumentFactory<CTBooleanProperty> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctbooleanproperty1f3ctype");
    public static final SchemaType type = Factory.getType();

    public boolean getVal();

    public XmlBoolean xgetVal();

    public boolean isSetVal();

    public void setVal(boolean var1);

    public void xsetVal(XmlBoolean var1);

    public void unsetVal();
}

