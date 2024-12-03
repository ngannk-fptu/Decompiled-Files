/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextSpacingPoint;

public interface CTTextSpacingPoint
extends XmlObject {
    public static final DocumentFactory<CTTextSpacingPoint> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextspacingpoint6cf5type");
    public static final SchemaType type = Factory.getType();

    public int getVal();

    public STTextSpacingPoint xgetVal();

    public void setVal(int var1);

    public void xsetVal(STTextSpacingPoint var1);
}

