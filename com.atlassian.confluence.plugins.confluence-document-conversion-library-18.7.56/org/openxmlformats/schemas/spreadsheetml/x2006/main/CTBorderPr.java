/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderStyle;

public interface CTBorderPr
extends XmlObject {
    public static final DocumentFactory<CTBorderPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctborderpre497type");
    public static final SchemaType type = Factory.getType();

    public CTColor getColor();

    public boolean isSetColor();

    public void setColor(CTColor var1);

    public CTColor addNewColor();

    public void unsetColor();

    public STBorderStyle.Enum getStyle();

    public STBorderStyle xgetStyle();

    public boolean isSetStyle();

    public void setStyle(STBorderStyle.Enum var1);

    public void xsetStyle(STBorderStyle var1);

    public void unsetStyle();
}

