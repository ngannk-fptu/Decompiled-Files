/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.STCoordinate32;
import org.openxmlformats.schemas.drawingml.x2006.main.STTextTabAlignType;

public interface CTTextTabStop
extends XmlObject {
    public static final DocumentFactory<CTTextTabStop> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttexttabstopb57btype");
    public static final SchemaType type = Factory.getType();

    public Object getPos();

    public STCoordinate32 xgetPos();

    public boolean isSetPos();

    public void setPos(Object var1);

    public void xsetPos(STCoordinate32 var1);

    public void unsetPos();

    public STTextTabAlignType.Enum getAlgn();

    public STTextTabAlignType xgetAlgn();

    public boolean isSetAlgn();

    public void setAlgn(STTextTabAlignType.Enum var1);

    public void xsetAlgn(STTextTabAlignType var1);

    public void unsetAlgn();
}

