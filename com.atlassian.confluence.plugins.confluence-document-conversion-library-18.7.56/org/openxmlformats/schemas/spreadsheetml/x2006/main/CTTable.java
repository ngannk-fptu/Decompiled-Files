/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.STTableType
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STXstring;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSortState;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableStyleInfo;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STDxfId;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STTableType;

public interface CTTable
extends XmlObject {
    public static final DocumentFactory<CTTable> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "cttable736dtype");
    public static final SchemaType type = Factory.getType();

    public CTAutoFilter getAutoFilter();

    public boolean isSetAutoFilter();

    public void setAutoFilter(CTAutoFilter var1);

    public CTAutoFilter addNewAutoFilter();

    public void unsetAutoFilter();

    public CTSortState getSortState();

    public boolean isSetSortState();

    public void setSortState(CTSortState var1);

    public CTSortState addNewSortState();

    public void unsetSortState();

    public CTTableColumns getTableColumns();

    public void setTableColumns(CTTableColumns var1);

    public CTTableColumns addNewTableColumns();

    public CTTableStyleInfo getTableStyleInfo();

    public boolean isSetTableStyleInfo();

    public void setTableStyleInfo(CTTableStyleInfo var1);

    public CTTableStyleInfo addNewTableStyleInfo();

    public void unsetTableStyleInfo();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public long getId();

    public XmlUnsignedInt xgetId();

    public void setId(long var1);

    public void xsetId(XmlUnsignedInt var1);

    public String getName();

    public STXstring xgetName();

    public boolean isSetName();

    public void setName(String var1);

    public void xsetName(STXstring var1);

    public void unsetName();

    public String getDisplayName();

    public STXstring xgetDisplayName();

    public void setDisplayName(String var1);

    public void xsetDisplayName(STXstring var1);

    public String getComment();

    public STXstring xgetComment();

    public boolean isSetComment();

    public void setComment(String var1);

    public void xsetComment(STXstring var1);

    public void unsetComment();

    public String getRef();

    public STRef xgetRef();

    public void setRef(String var1);

    public void xsetRef(STRef var1);

    public STTableType.Enum getTableType();

    public STTableType xgetTableType();

    public boolean isSetTableType();

    public void setTableType(STTableType.Enum var1);

    public void xsetTableType(STTableType var1);

    public void unsetTableType();

    public long getHeaderRowCount();

    public XmlUnsignedInt xgetHeaderRowCount();

    public boolean isSetHeaderRowCount();

    public void setHeaderRowCount(long var1);

    public void xsetHeaderRowCount(XmlUnsignedInt var1);

    public void unsetHeaderRowCount();

    public boolean getInsertRow();

    public XmlBoolean xgetInsertRow();

    public boolean isSetInsertRow();

    public void setInsertRow(boolean var1);

    public void xsetInsertRow(XmlBoolean var1);

    public void unsetInsertRow();

    public boolean getInsertRowShift();

    public XmlBoolean xgetInsertRowShift();

    public boolean isSetInsertRowShift();

    public void setInsertRowShift(boolean var1);

    public void xsetInsertRowShift(XmlBoolean var1);

    public void unsetInsertRowShift();

    public long getTotalsRowCount();

    public XmlUnsignedInt xgetTotalsRowCount();

    public boolean isSetTotalsRowCount();

    public void setTotalsRowCount(long var1);

    public void xsetTotalsRowCount(XmlUnsignedInt var1);

    public void unsetTotalsRowCount();

    public boolean getTotalsRowShown();

    public XmlBoolean xgetTotalsRowShown();

    public boolean isSetTotalsRowShown();

    public void setTotalsRowShown(boolean var1);

    public void xsetTotalsRowShown(XmlBoolean var1);

    public void unsetTotalsRowShown();

    public boolean getPublished();

    public XmlBoolean xgetPublished();

    public boolean isSetPublished();

    public void setPublished(boolean var1);

    public void xsetPublished(XmlBoolean var1);

    public void unsetPublished();

    public long getHeaderRowDxfId();

    public STDxfId xgetHeaderRowDxfId();

    public boolean isSetHeaderRowDxfId();

    public void setHeaderRowDxfId(long var1);

    public void xsetHeaderRowDxfId(STDxfId var1);

    public void unsetHeaderRowDxfId();

    public long getDataDxfId();

    public STDxfId xgetDataDxfId();

    public boolean isSetDataDxfId();

    public void setDataDxfId(long var1);

    public void xsetDataDxfId(STDxfId var1);

    public void unsetDataDxfId();

    public long getTotalsRowDxfId();

    public STDxfId xgetTotalsRowDxfId();

    public boolean isSetTotalsRowDxfId();

    public void setTotalsRowDxfId(long var1);

    public void xsetTotalsRowDxfId(STDxfId var1);

    public void unsetTotalsRowDxfId();

    public long getHeaderRowBorderDxfId();

    public STDxfId xgetHeaderRowBorderDxfId();

    public boolean isSetHeaderRowBorderDxfId();

    public void setHeaderRowBorderDxfId(long var1);

    public void xsetHeaderRowBorderDxfId(STDxfId var1);

    public void unsetHeaderRowBorderDxfId();

    public long getTableBorderDxfId();

    public STDxfId xgetTableBorderDxfId();

    public boolean isSetTableBorderDxfId();

    public void setTableBorderDxfId(long var1);

    public void xsetTableBorderDxfId(STDxfId var1);

    public void unsetTableBorderDxfId();

    public long getTotalsRowBorderDxfId();

    public STDxfId xgetTotalsRowBorderDxfId();

    public boolean isSetTotalsRowBorderDxfId();

    public void setTotalsRowBorderDxfId(long var1);

    public void xsetTotalsRowBorderDxfId(STDxfId var1);

    public void unsetTotalsRowBorderDxfId();

    public String getHeaderRowCellStyle();

    public STXstring xgetHeaderRowCellStyle();

    public boolean isSetHeaderRowCellStyle();

    public void setHeaderRowCellStyle(String var1);

    public void xsetHeaderRowCellStyle(STXstring var1);

    public void unsetHeaderRowCellStyle();

    public String getDataCellStyle();

    public STXstring xgetDataCellStyle();

    public boolean isSetDataCellStyle();

    public void setDataCellStyle(String var1);

    public void xsetDataCellStyle(STXstring var1);

    public void unsetDataCellStyle();

    public String getTotalsRowCellStyle();

    public STXstring xgetTotalsRowCellStyle();

    public boolean isSetTotalsRowCellStyle();

    public void setTotalsRowCellStyle(String var1);

    public void xsetTotalsRowCellStyle(STXstring var1);

    public void unsetTotalsRowCellStyle();

    public long getConnectionId();

    public XmlUnsignedInt xgetConnectionId();

    public boolean isSetConnectionId();

    public void setConnectionId(long var1);

    public void xsetConnectionId(XmlUnsignedInt var1);

    public void unsetConnectionId();
}

