/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;

public interface CTSheetFormatPr
extends XmlObject {
    public static final DocumentFactory<CTSheetFormatPr> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheetformatprdef7type");
    public static final SchemaType type = Factory.getType();

    public long getBaseColWidth();

    public XmlUnsignedInt xgetBaseColWidth();

    public boolean isSetBaseColWidth();

    public void setBaseColWidth(long var1);

    public void xsetBaseColWidth(XmlUnsignedInt var1);

    public void unsetBaseColWidth();

    public double getDefaultColWidth();

    public XmlDouble xgetDefaultColWidth();

    public boolean isSetDefaultColWidth();

    public void setDefaultColWidth(double var1);

    public void xsetDefaultColWidth(XmlDouble var1);

    public void unsetDefaultColWidth();

    public double getDefaultRowHeight();

    public XmlDouble xgetDefaultRowHeight();

    public void setDefaultRowHeight(double var1);

    public void xsetDefaultRowHeight(XmlDouble var1);

    public boolean getCustomHeight();

    public XmlBoolean xgetCustomHeight();

    public boolean isSetCustomHeight();

    public void setCustomHeight(boolean var1);

    public void xsetCustomHeight(XmlBoolean var1);

    public void unsetCustomHeight();

    public boolean getZeroHeight();

    public XmlBoolean xgetZeroHeight();

    public boolean isSetZeroHeight();

    public void setZeroHeight(boolean var1);

    public void xsetZeroHeight(XmlBoolean var1);

    public void unsetZeroHeight();

    public boolean getThickTop();

    public XmlBoolean xgetThickTop();

    public boolean isSetThickTop();

    public void setThickTop(boolean var1);

    public void xsetThickTop(XmlBoolean var1);

    public void unsetThickTop();

    public boolean getThickBottom();

    public XmlBoolean xgetThickBottom();

    public boolean isSetThickBottom();

    public void setThickBottom(boolean var1);

    public void xsetThickBottom(XmlBoolean var1);

    public void unsetThickBottom();

    public short getOutlineLevelRow();

    public XmlUnsignedByte xgetOutlineLevelRow();

    public boolean isSetOutlineLevelRow();

    public void setOutlineLevelRow(short var1);

    public void xsetOutlineLevelRow(XmlUnsignedByte var1);

    public void unsetOutlineLevelRow();

    public short getOutlineLevelCol();

    public XmlUnsignedByte xgetOutlineLevelCol();

    public boolean isSetOutlineLevelCol();

    public void setOutlineLevelCol(short var1);

    public void xsetOutlineLevelCol(XmlUnsignedByte var1);

    public void unsetOutlineLevelCol();
}

