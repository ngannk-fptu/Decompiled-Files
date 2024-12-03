/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotSelection
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import java.util.List;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPane;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPivotSelection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSelection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STCellRef;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSheetViewType;

public interface CTSheetView
extends XmlObject {
    public static final DocumentFactory<CTSheetView> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsheetview0f43type");
    public static final SchemaType type = Factory.getType();

    public CTPane getPane();

    public boolean isSetPane();

    public void setPane(CTPane var1);

    public CTPane addNewPane();

    public void unsetPane();

    public List<CTSelection> getSelectionList();

    public CTSelection[] getSelectionArray();

    public CTSelection getSelectionArray(int var1);

    public int sizeOfSelectionArray();

    public void setSelectionArray(CTSelection[] var1);

    public void setSelectionArray(int var1, CTSelection var2);

    public CTSelection insertNewSelection(int var1);

    public CTSelection addNewSelection();

    public void removeSelection(int var1);

    public List<CTPivotSelection> getPivotSelectionList();

    public CTPivotSelection[] getPivotSelectionArray();

    public CTPivotSelection getPivotSelectionArray(int var1);

    public int sizeOfPivotSelectionArray();

    public void setPivotSelectionArray(CTPivotSelection[] var1);

    public void setPivotSelectionArray(int var1, CTPivotSelection var2);

    public CTPivotSelection insertNewPivotSelection(int var1);

    public CTPivotSelection addNewPivotSelection();

    public void removePivotSelection(int var1);

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();

    public boolean getWindowProtection();

    public XmlBoolean xgetWindowProtection();

    public boolean isSetWindowProtection();

    public void setWindowProtection(boolean var1);

    public void xsetWindowProtection(XmlBoolean var1);

    public void unsetWindowProtection();

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

    public boolean getShowRowColHeaders();

    public XmlBoolean xgetShowRowColHeaders();

    public boolean isSetShowRowColHeaders();

    public void setShowRowColHeaders(boolean var1);

    public void xsetShowRowColHeaders(XmlBoolean var1);

    public void unsetShowRowColHeaders();

    public boolean getShowZeros();

    public XmlBoolean xgetShowZeros();

    public boolean isSetShowZeros();

    public void setShowZeros(boolean var1);

    public void xsetShowZeros(XmlBoolean var1);

    public void unsetShowZeros();

    public boolean getRightToLeft();

    public XmlBoolean xgetRightToLeft();

    public boolean isSetRightToLeft();

    public void setRightToLeft(boolean var1);

    public void xsetRightToLeft(XmlBoolean var1);

    public void unsetRightToLeft();

    public boolean getTabSelected();

    public XmlBoolean xgetTabSelected();

    public boolean isSetTabSelected();

    public void setTabSelected(boolean var1);

    public void xsetTabSelected(XmlBoolean var1);

    public void unsetTabSelected();

    public boolean getShowRuler();

    public XmlBoolean xgetShowRuler();

    public boolean isSetShowRuler();

    public void setShowRuler(boolean var1);

    public void xsetShowRuler(XmlBoolean var1);

    public void unsetShowRuler();

    public boolean getShowOutlineSymbols();

    public XmlBoolean xgetShowOutlineSymbols();

    public boolean isSetShowOutlineSymbols();

    public void setShowOutlineSymbols(boolean var1);

    public void xsetShowOutlineSymbols(XmlBoolean var1);

    public void unsetShowOutlineSymbols();

    public boolean getDefaultGridColor();

    public XmlBoolean xgetDefaultGridColor();

    public boolean isSetDefaultGridColor();

    public void setDefaultGridColor(boolean var1);

    public void xsetDefaultGridColor(XmlBoolean var1);

    public void unsetDefaultGridColor();

    public boolean getShowWhiteSpace();

    public XmlBoolean xgetShowWhiteSpace();

    public boolean isSetShowWhiteSpace();

    public void setShowWhiteSpace(boolean var1);

    public void xsetShowWhiteSpace(XmlBoolean var1);

    public void unsetShowWhiteSpace();

    public STSheetViewType.Enum getView();

    public STSheetViewType xgetView();

    public boolean isSetView();

    public void setView(STSheetViewType.Enum var1);

    public void xsetView(STSheetViewType var1);

    public void unsetView();

    public String getTopLeftCell();

    public STCellRef xgetTopLeftCell();

    public boolean isSetTopLeftCell();

    public void setTopLeftCell(String var1);

    public void xsetTopLeftCell(STCellRef var1);

    public void unsetTopLeftCell();

    public long getColorId();

    public XmlUnsignedInt xgetColorId();

    public boolean isSetColorId();

    public void setColorId(long var1);

    public void xsetColorId(XmlUnsignedInt var1);

    public void unsetColorId();

    public long getZoomScale();

    public XmlUnsignedInt xgetZoomScale();

    public boolean isSetZoomScale();

    public void setZoomScale(long var1);

    public void xsetZoomScale(XmlUnsignedInt var1);

    public void unsetZoomScale();

    public long getZoomScaleNormal();

    public XmlUnsignedInt xgetZoomScaleNormal();

    public boolean isSetZoomScaleNormal();

    public void setZoomScaleNormal(long var1);

    public void xsetZoomScaleNormal(XmlUnsignedInt var1);

    public void unsetZoomScaleNormal();

    public long getZoomScaleSheetLayoutView();

    public XmlUnsignedInt xgetZoomScaleSheetLayoutView();

    public boolean isSetZoomScaleSheetLayoutView();

    public void setZoomScaleSheetLayoutView(long var1);

    public void xsetZoomScaleSheetLayoutView(XmlUnsignedInt var1);

    public void unsetZoomScaleSheetLayoutView();

    public long getZoomScalePageLayoutView();

    public XmlUnsignedInt xgetZoomScalePageLayoutView();

    public boolean isSetZoomScalePageLayoutView();

    public void setZoomScalePageLayoutView(long var1);

    public void xsetZoomScalePageLayoutView(XmlUnsignedInt var1);

    public void unsetZoomScalePageLayoutView();

    public long getWorkbookViewId();

    public XmlUnsignedInt xgetWorkbookViewId();

    public void setWorkbookViewId(long var1);

    public void xsetWorkbookViewId(XmlUnsignedInt var1);
}

