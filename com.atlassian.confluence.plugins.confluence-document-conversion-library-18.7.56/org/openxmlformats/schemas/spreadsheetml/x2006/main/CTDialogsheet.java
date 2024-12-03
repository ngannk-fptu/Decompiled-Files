/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawingHF
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTControls;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomSheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawingHF;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTOleObjects;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPrintOptions;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetFormatPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetViews;

public interface CTDialogsheet
extends XmlObject {
    public static final DocumentFactory<CTDialogsheet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctdialogsheet6f36type");
    public static final SchemaType type = Factory.getType();

    public CTSheetPr getSheetPr();

    public boolean isSetSheetPr();

    public void setSheetPr(CTSheetPr var1);

    public CTSheetPr addNewSheetPr();

    public void unsetSheetPr();

    public CTSheetViews getSheetViews();

    public boolean isSetSheetViews();

    public void setSheetViews(CTSheetViews var1);

    public CTSheetViews addNewSheetViews();

    public void unsetSheetViews();

    public CTSheetFormatPr getSheetFormatPr();

    public boolean isSetSheetFormatPr();

    public void setSheetFormatPr(CTSheetFormatPr var1);

    public CTSheetFormatPr addNewSheetFormatPr();

    public void unsetSheetFormatPr();

    public CTSheetProtection getSheetProtection();

    public boolean isSetSheetProtection();

    public void setSheetProtection(CTSheetProtection var1);

    public CTSheetProtection addNewSheetProtection();

    public void unsetSheetProtection();

    public CTCustomSheetViews getCustomSheetViews();

    public boolean isSetCustomSheetViews();

    public void setCustomSheetViews(CTCustomSheetViews var1);

    public CTCustomSheetViews addNewCustomSheetViews();

    public void unsetCustomSheetViews();

    public CTPrintOptions getPrintOptions();

    public boolean isSetPrintOptions();

    public void setPrintOptions(CTPrintOptions var1);

    public CTPrintOptions addNewPrintOptions();

    public void unsetPrintOptions();

    public CTPageMargins getPageMargins();

    public boolean isSetPageMargins();

    public void setPageMargins(CTPageMargins var1);

    public CTPageMargins addNewPageMargins();

    public void unsetPageMargins();

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

    public CTDrawing getDrawing();

    public boolean isSetDrawing();

    public void setDrawing(CTDrawing var1);

    public CTDrawing addNewDrawing();

    public void unsetDrawing();

    public CTLegacyDrawing getLegacyDrawing();

    public boolean isSetLegacyDrawing();

    public void setLegacyDrawing(CTLegacyDrawing var1);

    public CTLegacyDrawing addNewLegacyDrawing();

    public void unsetLegacyDrawing();

    public CTLegacyDrawing getLegacyDrawingHF();

    public boolean isSetLegacyDrawingHF();

    public void setLegacyDrawingHF(CTLegacyDrawing var1);

    public CTLegacyDrawing addNewLegacyDrawingHF();

    public void unsetLegacyDrawingHF();

    public CTDrawingHF getDrawingHF();

    public boolean isSetDrawingHF();

    public void setDrawingHF(CTDrawingHF var1);

    public CTDrawingHF addNewDrawingHF();

    public void unsetDrawingHF();

    public CTOleObjects getOleObjects();

    public boolean isSetOleObjects();

    public void setOleObjects(CTOleObjects var1);

    public CTOleObjects addNewOleObjects();

    public void unsetOleObjects();

    public CTControls getControls();

    public boolean isSetControls();

    public void setControls(CTControls var1);

    public CTControls addNewControls();

    public void unsetControls();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

