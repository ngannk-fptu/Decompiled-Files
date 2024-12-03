/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRelativeRect;
import org.openxmlformats.schemas.drawingml.x2006.main.STPathShadeType;

public interface CTPathShadeProperties
extends XmlObject {
    public static final DocumentFactory<CTPathShadeProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpathshadeproperties7ccctype");
    public static final SchemaType type = Factory.getType();

    public CTRelativeRect getFillToRect();

    public boolean isSetFillToRect();

    public void setFillToRect(CTRelativeRect var1);

    public CTRelativeRect addNewFillToRect();

    public void unsetFillToRect();

    public STPathShadeType.Enum getPath();

    public STPathShadeType xgetPath();

    public boolean isSetPath();

    public void setPath(STPathShadeType.Enum var1);

    public void xsetPath(STPathShadeType var1);

    public void unsetPath();
}

