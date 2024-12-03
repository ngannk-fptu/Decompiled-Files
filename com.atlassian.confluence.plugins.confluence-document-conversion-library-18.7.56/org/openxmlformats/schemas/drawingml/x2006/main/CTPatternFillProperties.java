/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.drawingml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetPatternVal;

public interface CTPatternFillProperties
extends XmlObject {
    public static final DocumentFactory<CTPatternFillProperties> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctpatternfillproperties3637type");
    public static final SchemaType type = Factory.getType();

    public CTColor getFgClr();

    public boolean isSetFgClr();

    public void setFgClr(CTColor var1);

    public CTColor addNewFgClr();

    public void unsetFgClr();

    public CTColor getBgClr();

    public boolean isSetBgClr();

    public void setBgClr(CTColor var1);

    public CTColor addNewBgClr();

    public void unsetBgClr();

    public STPresetPatternVal.Enum getPrst();

    public STPresetPatternVal xgetPrst();

    public boolean isSetPrst();

    public void setPrst(STPresetPatternVal.Enum var1);

    public void xsetPrst(STPresetPatternVal var1);

    public void unsetPrst();
}

