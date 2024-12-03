/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetPr
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetProtection
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetViews
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCsPageSetup
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomChartsheetViews
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawingHF
 *  org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishItems
 */
package org.openxmlformats.schemas.spreadsheetml.x2006.main;

import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetPr;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetProtection;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTChartsheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCsPageSetup;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCustomChartsheetViews;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDrawingHF;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTExtensionList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTHeaderFooter;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTLegacyDrawing;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTPageMargins;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheetBackgroundPicture;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTWebPublishItems;

public interface CTChartsheet
extends XmlObject {
    public static final DocumentFactory<CTChartsheet> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctchartsheetf68atype");
    public static final SchemaType type = Factory.getType();

    public CTChartsheetPr getSheetPr();

    public boolean isSetSheetPr();

    public void setSheetPr(CTChartsheetPr var1);

    public CTChartsheetPr addNewSheetPr();

    public void unsetSheetPr();

    public CTChartsheetViews getSheetViews();

    public void setSheetViews(CTChartsheetViews var1);

    public CTChartsheetViews addNewSheetViews();

    public CTChartsheetProtection getSheetProtection();

    public boolean isSetSheetProtection();

    public void setSheetProtection(CTChartsheetProtection var1);

    public CTChartsheetProtection addNewSheetProtection();

    public void unsetSheetProtection();

    public CTCustomChartsheetViews getCustomSheetViews();

    public boolean isSetCustomSheetViews();

    public void setCustomSheetViews(CTCustomChartsheetViews var1);

    public CTCustomChartsheetViews addNewCustomSheetViews();

    public void unsetCustomSheetViews();

    public CTPageMargins getPageMargins();

    public boolean isSetPageMargins();

    public void setPageMargins(CTPageMargins var1);

    public CTPageMargins addNewPageMargins();

    public void unsetPageMargins();

    public CTCsPageSetup getPageSetup();

    public boolean isSetPageSetup();

    public void setPageSetup(CTCsPageSetup var1);

    public CTCsPageSetup addNewPageSetup();

    public void unsetPageSetup();

    public CTHeaderFooter getHeaderFooter();

    public boolean isSetHeaderFooter();

    public void setHeaderFooter(CTHeaderFooter var1);

    public CTHeaderFooter addNewHeaderFooter();

    public void unsetHeaderFooter();

    public CTDrawing getDrawing();

    public void setDrawing(CTDrawing var1);

    public CTDrawing addNewDrawing();

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

    public CTSheetBackgroundPicture getPicture();

    public boolean isSetPicture();

    public void setPicture(CTSheetBackgroundPicture var1);

    public CTSheetBackgroundPicture addNewPicture();

    public void unsetPicture();

    public CTWebPublishItems getWebPublishItems();

    public boolean isSetWebPublishItems();

    public void setWebPublishItems(CTWebPublishItems var1);

    public CTWebPublishItems addNewWebPublishItems();

    public void unsetWebPublishItems();

    public CTExtensionList getExtLst();

    public boolean isSetExtLst();

    public void setExtLst(CTExtensionList var1);

    public CTExtensionList addNewExtLst();

    public void unsetExtLst();
}

