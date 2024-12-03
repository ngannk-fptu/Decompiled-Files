/*
 * Decompiled with CFR 0.152.
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STGuid;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTAutoFilter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageBreak;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPane;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPrintOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSelection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetState;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;

public interface CTCustomSheetView
extends XmlObject {
    public static final DocumentFactory<CTCustomSheetView> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctcustomsheetview59d2type");
    public static final SchemaType type = Factory.getType();

    public CTPane getPane();

    public boolean isSetPane();

    public void setPane(CTPane var1);

    public CTPane addNewPane();

    public void unsetPane();

    public CTSelection getSelection();

    public boolean isSetSelection();

    public void setSelection(CTSelection var1);

    public CTSelection addNewSelection();

    public void unsetSelection();

    public CTPageBreak getRowBreaks();

    public boolean isSetRowBreaks();

    public void setRowBreaks(CTPageBreak var1);

    public CTPageBreak addNewRowBreaks();

    public void unsetRowBreaks();

    public CTPageBreak getColBreaks();

    public boolean isSetColBreaks();

    public void setColBreaks(CTPageBreak var1);

    public CTPageBreak addNewColBreaks();

    public void unsetColBreaks();

    public CTPageMargins getPageMargins();

    public boolean isSetPageMargins();

    public void setPageMargins(CTPageMargins var1);

    public CTPageMargins addNewPageMargins();

    public void unsetPageMargins();

    public CTPrintOptions getPrintOptions();

    public boolean isSetPrintOptions();

    public void setPrintOptions(CTPrintOptions var1);

    public CTPrintOptions addNewPrintOptions();

    public void unsetPrintOptions();

    public CTPageSetup getPageSetup();

    public boolean isSetPageSetup();

    public void setPageSetup(CTPageSetup var1);

    public CTPageSetup addNewPageSetup();

    public void unsetPageSetup();

    public CTHeaderFooter getHeaderFooter();

    public boolean isSetHeaderFooter();

    public void setHeaderFooter(CTHeaderFooter var1);

    public CTHeaderFooter addNewHeaderFooter();

    public void unsetHeaderFooter();

    public CTAutoFilter getAutoFilter();

    public boolean isSetAutoFilter();

    public void setAutoFilter(CTAutoFilter var1);

    public CTAutoFilter addNewAutoFilter();

    public void unsetAutoFilter();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public String getGuid();

    public STGuid xgetGuid();

    public void setGuid(String var1);

    public void xsetGuid(STGuid var1);

    public long getScale();

    public XmlUnsignedInt xgetScale();

    public boolean isSetScale();

    public void setScale(long var1);

    public void xsetScale(XmlUnsignedInt var1);

    public void unsetScale();

    public long getColorId();

    public XmlUnsignedInt xgetColorId();

    public boolean isSetColorId();

    public void setColorId(long var1);

    public void xsetColorId(XmlUnsignedInt var1);

    public void unsetColorId();

    public boolean getShowPageBreaks();

    public XmlBoolean xgetShowPageBreaks();

    public boolean isSetShowPageBreaks();

    public void setShowPageBreaks(boolean var1);

    public void xsetShowPageBreaks(XmlBoolean var1);

    public void unsetShowPageBreaks();

    public boolean getShowFormulas();

    public XmlBoolean xgetShowFormulas();

    public boolean isSetShowFormulas();

    public void setShowFormulas(boolean var1);

    public void xsetShowFormulas(XmlBoolean var1);

    public void unsetShowFormulas();

    public boolean getShowGridLines();

    public XmlBoolean xgetShowGridLines();

    public boolean isSetShowGridLines();

    public void setShowGridLines(boolean var1);

    public void xsetShowGridLines(XmlBoolean var1);

    public void unsetShowGridLines();

    public boolean getShowRowCol();

    public XmlBoolean xgetShowRowCol();

    public boolean isSetShowRowCol();

    public void setShowRowCol(boolean var1);

    public void xsetShowRowCol(XmlBoolean var1);

    public void unsetShowRowCol();

    public boolean getOutlineSymbols();

    public XmlBoolean xgetOutlineSymbols();

    public boolean isSetOutlineSymbols();

    public void setOutlineSymbols(boolean var1);

    public void xsetOutlineSymbols(XmlBoolean var1);

    public void unsetOutlineSymbols();

    public boolean getZeroValues();

    public XmlBoolean xgetZeroValues();

    public boolean isSetZeroValues();

    public void setZeroValues(boolean var1);

    public void xsetZeroValues(XmlBoolean var1);

    public void unsetZeroValues();

    public boolean getFitToPage();

    public XmlBoolean xgetFitToPage();

    public boolean isSetFitToPage();

    public void setFitToPage(boolean var1);

    public void xsetFitToPage(XmlBoolean var1);

    public void unsetFitToPage();

    public boolean getPrintArea();

    public XmlBoolean xgetPrintArea();

    public boolean isSetPrintArea();

    public void setPrintArea(boolean var1);

    public void xsetPrintArea(XmlBoolean var1);

    public void unsetPrintArea();

    public boolean getFilter();

    public XmlBoolean xgetFilter();

    public boolean isSetFilter();

    public void setFilter(boolean var1);

    public void xsetFilter(XmlBoolean var1);

    public void unsetFilter();

    public boolean getShowAutoFilter();

    public XmlBoolean xgetShowAutoFilter();

    public boolean isSetShowAutoFilter();

    public void setShowAutoFilter(boolean var1);

    public void xsetShowAutoFilter(XmlBoolean var1);

    public void unsetShowAutoFilter();

    public boolean getHiddenRows();

    public XmlBoolean xgetHiddenRows();

    public boolean isSetHiddenRows();

    public void setHiddenRows(boolean var1);

    public void xsetHiddenRows(XmlBoolean var1);

    public void unsetHiddenRows();

    public boolean getHiddenColumns();

    public XmlBoolean xgetHiddenColumns();

    public boolean isSetHiddenColumns();

    public void setHiddenColumns(boolean var1);

    public void xsetHiddenColumns(XmlBoolean var1);

    public void unsetHiddenColumns();

    public STSheetState.Enum getState();

    public STSheetState xgetState();

    public boolean isSetState();

    public void setState(STSheetState.Enum var1);

    public void xsetState(STSheetState var1);

    public void unsetState();

    public boolean getFilterUnique();

    public XmlBoolean xgetFilterUnique();

    public boolean isSetFilterUnique();

    public void setFilterUnique(boolean var1);

    public void xsetFilterUnique(XmlBoolean var1);

    public void unsetFilterUnique();

    public STSheetViewType.Enum getView();

    public STSheetViewType xgetView();

    public boolean isSetView();

    public void setView(STSheetViewType.Enum var1);

    public void xsetView(STSheetViewType var1);

    public void unsetView();

    public boolean getShowRuler();

    public XmlBoolean xgetShowRuler();

    public boolean isSetShowRuler();

    public void setShowRuler(boolean var1);

    public void xsetShowRuler(XmlBoolean var1);

    public void unsetShowRuler();

    public String getTopLeftCell();

    public STCellRef xgetTopLeftCell();

    public boolean isSetTopLeftCell();

    public void setTopLeftCell(String var1);

    public void xsetTopLeftCell(STCellRef var1);

    public void unsetTopLeftCell();
}

