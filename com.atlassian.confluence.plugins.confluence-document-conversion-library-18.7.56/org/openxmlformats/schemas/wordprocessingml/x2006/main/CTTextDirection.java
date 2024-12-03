/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.wordprocessingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTextDirection;

public interface CTTextDirection
extends XmlObject {
    public static final DocumentFactory<CTTextDirection> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextdirection0940type");
    public static final SchemaType type = Factory.getType();

    public STTextDirection.Enum getVal();

    public STTextDirection xgetVal();

    public void setVal(STTextDirection.Enum var1);

    public void xsetVal(STTextDirection var1);
}

