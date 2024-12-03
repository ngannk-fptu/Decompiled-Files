/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextFontSize;

public interface CTTextBulletSizePoint
extends XmlObject {
    public static final DocumentFactory<CTTextBulletSizePoint> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttextbulletsizepointe4f1type");
    public static final SchemaType type = Factory.getType();

    public int getVal();

    public STTextFontSize xgetVal();

    public void setVal(int var1);

    public void xsetVal(STTextFontSize var1);
}

