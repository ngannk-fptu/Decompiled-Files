/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STUnsignedShortHex;

public interface CTSheetProtection
extends XmlObject {
    public static final DocumentFactory<CTSheetProtection> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheetprotection22f7type");
    public static final SchemaType type = Factory.getType();

    public byte[] getPassword();

    public STUnsignedShortHex xgetPassword();

    public boolean isSetPassword();

    public void setPassword(byte[] var1);

    public void xsetPassword(STUnsignedShortHex var1);

    public void unsetPassword();

    public String getAlgorithmName();

    public STXstring xgetAlgorithmName();

    public boolean isSetAlgorithmName();

    public void setAlgorithmName(String var1);

    public void xsetAlgorithmName(STXstring var1);

    public void unsetAlgorithmName();

    public byte[] getHashValue();

    public XmlBase64Binary xgetHashValue();

    public boolean isSetHashValue();

    public void setHashValue(byte[] var1);

    public void xsetHashValue(XmlBase64Binary var1);

    public void unsetHashValue();

    public byte[] getSaltValue();

    public XmlBase64Binary xgetSaltValue();

    public boolean isSetSaltValue();

    public void setSaltValue(byte[] var1);

    public void xsetSaltValue(XmlBase64Binary var1);

    public void unsetSaltValue();

    public long getSpinCount();

    public XmlUnsignedInt xgetSpinCount();

    public boolean isSetSpinCount();

    public void setSpinCount(long var1);

    public void xsetSpinCount(XmlUnsignedInt var1);

    public void unsetSpinCount();

    public boolean getSheet();

    public XmlBoolean xgetSheet();

    public boolean isSetSheet();

    public void setSheet(boolean var1);

    public void xsetSheet(XmlBoolean var1);

    public void unsetSheet();

    public boolean getObjects();

    public XmlBoolean xgetObjects();

    public boolean isSetObjects();

    public void setObjects(boolean var1);

    public void xsetObjects(XmlBoolean var1);

    public void unsetObjects();

    public boolean getScenarios();

    public XmlBoolean xgetScenarios();

    public boolean isSetScenarios();

    public void setScenarios(boolean var1);

    public void xsetScenarios(XmlBoolean var1);

    public void unsetScenarios();

    public boolean getFormatCells();

    public XmlBoolean xgetFormatCells();

    public boolean isSetFormatCells();

    public void setFormatCells(boolean var1);

    public void xsetFormatCells(XmlBoolean var1);

    public void unsetFormatCells();

    public boolean getFormatColumns();

    public XmlBoolean xgetFormatColumns();

    public boolean isSetFormatColumns();

    public void setFormatColumns(boolean var1);

    public void xsetFormatColumns(XmlBoolean var1);

    public void unsetFormatColumns();

    public boolean getFormatRows();

    public XmlBoolean xgetFormatRows();

    public boolean isSetFormatRows();

    public void setFormatRows(boolean var1);

    public void xsetFormatRows(XmlBoolean var1);

    public void unsetFormatRows();

    public boolean getInsertColumns();

    public XmlBoolean xgetInsertColumns();

    public boolean isSetInsertColumns();

    public void setInsertColumns(boolean var1);

    public void xsetInsertColumns(XmlBoolean var1);

    public void unsetInsertColumns();

    public boolean getInsertRows();

    public XmlBoolean xgetInsertRows();

    public boolean isSetInsertRows();

    public void setInsertRows(boolean var1);

    public void xsetInsertRows(XmlBoolean var1);

    public void unsetInsertRows();

    public boolean getInsertHyperlinks();

    public XmlBoolean xgetInsertHyperlinks();

    public boolean isSetInsertHyperlinks();

    public void setInsertHyperlinks(boolean var1);

    public void xsetInsertHyperlinks(XmlBoolean var1);

    public void unsetInsertHyperlinks();

    public boolean getDeleteColumns();

    public XmlBoolean xgetDeleteColumns();

    public boolean isSetDeleteColumns();

    public void setDeleteColumns(boolean var1);

    public void xsetDeleteColumns(XmlBoolean var1);

    public void unsetDeleteColumns();

    public boolean getDeleteRows();

    public XmlBoolean xgetDeleteRows();

    public boolean isSetDeleteRows();

    public void setDeleteRows(boolean var1);

    public void xsetDeleteRows(XmlBoolean var1);

    public void unsetDeleteRows();

    public boolean getSelectLockedCells();

    public XmlBoolean xgetSelectLockedCells();

    public boolean isSetSelectLockedCells();

    public void setSelectLockedCells(boolean var1);

    public void xsetSelectLockedCells(XmlBoolean var1);

    public void unsetSelectLockedCells();

    public boolean getSort();

    public XmlBoolean xgetSort();

    public boolean isSetSort();

    public void setSort(boolean var1);

    public void xsetSort(XmlBoolean var1);

    public void unsetSort();

    public boolean getAutoFilter();

    public XmlBoolean xgetAutoFilter();

    public boolean isSetAutoFilter();

    public void setAutoFilter(boolean var1);

    public void xsetAutoFilter(XmlBoolean var1);

    public void unsetAutoFilter();

    public boolean getPivotTables();

    public XmlBoolean xgetPivotTables();

    public boolean isSetPivotTables();

    public void setPivotTables(boolean var1);

    public void xsetPivotTables(XmlBoolean var1);

    public void unsetPivotTables();

    public boolean getSelectUnlockedCells();

    public XmlBoolean xgetSelectUnlockedCells();

    public boolean isSetSelectUnlockedCells();

    public void setSelectUnlockedCells(boolean var1);

    public void xsetSelectUnlockedCells(XmlBoolean var1);

    public void unsetSelectUnlockedCells();
}

