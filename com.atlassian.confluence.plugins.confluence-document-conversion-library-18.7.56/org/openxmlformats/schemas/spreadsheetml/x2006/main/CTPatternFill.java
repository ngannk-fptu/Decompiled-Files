/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STPatternType;

public interface CTPatternFill
extends XmlObject {
    public static final DocumentFactory<CTPatternFill> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpatternfill7452type");
    public static final SchemaType type = Factory.getType();

    public CTColor getFgColor();

    public boolean isSetFgColor();

    public void setFgColor(CTColor var1);

    public CTColor addNewFgColor();

    public void unsetFgColor();

    public CTColor getBgColor();

    public boolean isSetBgColor();

    public void setBgColor(CTColor var1);

    public CTColor addNewBgColor();

    public void unsetBgColor();

    public STPatternType.Enum getPatternType();

    public STPatternType xgetPatternType();

    public boolean isSetPatternType();

    public void setPatternType(STPatternType.Enum var1);

    public void xsetPatternType(STPatternType var1);

    public void unsetPatternType();
}

