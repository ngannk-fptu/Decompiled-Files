/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellAlignment;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCellProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STBorderId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellStyleXfId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFillId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STFontId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STNumFmtId;

public interface CTXf
extends XmlObject {
    public static final DocumentFactory<CTXf> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctxf97f7type");
    public static final SchemaType type = Factory.getType();

    public CTCellAlignment getAlignment();

    public boolean isSetAlignment();

    public void setAlignment(CTCellAlignment var1);

    public CTCellAlignment addNewAlignment();

    public void unsetAlignment();

    public CTCellProtection getProtection();

    public boolean isSetProtection();

    public void setProtection(CTCellProtection var1);

    public CTCellProtection addNewProtection();

    public void unsetProtection();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getNumFmtId();

    public STNumFmtId xgetNumFmtId();

    public boolean isSetNumFmtId();

    public void setNumFmtId(long var1);

    public void xsetNumFmtId(STNumFmtId var1);

    public void unsetNumFmtId();

    public long getFontId();

    public STFontId xgetFontId();

    public boolean isSetFontId();

    public void setFontId(long var1);

    public void xsetFontId(STFontId var1);

    public void unsetFontId();

    public long getFillId();

    public STFillId xgetFillId();

    public boolean isSetFillId();

    public void setFillId(long var1);

    public void xsetFillId(STFillId var1);

    public void unsetFillId();

    public long getBorderId();

    public STBorderId xgetBorderId();

    public boolean isSetBorderId();

    public void setBorderId(long var1);

    public void xsetBorderId(STBorderId var1);

    public void unsetBorderId();

    public long getXfId();

    public STCellStyleXfId xgetXfId();

    public boolean isSetXfId();

    public void setXfId(long var1);

    public void xsetXfId(STCellStyleXfId var1);

    public void unsetXfId();

    public boolean getQuotePrefix();

    public XmlBoolean xgetQuotePrefix();

    public boolean isSetQuotePrefix();

    public void setQuotePrefix(boolean var1);

    public void xsetQuotePrefix(XmlBoolean var1);

    public void unsetQuotePrefix();

    public boolean getPivotButton();

    public XmlBoolean xgetPivotButton();

    public boolean isSetPivotButton();

    public void setPivotButton(boolean var1);

    public void xsetPivotButton(XmlBoolean var1);

    public void unsetPivotButton();

    public boolean getApplyNumberFormat();

    public XmlBoolean xgetApplyNumberFormat();

    public boolean isSetApplyNumberFormat();

    public void setApplyNumberFormat(boolean var1);

    public void xsetApplyNumberFormat(XmlBoolean var1);

    public void unsetApplyNumberFormat();

    public boolean getApplyFont();

    public XmlBoolean xgetApplyFont();

    public boolean isSetApplyFont();

    public void setApplyFont(boolean var1);

    public void xsetApplyFont(XmlBoolean var1);

    public void unsetApplyFont();

    public boolean getApplyFill();

    public XmlBoolean xgetApplyFill();

    public boolean isSetApplyFill();

    public void setApplyFill(boolean var1);

    public void xsetApplyFill(XmlBoolean var1);

    public void unsetApplyFill();

    public boolean getApplyBorder();

    public XmlBoolean xgetApplyBorder();

    public boolean isSetApplyBorder();

    public void setApplyBorder(boolean var1);

    public void xsetApplyBorder(XmlBoolean var1);

    public void unsetApplyBorder();

    public boolean getApplyAlignment();

    public XmlBoolean xgetApplyAlignment();

    public boolean isSetApplyAlignment();

    public void setApplyAlignment(boolean var1);

    public void xsetApplyAlignment(XmlBoolean var1);

    public void unsetApplyAlignment();

    public boolean getApplyProtection();

    public XmlBoolean xgetApplyProtection();

    public boolean isSetApplyProtection();

    public void setApplyProtection(boolean var1);

    public void xsetApplyProtection(XmlBoolean var1);

    public void unsetApplyProtection();
}

